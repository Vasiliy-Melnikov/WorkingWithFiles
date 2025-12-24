import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ZipResourcesTest {

    private static final String ZIP_PATH = "files/archive.zip";

    private Map<String, byte[]> readZipEntries() throws IOException {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(ZIP_PATH)) {
            assertThat(is)
                    .as("archive.zip must exist in src/test/resources/files")
                    .isNotNull();

            Map<String, byte[]> result = new HashMap<>();

            try (ZipInputStream zis = new ZipInputStream(is)) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if (entry.isDirectory()) continue;

                    byte[] bytes = readAllBytes(zis);
                    result.put(entry.getName(), bytes);
                    zis.closeEntry();
                }
            }
            return result;
        }
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = is.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            return bos.toByteArray();
        }
    }

    @Test
    void pdfFromZipContainsExpectedText() throws Exception {
        Map<String, byte[]> entries = readZipEntries();

        byte[] pdfBytes = entries.get("doc.pdf");
        assertThat(pdfBytes).as("doc.pdf must exist in archive.zip").isNotNull();

        PDF pdf = new PDF(new ByteArrayInputStream(pdfBytes));

        assertThat(pdf.text)
                .contains("This manual provides information about the hardware and software")
                .contains("features of your ROG Handheld Console, organized through the")
                .contains("following chapters.");git
    }

    @Test
    void xlsxFromZipHasExpectedCellValues() throws Exception {
        Map<String, byte[]> entries = readZipEntries();

        byte[] xlsxBytes = entries.get("table.xlsx");
        assertThat(xlsxBytes).as("table.xlsx must exist in archive.zip").isNotNull();

        XLS xls = new XLS(new ByteArrayInputStream(xlsxBytes));

        assertThat(xls.excel.getNumberOfSheets()).isGreaterThan(0);

        String a1 = xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
        assertThat(a1).isNotBlank();
    }

    @Test
    void csvFromZipContainsProperties() throws Exception {
        Map<String, byte[]> entries = readZipEntries();

        byte[] csvBytes = entries.get("data.csv");
        assertThat(csvBytes).as("data.csv must exist in archive.zip").isNotNull();

        String csvText = new String(csvBytes, StandardCharsets.UTF_8);

        assertThat(csvText)
                .contains("Property,Specification")
                .contains("Model,Asus ROG Ally Z1 Extreme (2023)")
                .contains("RAM,16 GB LPDDR5 (6400 MT/s)")
                .contains("Display Refresh Rate,120 Hz");
    }
}
