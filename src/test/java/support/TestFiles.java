package support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestFiles {
    public static final Path XML = Path.of("src/main/resources/xml/planes.xml");
    public static final Path XSD = Path.of("src/main/resources/xml/planes.xsd");
    public static final Path XSL = Path.of("src/main/resources/xml/planes-by-type.xsl");

    private TestFiles() {
    }

    public static Path mutated(String from, String to) throws IOException {
        String content = Files.readString(XML, StandardCharsets.UTF_8);
        if (!content.contains(from)) {
            throw new IllegalArgumentException("Test setup error, text not found: " + from);
        }
        return write(content.replaceFirst(java.util.regex.Pattern.quote(from),
                java.util.regex.Matcher.quoteReplacement(to)));
    }

    public static Path write(String content) throws IOException {
        Path file = Files.createTempFile("planes-test", ".xml");
        file.toFile().deleteOnExit();
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }
}
