package backend.academy.project.logs;

import backend.academy.project.logs.exception.LogParsingException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogRecordParserTest {

    @Test
    public void parseCorrectLogLine() {
        String logLine = "128.199.51.40 - - [04/Jun/2015:07:06:34 +0000] \"GET /downloads/product_2 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"";
        LogRecord log = assertDoesNotThrow(() -> LogRecordParser.parseLog(logLine));
        assertEquals("128.199.51.40", log.remoteAddress());
        assertEquals("-", log.remoteUser());
        assertEquals(LocalDateTime.of(2015, 6, 4, 7, 6, 34), log.timeLocal());
        assertEquals(RequestType.GET, log.requestType());
        assertEquals("/downloads/product_2", log.requestResource());
        assertEquals("HTTP/1.1", log.httpVersion());
        assertEquals(304, log.status());
        assertEquals(0, log.bytesSent());
        assertEquals("-", log.httpReferer());
        assertEquals("Debian APT-HTTP/1.3 (0.9.7.9)", log.httpUserAgent());
    }

    @Test
    public void parseIncorrectFormatLogLine() {
        String logLine = "128.199.51.40 - 04/Jun/2015:07:06:34 +0000 GET /downloads/product_2 HTTP/1.1 304 0 - Debian APT-HTTP/1.3 (0.9.7.9)";
        assertThatThrownBy(() -> LogRecordParser.parseLog(logLine))
            .isInstanceOf(LogParsingException.class)
            .hasMessage("Can't parse log, invalid format: " + logLine);
    }

    @Test
    public void parseIncorrectAnswerCodeLogLine() {
        String logLine = "128.199.51.40 - 04/Jun/2015:07:06:34 +0000 GET /downloads/product_2 HTTP/1.1 3004 0 - Debian APT-HTTP/1.3 (0.9.7.9)";
        assertThatThrownBy(() -> LogRecordParser.parseLog(logLine))
            .isInstanceOf(LogParsingException.class)
            .hasMessage("Can't parse log, invalid format: " + logLine);
    }

    @Test
    public void parseIncorrectNumbersLogLine() {
        String logLine = "128.199.51.40 - 04/Jun/2015:07:06:34 +0000 GET /downloads/product_2 HTTP/1.1 300Error4 0Error - Debian APT-HTTP/1.3 (0.9.7.9)";
        assertThatThrownBy(() -> LogRecordParser.parseLog(logLine))
            .isInstanceOf(LogParsingException.class)
            .hasMessage("Can't parse log, invalid format: " + logLine);
    }

    @ParameterizedTest
    @CsvSource({"GET","POST","PUT","PATCH","OPTIONS","HEAD"})
    public void parseAllRequestTypesLogLine(String requestType) {
        String logLine = "128.199.51.40 - - [04/Jun/2015:07:06:34 +0000] \""+requestType+" /downloads/product_2 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"";
        LogRecord log = assertDoesNotThrow(() -> LogRecordParser.parseLog(logLine));
        assertEquals(RequestType.valueOf(requestType), log.requestType());
    }

    @Test
    public void parseIncorrectRequestTypeLogLine() {
        String logLine = "128.199.51.40 - - [04/Jun/2015:07:06:34 +0000] \"ERROR /downloads/product_2 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"";
        assertThatThrownBy(() -> LogRecordParser.parseLog(logLine))
            .isInstanceOf(LogParsingException.class)
            .hasMessageContaining("Can't parse request: ");
    }

    @Test
    public void parseIncorrectHTTPVersionFormatLogLine() {
        String logLine = "128.199.51.40 - - [04/Jun/2015:07:06:34 +0000] \"GET /downloads/product_2 ERROR/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.9.7.9)\"";
        assertThatThrownBy(() -> LogRecordParser.parseLog(logLine))
            .isInstanceOf(LogParsingException.class)
            .hasMessageContaining("Can't parse request: ");
    }
}
