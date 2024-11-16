package backend.academy.project.report.data;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.commandline.FilterFieldType;
import backend.academy.project.logs.LogRecord;
import backend.academy.project.logs.RequestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class StatisticsCollectorTest {

    @Mock
    private CommandLineArgs mockArgs = Mockito.mock(CommandLineArgs.class);
    @Mock
    private LogRecord mockLogRecord = Mockito.mock(LogRecord.class);

    private static final LocalDateTime MOCK_DATE_TIME = LocalDateTime.of(2024,4,14,0,0,0);
    private static final double DELTA = 0.01;


    @BeforeEach
    void setUp() {
        makeDefaultLogRecord(mockLogRecord);
        makeDefaultMockArgs(mockArgs);
    }

    @Test
    public void oneLogInStreamTest() {

        Stream<LogRecord> logRecords = Stream.of(mockLogRecord);
        LogInfoReport report = StatisticsCollector.calculateLogStatistics(logRecords, mockArgs);
        assertEquals(1, report.logsCount());
        assertEquals(1, report.uniqueIPCount());
        assertEquals(100.0, report.avgAnswerSize(), DELTA);
        assertEquals(100.0, report.percentile95AnswerSize(), DELTA);

        assertEquals(1, report.requestTypeFrequency().size());
        assertTrue(report.requestTypeFrequency().containsKey(RequestType.GET));
        assertEquals(1, report.requestTypeFrequency().get(RequestType.GET));

        assertEquals(1, report.codeAnswerFrequency().size());
        assertTrue(report.codeAnswerFrequency().containsKey(200));
        assertEquals(1, report.codeAnswerFrequency().get(200));

        assertEquals(1, report.resourceFrequency().size());
        assertTrue(report.resourceFrequency().containsKey("resource"));
        assertEquals(1, report.resourceFrequency().get("resource"));
    }

    @Test
    public void twoSameLogsInStreamTest() {

        Stream<LogRecord> logRecords = Stream.of(mockLogRecord, mockLogRecord);
        LogInfoReport report = StatisticsCollector.calculateLogStatistics(logRecords, mockArgs);
        assertEquals(2, report.logsCount());
        assertEquals(1, report.uniqueIPCount());
        assertEquals(100.0, report.avgAnswerSize(), DELTA);
        assertEquals(100.0, report.percentile95AnswerSize(), DELTA);

        assertEquals(1, report.requestTypeFrequency().size());
        assertTrue(report.requestTypeFrequency().containsKey(RequestType.GET));
        assertEquals(2, report.requestTypeFrequency().get(RequestType.GET));

        assertEquals(1, report.codeAnswerFrequency().size());
        assertTrue(report.codeAnswerFrequency().containsKey(200));
        assertEquals(2, report.codeAnswerFrequency().get(200));

        assertEquals(1, report.resourceFrequency().size());
        assertTrue(report.resourceFrequency().containsKey("resource"));
        assertEquals(2, report.resourceFrequency().get("resource"));
    }
    

    private static void makeDefaultLogRecord(LogRecord mockLogRecord) {
        when(mockLogRecord.remoteAddress()).thenReturn("127.0.0.1");
        when(mockLogRecord.remoteUser()).thenReturn("-");
        when(mockLogRecord.timeLocal()).thenReturn(MOCK_DATE_TIME);
        when(mockLogRecord.requestResource()).thenReturn("resource");
        when(mockLogRecord.status()).thenReturn(200);
        when(mockLogRecord.requestType()).thenReturn(RequestType.GET);
        when(mockLogRecord.httpVersion()).thenReturn("HTTP/1.1");
        when(mockLogRecord.bytesSent()).thenReturn(100L);
        when(mockLogRecord.httpReferer()).thenReturn("-");
        when(mockLogRecord.httpUserAgent()).thenReturn("Mozilla/5.0 (X11; Linux x86_64; rv:8.0) Gecko/1963-24-10 Firefox/37.0");
    }

    private static void makeDefaultMockArgs(CommandLineArgs mockArgs) {
        when(mockArgs.from()).thenReturn(Optional.empty());
        when(mockArgs.to()).thenReturn(Optional.empty());
        when(mockArgs.filterField()).thenReturn(FilterFieldType.NONE);
        when(mockArgs.filterValue()).thenReturn("");
    }
}
