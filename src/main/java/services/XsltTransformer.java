package services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import exceptions.PlaneProcessingException;

public final class XsltTransformer {
    private XsltTransformer() {
    }

    public static void transform(Path xml, Path xsl, Path output) {
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = factory.newTransformer(new StreamSource(xsl.toFile()));
            Path parent = output.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            transformer.transform(new StreamSource(xml.toFile()), new StreamResult(output.toFile()));
        } catch (TransformerException | IOException e) {
            throw new PlaneProcessingException("XSLT transformation failed: " + xml, e);
        }
    }
}
