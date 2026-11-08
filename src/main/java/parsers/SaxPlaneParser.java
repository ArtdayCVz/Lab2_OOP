package parsers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import exceptions.PlaneProcessingException;
import models.Plane;

public class SaxPlaneParser implements PlaneParser {
    private static final Logger LOG = Logger.getLogger(SaxPlaneParser.class.getName());

    @Override
    public List<Plane> parse(Path xml) {
        PlaneEventCollector collector = new PlaneEventCollector();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.newSAXParser().parse(xml.toFile(), new DefaultHandler() {
                @Override
                public void startElement(String uri, String localName, String qName, Attributes attrs) {
                    collector.start(qName, attrs.getValue(XmlTags.ID_ATTR));
                }

                @Override
                public void characters(char[] ch, int start, int length) {
                    collector.chars(new String(ch, start, length));
                }

                @Override
                public void endElement(String uri, String localName, String qName) {
                    collector.end(qName);
                }
            });
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new PlaneProcessingException("SAX parsing failed: " + xml, e);
        }
        LOG.fine(() -> "SAX parsed " + collector.planes().size() + " planes");
        return collector.planes();
    }
}
