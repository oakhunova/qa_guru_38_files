import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;


public class JsonFileTest {
    private final ClassLoader cl = JsonFileTest.class.getClassLoader();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void jsonFileParsingTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("JsonTestData")) {
            assertNotNull(is, "JSON-файл не найден");

            TestDataWrapper wrapper = objectMapper.readValue(is, TestDataWrapper.class);
            TestData testData = wrapper.getTestData();

            assertAll("Проверка JSON данных",
                    () -> assertEquals("QA Automation", testData.getProject()),
                    () -> assertEquals("Ольга Ахунова", testData.getTester()),
                    () -> assertEquals(5, testData.getExperience()),
                    () -> assertFalse(testData.isCertified()),
                    () -> assertTrue(testData.getSkills().contains("Java")),
                    () -> assertTrue(testData.getSkills().contains("Selenide"))
            );

            System.out.println("JSON-файл успешно прочитан и проверен");
        }
    }
}