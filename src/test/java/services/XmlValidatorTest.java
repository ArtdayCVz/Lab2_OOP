package services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import exceptions.PlaneProcessingException;
import support.TestFiles;

class XmlValidatorTest {
    private static boolean isInvalid(Path xml) {
        return !XmlValidator.validate(xml, TestFiles.XSD).isEmpty();
    }

    @Test
    void validDocumentHasNoErrors() {
        assertTrue(XmlValidator.validate(TestFiles.XML, TestFiles.XSD).isEmpty());
    }

    @Test
    void missilesAboveLimitAreRejected() throws IOException {
        assertTrue(isInvalid(TestFiles.mutated("<Missiles>8</Missiles>", "<Missiles>11</Missiles>")));
    }

    @Test
    void missilesBelowLimitAreRejected() throws IOException {
        assertTrue(isInvalid(TestFiles.mutated("<Missiles>8</Missiles>", "<Missiles>-1</Missiles>")));
    }

    @Test
    void seatsOutsideEnumerationAreRejected() throws IOException {
        assertTrue(isInvalid(TestFiles.mutated("<Seats>1</Seats>", "<Seats>3</Seats>")));
    }

    @Test
    void unknownTypeIsRejected() throws IOException {
        assertTrue(isInvalid(TestFiles.mutated("<Type>fighter</Type>", "<Type>bomber</Type>")));
    }

    @Test
    void duplicateIdIsRejected() throws IOException {
        assertTrue(isInvalid(TestFiles.mutated("id=\"P002\"", "id=\"P001\"")));
    }

    @Test
    void idWithWrongPatternIsRejected() throws IOException {
        assertTrue(isInvalid(TestFiles.mutated("id=\"P001\"", "id=\"X1\"")));
    }

    @Test
    void missingIdAttributeIsRejected() throws IOException {
        assertTrue(isInvalid(TestFiles.mutated("<Plane id=\"P001\">", "<Plane>")));
    }

    @Test
    void wrongCurrencyIsRejected() throws IOException {
        assertTrue(isInvalid(TestFiles.mutated("currency=\"thaler\"", "currency=\"euro\"")));
    }

    @Test
    void negativeDimensionIsRejected() throws IOException {
        assertTrue(isInvalid(TestFiles.mutated("<Length>9.54</Length>", "<Length>-1</Length>")));
    }

    @Test
    void malformedXmlIsReportedAsError() throws IOException {
        Path file = TestFiles.write("<Planes>");
        assertFalse(XmlValidator.validate(file, TestFiles.XSD).isEmpty());
    }

    @Test
    void brokenSchemaThrows() throws IOException {
        Path brokenXsd = TestFiles.write("<xs:schema");
        assertThrows(PlaneProcessingException.class, () -> XmlValidator.validate(TestFiles.XML, brokenXsd));
    }

    @Test
    void missingXmlFileThrows() {
        assertThrows(PlaneProcessingException.class,
                () -> XmlValidator.validate(Path.of("missing.xml"), TestFiles.XSD));
    }
}
