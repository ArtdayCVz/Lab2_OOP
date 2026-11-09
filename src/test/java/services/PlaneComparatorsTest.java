package services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import support.TestFiles;
import models.Plane;
import parsers.SaxPlaneParser;

class PlaneComparatorsTest {
    private static List<String> ids(java.util.Comparator<Plane> comparator) {
        List<Plane> planes = new ArrayList<>(new SaxPlaneParser().parse(TestFiles.XML));
        planes.sort(comparator);
        return planes.stream().map(Plane::id).toList();
    }

    @Test
    void sortsByModelIgnoringCase() {
        assertEquals(List.of("P008", "P007", "P006", "P004", "P002", "P005", "P003", "P001"),
                ids(PlaneComparators.BY_MODEL));
    }

    @Test
    void sortsByPriceAscending() {
        assertEquals(List.of("P007", "P004", "P002", "P001", "P006", "P003", "P008", "P005"),
                ids(PlaneComparators.BY_PRICE));
    }

    @Test
    void sortsByOriginThenModel() {
        assertEquals(List.of("P007", "P002", "P005", "P003", "P004", "P008", "P006", "P001"),
                ids(PlaneComparators.BY_ORIGIN_THEN_MODEL));
    }

    @Test
    void sortsByKindThenPriceDescending() {
        assertEquals(List.of("P006", "P004", "P003", "P001", "P002", "P005", "P008", "P007"),
                ids(PlaneComparators.BY_KIND_THEN_PRICE_DESC));
    }
}
