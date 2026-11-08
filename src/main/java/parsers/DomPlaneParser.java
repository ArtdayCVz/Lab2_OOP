package parsers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import exceptions.PlaneProcessingException;
import models.Plane;

public class DomPlaneParser implements PlaneParser {
    private static final Logger LOG = Logger.getLogger(DomPlaneParser.class.getName());

    @Override
    public List<Plane> parse(Path xml) {
        PlaneEventCollector collector = new PlaneEventCollector();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Document document = factory.newDocumentBuilder().parse(xml.toFile());
            walk(document.getDocumentElement(), collector);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new PlaneProcessingException("DOM parsing failed: " + xml, e);
        }
        LOG.fine(() -> "DOM parsed " + collector.planes().size() + " planes");
        return collector.planes();
    }

    private void walk(Element element, PlaneEventCollector collector) {
        collector.start(element.getTagName(), element.getAttribute(XmlTags.ID_ATTR));
        for (Node child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                walk((Element) child, collector);
            } else if (child.getNodeType() == Node.TEXT_NODE) {
                collector.chars(child.getNodeValue());
            }
        }
        collector.end(element.getTagName());
    }
}
