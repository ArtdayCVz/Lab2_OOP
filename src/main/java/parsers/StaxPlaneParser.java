package parsers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import exceptions.PlaneProcessingException;
import models.Plane;

public class StaxPlaneParser implements PlaneParser {
    private static final Logger LOG = Logger.getLogger(StaxPlaneParser.class.getName());

    @Override
    public List<Plane> parse(Path xml) {
        PlaneEventCollector collector = new PlaneEventCollector();
        XMLInputFactory factory = XMLInputFactory.newFactory();
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        try (InputStream in = Files.newInputStream(xml)) {
            XMLStreamReader reader = factory.createXMLStreamReader(in);
            try {
                while (reader.hasNext()) {
                    switch (reader.next()) {
                        case XMLStreamReader.START_ELEMENT ->
                                collector.start(reader.getLocalName(), reader.getAttributeValue(null, XmlTags.ID_ATTR));
                        case XMLStreamReader.CHARACTERS -> collector.chars(reader.getText());
                        case XMLStreamReader.END_ELEMENT -> collector.end(reader.getLocalName());
                        default -> {  }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (XMLStreamException | IOException e) {
            throw new PlaneProcessingException("StAX parsing failed: " + xml, e);
        }
        LOG.fine(() -> "StAX parsed " + collector.planes().size() + " planes");
        return collector.planes();
    }
}
