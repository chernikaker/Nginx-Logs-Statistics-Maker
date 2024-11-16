package backend.academy.project.readers;

import backend.academy.project.logs.LogRecord;
import backend.academy.project.readers.exception.ReadingFromUrlException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UrlLogsReaderTest {

    private static final String MOCK_URL = "http://example.com/logs";
    private  UrlLogsReader reader;
    private final HttpClient mockHttpClient = mock(HttpClient.class);
    private final HttpResponse<String> mockHttpResponse = mock(HttpResponse.class);
    private static final String VALID_LOG =
        "0.61.164.147 - - [13/Nov/2024:12:59:53 +0000] \"GET /moderator.jpg HTTP/1.1\" 200 2065 \"-\" \"Mozilla/5.0 (X11; Linux x86_64; rv:8.0) Gecko/1963-24-10 Firefox/37.0\"";
    private static final String INVALID_LOG = "Invalid info";
    

    @Test
    public void pathAndAndLogsAreCorrectTest() {
        try (MockedStatic<HttpClient> mockedStaticHttpClient = Mockito.mockStatic(HttpClient.class)) {
            mockedStaticHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpResponse.body()).thenReturn(VALID_LOG);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);
            reader = new UrlLogsReader(MOCK_URL);
            List<LogRecord> result = assertDoesNotThrow(() -> reader.readLogLines().toList());
            assertEquals(1, result.size());
            List<String> logSourceNames = reader.getLogSourceNames();
            assertEquals(1, logSourceNames.size());
            assertEquals("http://example.com/logs", logSourceNames.getFirst());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void ErrorProcessingPathTest() {
        reader = new UrlLogsReader("Invalid URL");
        assertThatThrownBy(() -> reader.readLogLines())
            .isInstanceOf(ReadingFromUrlException.class)
            .hasMessageContaining("Error while reading log lines from Invalid URL");
    }

    @Test
    public void ErrorProcessingLogLineTest() {
        try (MockedStatic<HttpClient> mockedStaticHttpClient = Mockito.mockStatic(HttpClient.class)) {
            mockedStaticHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            when(mockHttpResponse.body()).thenReturn(VALID_LOG+'\n'+INVALID_LOG);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(mockHttpResponse);
            reader = new UrlLogsReader(MOCK_URL);
            List<LogRecord> result = assertDoesNotThrow(() -> reader.readLogLines().toList());
            assertEquals(1, result.size());
            List<String> logSourceNames = reader.getLogSourceNames();
            assertEquals(1, logSourceNames.size());
            assertEquals("http://example.com/logs", logSourceNames.getFirst());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void CallingFilenamesGettingBeforeLogsReadingTest() {
        reader = new UrlLogsReader(MOCK_URL);
        List<String> fileNames = reader.getLogSourceNames();
        assertEquals(0, fileNames.size());
    }
}
