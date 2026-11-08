package parsers;

import static parsers.XmlTags.PLANE;

import java.util.ArrayList;
import java.util.List;

import models.Plane;

final class PlaneEventCollector {
    private final List<Plane> planes = new ArrayList<>();
    private final StringBuilder text = new StringBuilder();
    private PlaneAssembler current;

    void start(String name, String idAttribute) {
        text.setLength(0);
        if (PLANE.equals(name)) {
            current = new PlaneAssembler(idAttribute);
        }
    }

    void chars(CharSequence characters) {
        text.append(characters);
    }

    void end(String name) {
        if (current == null) {
            return;
        }
        if (PLANE.equals(name)) {
            planes.add(current.build());
            current = null;
        } else {
            current.accept(name, text.toString());
        }
    }

    List<Plane> planes() {
        return planes;
    }
}
