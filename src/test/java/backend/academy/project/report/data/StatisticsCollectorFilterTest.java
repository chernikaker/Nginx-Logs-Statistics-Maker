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
import static org.mockito.Mockito.*;

public class StatisticsCollectorFilterTest {

    @Mock
    private CommandLineArgs mockArgs = Mockito.mock(CommandLineArgs.class);
    @Mock
    private LogRecord mockLogRecord = Mockito.mock(LogRecord.class);
    @Mock
    private LogRecord mockLogRecordToFilter = Mockito.mock(LogRecord.class);

    private static final LocalDateTime MOCK_DATE_TIME = LocalDateTime.of(2024,4,14,0,0,0);


    @BeforeEach
    void setUp() {
        makeDefaultLogRecord(mockLogRecord);
        makeDefaultLogRecord(mockLogRecordToFilter);
        makeDefaultMockArgs(mockArgs);
    }

    @Test
    public void noFilteringLogsTest() {

        Stream<LogRecord> logRecords = Stream.of(mockLogRecord, mockLogRecordToFilter);
        LogInfoReport report = StatisticsCollector.calculateLogStatistics(logRecords, mockArgs);
        assertEquals(2, report.logsCount());
    }

    @Test
    public void filterLogsByDateFromTest() {

        when(mockLogRecordToFilter.timeLocal()).thenReturn(MOCK_DATE_TIME.minusDays(2));
        when(mockArgs.from()).thenReturn(Optional.of(MOCK_DATE_TIME.minusDays(1)));

        Stream<LogRecord> logRecords = Stream.of(mockLogRecord, mockLogRecordToFilter);
        LogInfoReport report = StatisticsCollector.calculateLogStatistics(logRecords, mockArgs);
        assertEquals(1, report.logsCount());
    }

    @Test
    public void filterLogsByDateToTest() {

        when(mockLogRecordToFilter.timeLocal()).thenReturn(MOCK_DATE_TIME.plusDays(2));
        when(mockArgs.to()).thenReturn(Optional.of(MOCK_DATE_TIME.plusDays(1)));

        Stream<LogRecord> logRecords = Stream.of(mockLogRecord, mockLogRecordToFilter);
        LogInfoReport report = StatisticsCollector.calculateLogStatistics(logRecords, mockArgs);
        assertEquals(1, report.logsCount());
    }

    @Test
    public void filterLogsByRequestFieldTest() {

        when(mockLogRecord.getValueByFieldName(FilterFieldType.REQUEST_TYPE)).thenReturn("GET");
        when(mockLogRecordToFilter.getValueByFieldName(FilterFieldType.REQUEST_TYPE)).thenReturn("PUT");
        when(mockArgs.filterField()).thenReturn(FilterFieldType.REQUEST_TYPE);
        when(mockArgs.filterValue()).thenReturn("GET");

        Stream<LogRecord> logRecords = Stream.of(mockLogRecord, mockLogRecordToFilter);
        LogInfoReport report = StatisticsCollector.calculateLogStatistics(logRecords, mockArgs);
        assertEquals(1, report.logsCount());
        assertEquals(1, report.requestTypeFrequency().size());
        assertEquals(1, report.requestTypeFrequency().get(RequestType.GET));
    }

    @Test
    public void filterLogsByHTTPVersionRegexTest() {

        when(mockLogRecord.getValueByFieldName(FilterFieldType.HTTP_VERSION)).thenReturn("HTTP/1.1");
        when(mockLogRecordToFilter.getValueByFieldName(FilterFieldType.HTTP_VERSION)).thenReturn("HTTP/2.1");
        when(mockArgs.filterField()).thenReturn(FilterFieldType.HTTP_VERSION);
        when(mockArgs.filterValue()).thenReturn("HTTP\\/1\\..*");

        Stream<LogRecord> logRecords = Stream.of(mockLogRecord, mockLogRecordToFilter);
        LogInfoReport report = StatisticsCollector.calculateLogStatistics(logRecords, mockArgs);
        assertEquals(1, report.logsCount());
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
