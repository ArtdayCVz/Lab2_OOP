package models;

import java.math.BigDecimal;

public record Plane(String id, String model, String origin, Chars chars, Parameters parameters, BigDecimal price) {
}
