package parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import exceptions.PlaneProcessingException;
import support.TestFiles;
import models.Ammunition;
import models.Plane;
import models.PlaneKind;

class PlaneParsersTest {
    private static final List<PlaneParser> PARSERS =
            List.of(new SaxPlaneParser(), new DomPlaneParser(), new StaxPlaneParser());

    @Test
    void allParsersReadAllPlanes() {
        for (PlaneParser parser : PARSERS) {
            assertEquals(8, parser.parse(TestFiles.XML).size(), parser.getClass().getSimpleName());
        }
    }

    @Test
    void allParsersProduceIdenticalResults() {
        List<Plane> expected = PARSERS.get(0).parse(TestFiles.XML);
        for (PlaneParser parser : PARSERS) {
            assertEquals(expected, parser.parse(TestFiles.XML), parser.getClass().getSimpleName());
        }
    }

    @Test
    void armedPlaneIsMappedCompletely() {
        for (PlaneParser parser : PARSERS) {
            Plane plane = parser.parse(TestFiles.XML).get(3);
            assertEquals("P004", plane.id());
            assertEquals("Ilyushin Il-2", plane.model());
            assertEquals("USSR", plane.origin());
            assertEquals(PlaneKind.SUPPORT, plane.chars().kind());
            assertEquals(2, plane.chars().seats());
            assertEquals(new Ammunition(8, false), plane.chars().ammunition());
            assertEquals(11.65, plane.parameters().length());
            assertEquals(14.60, plane.parameters().width());
            assertEquals(4.17, plane.parameters().height());
            assertEquals(new BigDecimal("6300.00"), plane.price());
        }
    }

    @Test
    void reconPlaneHasNoAmmunition() {
        for (PlaneParser parser : PARSERS) {
            Plane recon = parser.parse(TestFiles.XML).get(6);
            assertEquals(PlaneKind.RECON, recon.chars().kind());
            assertNull(recon.chars().ammunition());
            assertFalse(recon.chars().isArmed());
        }
    }

    @Test
    void radarFlagIsParsed() throws IOException {
        Path file = TestFiles.mutated("<Radar>true</Radar>", "<Radar>false</Radar>");
        for (PlaneParser parser : PARSERS) {
            assertFalse(parser.parse(file).get(7).chars().ammunition().radar());
            assertTrue(parser.parse(TestFiles.XML).get(7).chars().ammunition().radar());
        }
    }

    @Test
    void malformedXmlIsRejected() throws IOException {
        Path file = TestFiles.write("<Planes><Plane id=\"P001\"></Planes>");
        for (PlaneParser parser : PARSERS) {
            assertThrows(PlaneProcessingException.class, () -> parser.parse(file));
        }
    }

    @Test
    void missingFileIsRejected() {
        Path file = Path.of("no-such-file.xml");
        for (PlaneParser parser : PARSERS) {
            assertThrows(PlaneProcessingException.class, () -> parser.parse(file));
        }
    }

    @Test
    void invalidNumberIsRejected() throws IOException {
        Path file = TestFiles.mutated("<Seats>1</Seats>", "<Seats>one</Seats>");
        for (PlaneParser parser : PARSERS) {
            assertThrows(PlaneProcessingException.class, () -> parser.parse(file));
        }
    }

    @Test
    void missingIdIsRejected() throws IOException {
        Path file = TestFiles.mutated("<Plane id=\"P001\">", "<Plane>");
        for (PlaneParser parser : PARSERS) {
            assertThrows(PlaneProcessingException.class, () -> parser.parse(file));
        }
    }

    @Test
    void missingRequiredElementIsRejected() throws IOException {
        Path file = TestFiles.mutated("<Origin>United Kingdom</Origin>", "");
        for (PlaneParser parser : PARSERS) {
            assertThrows(PlaneProcessingException.class, () -> parser.parse(file));
        }
    }
}
