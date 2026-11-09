package parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import exceptions.PlaneProcessingException;
import models.Plane;

class PlaneAssemblerTest {
    private static PlaneAssembler filled(String id) {
        PlaneAssembler a = new PlaneAssembler(id);
        a.accept(XmlTags.MODEL, " X-1 ");
        a.accept(XmlTags.ORIGIN, "Nowhere");
        a.accept(XmlTags.TYPE, "escort");
        a.accept(XmlTags.SEATS, "2");
        a.accept(XmlTags.LENGTH, "10");
        a.accept(XmlTags.WIDTH, "5");
        a.accept(XmlTags.HEIGHT, "3");
        a.accept(XmlTags.PRICE, "100.50");
        return a;
    }

    @Test
    void buildsPlaneAndTrimsText() {
        Plane plane = filled("P100").build();
        assertEquals("X-1", plane.model());
        assertEquals(2, plane.chars().seats());
    }

    @Test
    void containerTagsAreIgnored() {
        PlaneAssembler a = filled("P100");
        a.accept("Chars", "   \n  ");
        assertEquals("P100", a.build().id());
    }

    @Test
    void radarAcceptsNumericTrue() {
        PlaneAssembler a = filled("P100");
        a.accept(XmlTags.MISSILES, "2");
        a.accept(XmlTags.RADAR, "1");
        assertTrue(a.build().chars().ammunition().radar());
    }

    @Test
    void blankOrNullIdIsRejected() {
        assertThrows(PlaneProcessingException.class, () -> filled(null).build());
        assertThrows(PlaneProcessingException.class, () -> filled(" ").build());
    }

    @Test
    void incompletePlaneIsRejected() {
        assertThrows(PlaneProcessingException.class, () -> new PlaneAssembler("P101").build());
    }

    @Test
    void badValueIsRejected() {
        PlaneAssembler a = new PlaneAssembler("P102");
        assertThrows(PlaneProcessingException.class, () -> a.accept(XmlTags.PRICE, "free"));
        assertThrows(PlaneProcessingException.class, () -> a.accept(XmlTags.TYPE, "bomber"));
    }
}
