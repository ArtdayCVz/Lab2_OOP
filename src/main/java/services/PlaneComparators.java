package services;

import java.util.Comparator;

import models.Plane;

public final class PlaneComparators {
    public static final Comparator<Plane> BY_MODEL =
            Comparator.comparing(Plane::model, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Plane> BY_PRICE = Comparator.comparing(Plane::price);

    public static final Comparator<Plane> BY_ORIGIN_THEN_MODEL =
            Comparator.comparing(Plane::origin).thenComparing(BY_MODEL);

    public static final Comparator<Plane> BY_KIND_THEN_PRICE_DESC =
            Comparator.comparing((Plane p) -> p.chars().kind()).thenComparing(BY_PRICE.reversed());

    private PlaneComparators() {
    }
}
