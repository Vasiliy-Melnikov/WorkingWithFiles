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

    @Test
    void readFilesFromZipAndValidateContent() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("files/archive.zip")) {
            assertThat(is).as("archive.zip must exist in src/test/resources/files").isNotNull();

            Map<String, byte[]> entries = readZipEntries(is);

            byte[] pdfBytes = entries.get("doc.pdf");
            assertThat(pdfBytes).as("doc.pdf must exist in zip").isNotNull();

            PDF pdf = new PDF(new ByteArrayInputStream(pdfBytes));
            assertThat(pdf.text)
                    .contains("ROG Handheld Console")
                    .contains("hardware and software");
            assertThat(pdf.numberOfPages).isGreaterThan(0);

            byte[] xlsxBytes = entries.get("table.xlsx");
            assertThat(xlsxBytes).as("table.xlsx must exist in zip").isNotNull();
            XLS xls = new XLS(new ByteArrayInputStream(xlsxBytes));
            assertThat(xls.excel.getNumberOfSheets()).isGreaterThan(0);
            String a1 = xls.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
            assertThat(a1).isEqualTo("Category");

            byte[] csvBytes = entries.get("data.csv");
            assertThat(csvBytes).as("data.csv must exist in zip").isNotNull();

            String csvText = new String(csvBytes, StandardCharsets.UTF_8);
            assertThat(csvText)
                    .contains("Property,Specification")
                    .contains("Operating System,Windows 11")
                    .contains("CPU,AMD Ryzen Z1 Extreme")
                    .contains("RAM,16 GB")
                    .contains("Display Refresh Rate,120 Hz");
        }
    }

    private Map<String, byte[]> readZipEntries(InputStream zipStream) throws IOException {
        Map<String, byte[]> result = new HashMap<>();

        try (ZipInputStream zis = new ZipInputStream(zipStream)) {
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
}
