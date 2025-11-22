import com.codeborne.selenide.Configuration;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import org.junit.jupiter.api.*;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVReader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public class ZipArchiveTest {
    private final ClassLoader cl = ZipArchiveTest.class.getClassLoader();

    @Test
    void zipFileParsingTest() throws Exception {
        List<String> fileNames = new ArrayList<>();
        try (ZipInputStream zis = new ZipInputStream(
                Objects.requireNonNull(cl.getResourceAsStream("Архив.zip"))
        )) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                String fileName = entry.getName();
                fileNames.add(fileName);
                System.out.println("Найден файл: " + fileName);
            }
        }
        assertFalse(fileNames.isEmpty(), "Архив не должен быть пустым");
        }

    @Test
    void csvFileFromZipParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                Objects.requireNonNull(cl.getResourceAsStream("Архив.zip")))
        ) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if ("Книга1.csv".equals(entry.getName())) {

                    try (CSVReader csvReader = new CSVReaderBuilder(
                            new InputStreamReader(zis, StandardCharsets.UTF_8))
                            .withCSVParser(new CSVParserBuilder()
                                    .withSeparator(';')  // ← ВАЖНО: указываем разделитель
                                    .build())
                            .build()) {
                        List<String[]> data = csvReader.readAll();

                        assertAll("Проверка CSV файла",
                                () -> assertEquals(2, data.size(), "Должно быть 2 строки"),
                                () -> assertArrayEquals(
                                        new String[] {"Selenide", "https://selenide.org"},
                                        data.get(0),
                                        "Первая строка должна содержать Selenide и URL"
                                ),
                                () -> assertArrayEquals(
                                        new String[] {"JUnit 5", "https://junit.org"},
                                        data.get(1),
                                        "Вторая строка должна содержать JUnit 5 и URL"
                                )
                        );
                        System.out.println("CSV файл успешно прочитан и проверен");
                        }
                    }
                else
                    break;
                }
            }
        }
    }
