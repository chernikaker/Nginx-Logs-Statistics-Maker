package backend.academy.project.readers;

import backend.academy.project.readers.exception.EmptyOrNullPathException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class LogsReaderCreatorTest {

    @ParameterizedTest
    @CsvSource({
        "http://example.com/logs",
        "https://localhost:4200/logs",
        "https://raw.githubusercontent.com/elastic/examples/master/Common%20Data%20Formats/nginx_logs/nginx_logs"
    })
    public void validURLPathTest(String validPath) {
        LogsReader reader = assertDoesNotThrow(() -> LogsReaderCreator.getReaderByPath(validPath));
        assertInstanceOf(UrlLogsReader.class, reader);
    }

    @ParameterizedTest
    @CsvSource({"/path/to/local/file.log", "logs/*", "*.txt"})
    public void validLocalPathTest(String validPath) {
        LogsReader reader = assertDoesNotThrow(() -> LogsReaderCreator.getReaderByPath(validPath));
        assertInstanceOf(LocalFileLogsReader.class, reader);
    }

    @ParameterizedTest
    @CsvSource({"//example.com/logs", "htp://example.com/logs"})
    public void getDefaultReaderWhenURLIsInvalidTest(String validPath) {
        LogsReader reader = assertDoesNotThrow(() -> LogsReaderCreator.getReaderByPath(validPath));
        assertInstanceOf(LocalFileLogsReader.class, reader);
    }

    @Test
    public void invalidEmptyPathTest() {
        String emptyPath = "";
        assertThatThrownBy(() -> LogsReaderCreator.getReaderByPath(emptyPath))
            .isInstanceOf(EmptyOrNullPathException.class)
            .hasMessageContaining("Path cannot be null or blank");
    }

    @Test
    public void testGetReaderByPath_NullPath() {
        String nullPath = null;
        assertThatThrownBy(() -> LogsReaderCreator.getReaderByPath(nullPath))
            .isInstanceOf(EmptyOrNullPathException.class)
            .hasMessageContaining("Path cannot be null or blank");
    }
}
