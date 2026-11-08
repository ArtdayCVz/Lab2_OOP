package models;

public record Chars(PlaneKind kind, int seats, Ammunition ammunition) {
    public boolean isArmed() {
        return ammunition != null;
    }
}
