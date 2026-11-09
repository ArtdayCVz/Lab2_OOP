package models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ModelTest {
    @Test
    void kindIsParsedFromXmlValue() {
        assertEquals(PlaneKind.FIGHTER, PlaneKind.fromXml("fighter"));
        assertEquals(PlaneKind.RECON, PlaneKind.fromXml("  recon "));
    }

    @Test
    void unknownKindIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> PlaneKind.fromXml("bomber"));
    }

    @Test
    void charsIsArmedOnlyWithAmmunition() {
        assertTrue(new Chars(PlaneKind.FIGHTER, 1, new Ammunition(4, true)).isArmed());
        assertFalse(new Chars(PlaneKind.RECON, 1, null).isArmed());
    }
}
