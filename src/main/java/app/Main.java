package app;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import exceptions.PlaneProcessingException;
import models.Plane;
import parsers.DomPlaneParser;
import parsers.PlaneParser;
import parsers.SaxPlaneParser;
import parsers.StaxPlaneParser;
import services.PlaneComparators;
import services.XmlValidator;
import services.XsltTransformer;

public final class Main {
    private static final Logger LOG = Logger.getLogger(Main.class.getName());

    private static final String[] DEFAULTS = {
        "src/main/resources/xml/planes.xml",
        "src/main/resources/xml/planes.xsd",
        "src/main/resources/xml/planes-by-type.xsl",
        "target/planes-by-type.xml"
    };

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        configureLogging();
        int code = run(args);
        if (code != 0) {
            System.exit(code);
        }
    }

    public static int run(String[] args) {
        Path xml = Path.of(arg(args, 0));
        Path xsd = Path.of(arg(args, 1));
        Path xsl = Path.of(arg(args, 2));
        Path output = Path.of(arg(args, 3));

        try {
            List<String> errors = XmlValidator.validate(xml, xsd);
            if (!errors.isEmpty()) {
                LOG.severe(() -> xml + " is NOT valid against " + xsd);
                errors.forEach(LOG::severe);
                return 1;
            }
            LOG.info(() -> xml + " is valid against " + xsd);

            Map<String, PlaneParser> parsers = new LinkedHashMap<>();
            parsers.put("SAX", new SaxPlaneParser());
            parsers.put("DOM", new DomPlaneParser());
            parsers.put("StAX", new StaxPlaneParser());

            parsers.forEach((name, parser) -> {
                List<Plane> planes = parser.parse(xml);
                planes.sort(PlaneComparators.BY_PRICE);
                LOG.info(() -> name + ": " + planes.size() + " planes sorted by price");
                planes.forEach(p -> LOG.fine(() -> name + " -> " + p));
            });

            XsltTransformer.transform(xml, xsl, output);
            LOG.info(() -> "Grouped XML written to " + output);
            return 0;
        } catch (PlaneProcessingException e) {
            LOG.log(java.util.logging.Level.SEVERE, "Processing failed", e);
            return 1;
        }
    }

    private static String arg(String[] args, int index) {
        return args != null && args.length > index ? args[index] : DEFAULTS[index];
    }

    private static void configureLogging() throws IOException {
        Files.createDirectories(Path.of("logs"));
        try (InputStream config = Main.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(config);
        }
    }
}
