package app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import support.TestFiles;

class MainTest {
    @Test
    void validInputProducesGroupedFileAndExitCodeZero() throws IOException {
        Path out = Files.createTempFile("grouped", ".xml");
        out.toFile().deleteOnExit();
        int code = Main.run(new String[] {
            TestFiles.XML.toString(), TestFiles.XSD.toString(), TestFiles.XSL.toString(), out.toString()});
        assertEquals(0, code);
        assertTrue(Files.readString(out).contains("PlanesByType"));
    }

    @Test
    void defaultArgumentsAreUsedWhenNoneGiven() {
        assertEquals(0, Main.run(new String[0]));
        assertEquals(0, Main.run(null));
    }

    @Test
    void invalidXmlGivesExitCodeOne() throws IOException {
        Path bad = TestFiles.mutated("<Missiles>8</Missiles>", "<Missiles>11</Missiles>");
        int code = Main.run(new String[] {bad.toString()});
        assertEquals(1, code);
    }

    @Test
    void processingErrorGivesExitCodeOne() {
        int code = Main.run(new String[] {
            TestFiles.XML.toString(), TestFiles.XSD.toString(), "does-not-exist.xsl"});
        assertEquals(1, code);
    }
}
