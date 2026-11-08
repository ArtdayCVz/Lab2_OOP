package parsers;

import static parsers.XmlTags.*;

import java.math.BigDecimal;

import exceptions.PlaneProcessingException;
import models.Ammunition;
import models.Chars;
import models.Parameters;
import models.Plane;
import models.PlaneKind;

final class PlaneAssembler {
    private final String id;
    private String model;
    private String origin;
    private PlaneKind kind;
    private Integer seats;
    private Integer missiles;
    private boolean radar;
    private Double length;
    private Double width;
    private Double height;
    private BigDecimal price;

    PlaneAssembler(String id) {
        this.id = id;
    }

    void accept(String tag, String rawText) {
        String text = rawText.trim();
        try {
            switch (tag) {
                case MODEL -> model = text;
                case ORIGIN -> origin = text;
                case TYPE -> kind = PlaneKind.fromXml(text);
                case SEATS -> seats = Integer.valueOf(text);
                case MISSILES -> missiles = Integer.valueOf(text);
                case RADAR -> radar = "true".equals(text) || "1".equals(text);
                case LENGTH -> length = Double.valueOf(text);
                case WIDTH -> width = Double.valueOf(text);
                case HEIGHT -> height = Double.valueOf(text);
                case PRICE -> price = new BigDecimal(text);
                default -> {  }
            }
        } catch (IllegalArgumentException e) {
            throw new PlaneProcessingException("Plane " + id + ": invalid value '" + text + "' in <" + tag + ">", e);
        }
    }

    Plane build() {
        if (id == null || id.isBlank()) {
            throw new PlaneProcessingException("Plane without id attribute");
        }
        if (model == null || origin == null || kind == null || seats == null
                || length == null || width == null || height == null || price == null) {
            throw new PlaneProcessingException("Plane " + id + ": a required element is missing");
        }
        Ammunition ammunition = missiles == null ? null : new Ammunition(missiles, radar);
        return new Plane(id, model, origin, new Chars(kind, seats, ammunition),
                new Parameters(length, width, height), price);
    }
}
