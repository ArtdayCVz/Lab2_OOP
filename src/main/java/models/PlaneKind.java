package models;

import java.util.Locale;

public enum PlaneKind {
    SUPPORT, ESCORT, FIGHTER, INTERCEPTOR, RECON;

    public static PlaneKind fromXml(String value) {
        return valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
