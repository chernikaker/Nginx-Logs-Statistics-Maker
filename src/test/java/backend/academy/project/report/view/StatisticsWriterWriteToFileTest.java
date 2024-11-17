package backend.academy.project.report.view;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.commandline.FilterFieldType;
import backend.academy.project.logs.RequestType;
import backend.academy.project.report.data.LogInfoReport;
import backend.academy.project.report.view.exception.PathIsNotDirectoryException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class StatisticsWriterWriteToFileTest {

    private static final Path TEMP_REPORT_DIR = FileSystems.getDefault().getPath("src", "test", "resources");
    private static final List<String> SOURCES = List.of("source.txt");
    private StatisticsWriter writer;
    private final CommandLineArgs mockArgs = Mockito.mock(CommandLineArgs.class);
    private final LogInfoReport mockReport = Mockito.mock(LogInfoReport.class);

    @BeforeEach
    public void setUp() {
        setUpMocks();
        writer = new MarkdownStatisticsWriter(mockArgs.filename());
    }

    @Test
    public void writeToFileCorrectlyTest() {
        assertDoesNotThrow(() ->  writer.writeResultsToFile(TEMP_REPORT_DIR, mockReport, mockArgs, SOURCES));
        Path filePath = TEMP_REPORT_DIR.resolve("report.md");
        assertTrue(Files.exists(filePath));
        try {
            String content = Files.readString(filePath);
            assertTrue(content.contains("Logs statistics report"));
            assertTrue(content.contains("Most frequently used resources"));
            assertTrue(content.contains("HTTP request types frequency"));
            assertTrue(content.contains("Most frequently appeared answer codes"));
            assertTrue(content.contains("Processed source files or URL"));
            assertTrue(content.contains("source.txt"));
            assertTrue(content.contains("resource1"));
            assertTrue(content.contains("resource2"));
            assertTrue(content.contains("200"));
            assertTrue(content.contains("404"));
            assertTrue(content.contains("GET"));
            assertTrue(content.contains("POST"));

            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void pathIsNotDirectoryTest() throws Exception {
        Path filePath = TEMP_REPORT_DIR.resolve("testFile.txt");
        Files.createFile(filePath);
        assertThatThrownBy(() -> writer.writeResultsToFile(filePath, mockReport, mockArgs, List.of()))
            .isInstanceOf(PathIsNotDirectoryException.class)
            .hasMessage("Path is a regular file, not directory: " + filePath);
        Files.deleteIfExists(filePath);
    }

    @Test
    public void FileAlreadyExistsTest() throws Exception {
        Path filePath = TEMP_REPORT_DIR.resolve("report.md");
        Files.createFile(filePath);
        assertThatThrownBy(() -> writer.writeResultsToFile(TEMP_REPORT_DIR, mockReport, mockArgs, List.of()))
            .isInstanceOf(FileAlreadyExistsException.class)
            .hasMessage("File already exists: " + filePath);

        Files.deleteIfExists(filePath);
    }

    private void setUpMocks(){
        when(mockArgs.from()).thenReturn(Optional.empty());
        when(mockArgs.to()).thenReturn(Optional.empty());
        when(mockArgs.filterField()).thenReturn(FilterFieldType.NONE);
        when(mockArgs.filterValue()).thenReturn("");
        when(mockArgs.filename()).thenReturn("report");

        when(mockReport.logsCount()).thenReturn(100L);
        when(mockReport.avgAnswerSize()).thenReturn(100.0);
        when(mockReport.percentile95AnswerSize()).thenReturn(100.0);
        when(mockReport.resourceFrequency()).thenReturn(Map.of("resource1", 50L, "resource2", 50L));
        when(mockReport.codeAnswerFrequency()).thenReturn(Map.of(200, 50L, 404, 50L));
        when(mockReport.requestTypeFrequency()).thenReturn(Map.of(RequestType.GET, 50L, RequestType.POST, 50L));
    }
}
