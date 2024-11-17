package backend.academy.project.readers;

import backend.academy.project.logs.LogRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LocalFileLogsReaderTest {

    private  LocalFileLogsReader reader;
    private final Path mockLogFile = Mockito.mock(Path.class);
    private final Path mockLogErrorFile = Mockito.mock(Path.class);

    private static final String VALID_LOG = "0.61.164.147 - - [13/Nov/2024:12:59:53 +0000] \"GET /moderator.jpg HTTP/1.1\" 200 2065 \"-\" \"Mozilla/5.0 (X11; Linux x86_64; rv:8.0) Gecko/1963-24-10 Firefox/37.0\"";
    private static final String INVALID_LOG = "Invalid info";

    @BeforeEach
    void setUp() {
        reader = new LocalFileLogsReader("glob", Path.of("root"));
        when(mockLogFile.getFileName()).thenReturn(Path.of("mockLogFile.txt"));
        when(mockLogFile.toString()).thenReturn("root/mockLogFile.txt");
        when(mockLogErrorFile.getFileName()).thenReturn(Path.of("mockLogErrorFile.txt"));
        when(mockLogErrorFile.toString()).thenReturn("root/mockLogErrorFile.txt");
    }

    @Test
    public void allFilesAndLogsCorrectTest(){
        try (MockedStatic<FileSearcher> mockedFileStatic = Mockito.mockStatic(FileSearcher.class);
             MockedStatic<Files> mockedStatic = Mockito.mockStatic(Files.class)) {
            mockedFileStatic.when(() -> FileSearcher.getLogFiles(anyString(), any(Path.class)))
                .thenReturn(List.of(mockLogFile));
            Stream<String> lines = Stream.of(VALID_LOG);
                mockedStatic.when(() -> Files.lines(mockLogFile)).thenReturn(lines);
                List<LogRecord> result = assertDoesNotThrow(() -> reader.readLogLines().toList());
                assertEquals(1, result.size());
                List<String> fileNames = reader.getLogSourceNames();
                assertEquals(1, fileNames.size());
                assertEquals("mockLogFile.txt", fileNames.getFirst());
        }
    }

    @Test
    public void ErrorProcessingFileTest() {
        try (MockedStatic<FileSearcher> mockedFileStatic = Mockito.mockStatic(FileSearcher.class);
             MockedStatic<Files> mockedStatic = Mockito.mockStatic(Files.class)) {
            mockedFileStatic.when(() -> FileSearcher.getLogFiles(anyString(), any(Path.class))).thenReturn(List.of(mockLogFile, mockLogErrorFile));
            Stream<String> lines = Stream.of(VALID_LOG);
            mockedStatic.when(() -> Files.lines(mockLogFile)).thenReturn(lines);
            mockedStatic.when(() -> Files.lines(mockLogErrorFile)).thenThrow(new IOException());

                List<LogRecord> result = assertDoesNotThrow(() -> reader.readLogLines().toList());
                assertEquals(1, result.size());
                List<String> fileNames = reader.getLogSourceNames();
                assertEquals(1, fileNames.size());
                assertEquals("mockLogFile.txt", fileNames.getFirst());
        }
    }

    @Test
    public void ErrorProcessingLogLineTest() {
        try (MockedStatic<FileSearcher> mockedFileStatic = Mockito.mockStatic(FileSearcher.class);
            MockedStatic<Files> mockedStatic = Mockito.mockStatic(Files.class)) {
            mockedFileStatic.when(() -> FileSearcher.getLogFiles(anyString(), any(Path.class)))
                .thenReturn(List.of(mockLogFile));

            Stream<String> lines = Stream.of(VALID_LOG, INVALID_LOG);
                mockedStatic.when(() -> Files.lines(mockLogFile)).thenReturn(lines);

                List<LogRecord> result = assertDoesNotThrow(() -> reader.readLogLines().toList());
                assertEquals(1, result.size());
                List<String> fileNames = reader.getLogSourceNames();
                assertEquals(1, fileNames.size());
                assertEquals("mockLogFile.txt", fileNames.getFirst());
        }
    }

    @Test
    public void ErrorNullFileNameError() {
        when(mockLogFile.getFileName()).thenReturn(null);
        try (MockedStatic<FileSearcher> mockedFileStatic = Mockito.mockStatic(FileSearcher.class);
             MockedStatic<Files> mockedStatic = Mockito.mockStatic(Files.class)) {
            mockedFileStatic.when(() -> FileSearcher.getLogFiles(anyString(), any(Path.class)))
                .thenReturn(List.of(mockLogFile));

            Stream<String> lines = Stream.of(VALID_LOG);
                mockedStatic.when(() -> Files.lines(mockLogFile)).thenReturn(lines);

                List<LogRecord> result = assertDoesNotThrow(() -> reader.readLogLines().toList());
                assertEquals(0, result.size());
                List<String> fileNames = reader.getLogSourceNames();
                assertEquals(0, fileNames.size());
        }
    }

    @Test
    public void CallingFilenamesGettingBeforeLogsReadingTest() {
            List<String> fileNames = reader.getLogSourceNames();
            assertEquals(0, fileNames.size());
    }

}
