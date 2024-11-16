package backend.academy.project.report.view;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.commandline.FilterFieldType;
import backend.academy.project.logs.RequestType;
import backend.academy.project.report.data.LogInfoReport;
import backend.academy.project.report.view.exception.PathIsNotDirectoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class MarkdownStatisticsWriterTest {

    private static final Path TEMP_REPORT_DIR = FileSystems.getDefault().getPath("src", "test", "resources");
    private StatisticsWriter writer;
    private static final List<String> SOURCES = List.of("source.txt");

    @Mock
    private CommandLineArgs mockArgs = Mockito.mock(CommandLineArgs.class);

    @Mock
    private LogInfoReport mockReport = Mockito.mock(LogInfoReport.class);

    @BeforeEach
    public void setUp() {
        setUpMocks();
        writer = new MarkdownStatisticsWriter(mockArgs.filename());
    }

    @Test
    public void correctStructureTest() {

        assertDoesNotThrow(() ->  writer.writeResultsToFile(TEMP_REPORT_DIR, mockReport, mockArgs, SOURCES));
        Path filePath = TEMP_REPORT_DIR.resolve("report.md");
        assertTrue(Files.exists(filePath));
        try {
            String content = Files.readString(filePath);
            assertEquals(content, returnMockReport());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private String returnMockReport(){
        String report = "# Logs statistics report\n" +
            "##  Processed source files or URL\n" +
            "Source|\n" +
            "|:---------:|\n" +
            "|source.txt|\n" +
            "## Common info\n" +
            "Metric|Value|\n" +
            "|:---------:|:---------:|\n" +
            "|Date from|-|\n" +
            "|Date to|-|\n" +
            "|Filter|-|\n" +
            "|Logs amount|100|\n" +
            "|Unique IP amount|100|\n" +
            "|Average bytes sent|100,000000|\n" +
            "|95 percentile of bytes sent|100,000000|\n" +
            "## Most frequently used resources (top 5)\n" +
            "Resource|Usages|\n" +
            "|:---------:|:---------:|\n" +
            "|resource1|50|\n" +
            "|resource2|50|\n" +
            "## Most frequently appeared answer codes (top 5)\n" +
            "Code|Description|Amount|\n" +
            "|:---------:|:---------:|:---------:|\n" +
            "|200|OK|50|\n" +
            "|404|Not Found|50|\n" +
            "## HTTP request types frequency\n" +
            "Type|Occurrences|\n" +
            "|:---------:|:---------:|\n" +
            "|GET|50|\n" +
            "|POST|50|\n";

        return report;
    }
}
