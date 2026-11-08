package parsers;

import java.nio.file.Path;
import java.util.List;

import models.Plane;

public interface PlaneParser {
    List<Plane> parse(Path xml);
}
