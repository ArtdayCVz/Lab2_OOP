package services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import exceptions.PlaneProcessingException;
import support.TestFiles;

class XsltTransformerTest {
    @Test
    void planesAreGroupedByType() throws Exception {
        Path out = Files.createTempDirectory("xslt").resolve("nested/out.xml");
        XsltTransformer.transform(TestFiles.XML, TestFiles.XSL, out);

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(out.toFile());
        assertEquals("PlanesByType", doc.getDocumentElement().getTagName());

        NodeList groups = doc.getElementsByTagName("Group");
        assertEquals(5, groups.getLength());
        assertEquals("escort", ((Element) groups.item(0)).getAttribute("type"));

        int planes = 0;
        for (int i = 0; i < groups.getLength(); i++) {
            Element group = (Element) groups.item(i);
            int inGroup = group.getElementsByTagName("Plane").getLength();
            assertEquals(Integer.parseInt(group.getAttribute("count")), inGroup);
            planes += inGroup;
        }
        assertEquals(8, planes);
        assertEquals(0, doc.getElementsByTagName("Type").getLength());
    }

    @Test
    void outputInCurrentDirectoryIsAllowed() throws IOException {
        Path out = Path.of("target-xslt-test.xml");
        try {
            XsltTransformer.transform(TestFiles.XML, TestFiles.XSL, out);
            assertEquals(true, Files.exists(out));
        } finally {
            Files.deleteIfExists(out);
        }
    }

    @Test
    void missingStylesheetThrows() {
        assertThrows(PlaneProcessingException.class,
                () -> XsltTransformer.transform(TestFiles.XML, Path.of("missing.xsl"), Path.of("target/x.xml")));
    }
}
