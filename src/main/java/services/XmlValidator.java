package services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import exceptions.PlaneProcessingException;

public final class XmlValidator {
    private XmlValidator() {
    }

    public static List<String> validate(Path xml, Path xsd) {
        Schema schema = loadSchema(xsd);
        List<String> errors = new ArrayList<>();
        try {
            Validator validator = schema.newValidator();
            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException e) {
                }

                @Override
                public void error(SAXParseException e) {
                    errors.add("line " + e.getLineNumber() + ": " + e.getMessage());
                }

                @Override
                public void fatalError(SAXParseException e) throws SAXException {
                    throw e;
                }
            });
            validator.validate(new StreamSource(xml.toFile()));
        } catch (SAXParseException e) {
            errors.add("line " + e.getLineNumber() + ": " + e.getMessage());
        } catch (SAXException | IOException e) {
            throw new PlaneProcessingException("Cannot read file: " + xml, e);
        }
        return errors;
    }

    private static Schema loadSchema(Path xsd) {
        try {
            return SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(xsd.toFile());
        } catch (SAXException e) {
            throw new PlaneProcessingException("Cannot load schema: " + xsd, e);
        }
    }
}
