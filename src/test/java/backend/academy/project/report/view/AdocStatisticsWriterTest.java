package backend.academy.project.report.view;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.commandline.FilterFieldType;
import backend.academy.project.logs.RequestType;
import backend.academy.project.report.data.LogInfoReport;
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

public class AdocStatisticsWriterTest {

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
        writer = new AdocStatisticsWriter(mockArgs.filename());
    }

    @Test
    public void containsAdocSymbolsTest() {

        assertDoesNotThrow(() ->  writer.writeResultsToFile(TEMP_REPORT_DIR, mockReport, mockArgs, SOURCES));
        Path filePath = TEMP_REPORT_DIR.resolve("report.adoc");
        assertTrue(Files.exists(filePath));
        try {
            String content = Files.readString(filePath);
            assertTrue(content.contains("="));
            assertTrue(content.contains("[cols="));
            assertTrue(content.contains("]"));
            assertTrue(content.contains("|"));
            assertTrue(content.contains("=="));
            assertTrue(content.contains("|===="));
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

}
