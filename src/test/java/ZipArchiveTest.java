import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import org.junit.jupiter.api.*;
import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
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
        boolean csvFileFound = false;
        try (ZipInputStream zis = new ZipInputStream(
                Objects.requireNonNull(cl.getResourceAsStream("Архив.zip")))
        ) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if ("Книга1.csv".equals(entry.getName())) {
                    csvFileFound = true;
                    byte[] csvContent = zis.readAllBytes();
                    try (CSVReader csvReader = new CSVReaderBuilder(
                            new InputStreamReader(new ByteArrayInputStream(csvContent), StandardCharsets.UTF_8))
                            .withCSVParser(new CSVParserBuilder()
                                    .withSeparator(';')
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
                        System.out.println("Количество строк: " + data.size());
                        }
                    break;
                    }
                }
            }
        assertTrue(csvFileFound, "CSV файл не найден в архиве");
        }
    @Test
    void pdfFileFromZipParsingTest() throws Exception {
        boolean pdfFileFound = false;
        try (ZipInputStream zis = new ZipInputStream(
                Objects.requireNonNull(cl.getResourceAsStream("Архив.zip")))
        ) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if ("price2025.pdf".equals(entry.getName())) {
                    pdfFileFound = true;
                    PDF pdf = new PDF(zis);

                    assertAll("Проверка PDF файла",
                            () -> assertTrue(pdf.text.contains("ПРЕЙСКУРАНТ"),
                                    "Файл должен содержать заголовок 'ПРЕЙСКУРАНТ'"),
                            () -> assertTrue(pdf.text.contains("Цена"),
                                    "Файл должен содержать слово 'Цена'"),
                            () -> assertTrue(pdf.numberOfPages > 0,
                                    "Файл должен содержать страницы")
                    );

                    System.out.println("PDF файл успешно прочитан и проверен");
                    System.out.println("Количество страниц: " + pdf.numberOfPages);
                    break;
                }
            }
        }
        assertTrue(pdfFileFound, "PDF файл не найден в архиве");
    }
    @Test
    void xlsxFileFromZipParsingTest() throws Exception {
        boolean xlsxFileFound = false;
        try (ZipInputStream zis = new ZipInputStream(
                Objects.requireNonNull(cl.getResourceAsStream("Архив.zip")))
        ) {
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                if ("Тестовое задание №1 (после p2p).xlsx".equals(entry.getName())) {
                    xlsxFileFound = true;
                    XLS xls = new XLS(zis);
                    String deviceType = xls.excel.getSheetAt(0).getRow(5).getCell(4).getStringCellValue();
                    String nameQa = xls.excel.getSheetAt(0).getRow(2).getCell(3).getStringCellValue();

                    assertAll("Проверка XLSX файла",
                            () -> assertTrue(xls.excel.getNumberOfSheets() > 0,
                                    "Файл должен содержать листы"),
                            () -> assertTrue(xls.excel.getSheetAt(0).getPhysicalNumberOfRows() > 0,
                                    "Файл должен содержать строки"),
                            () -> assertTrue(xls.excel.getSheetAt(0).getRow(0).getPhysicalNumberOfCells() > 0,
                                    "Файл должен содержать колонки"),
                            () -> assertTrue(deviceType.contains("Эмулятор"),
                                    "Должен быть указан тип устройства 'Эмулятор'"),
                            () -> assertTrue(nameQa.contains("Ахунова Ольга Маратовна"),
                                    "Должно быть указано ФИО 'Ахунова Ольга Маратовна'"));

                    System.out.println("XLSX файл успешно прочитан и проверен");
                    System.out.println("Тип устройства: " + deviceType);
                    System.out.println("Должность тестировщика: " + nameQa);
                    System.out.println("Количество листов: " + xls.excel.getNumberOfSheets());
                    break;
                }
            }
        }
        assertTrue(xlsxFileFound, "XLSX файл не найден в архиве");
    }
}
