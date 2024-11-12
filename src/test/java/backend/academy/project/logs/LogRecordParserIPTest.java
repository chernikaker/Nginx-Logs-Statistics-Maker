package backend.academy.project.logs;

import backend.academy.project.logs.exception.ParsingLogException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogRecordParserIPTest {

    private final LogRecordParser logRecordParser = new LogRecordParser();
    private final String correctLogPart = " - - [17/May/2015:08:05:07 +0000] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.17)\"";

    @ParameterizedTest
    @CsvSource({"80.91.33.133","255.255.255.255","1.1.1.1","210.14.1.0"})
    public void parseCorrectLogStandardIP(String ipPart) {
        String log = ipPart + correctLogPart;
        LogRecord processedLog = assertDoesNotThrow(() -> logRecordParser.parseLog(log));
        assertEquals(ipPart, processedLog.remoteAddress());
    }

    @ParameterizedTest
    @CsvSource({"FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF:FFFF",
                "acb:1223::11", "acb:1223::dddd:2:3:11",
                "1111:AAAA::","1111:AAAA:10:11::",
                "::1111:AAAA","::1111:AAAA:10:11",
                "::"})
    public void parseCorrectIPv6(String ipPart) {
        String log = ipPart + correctLogPart;
        LogRecord processedLog = assertDoesNotThrow(() -> logRecordParser.parseLog(log));
        assertEquals(ipPart, processedLog.remoteAddress());
    }

    @ParameterizedTest
    @CsvSource({"80.91.33..133","255-255-255-255","1.1.1","210.14.1.0.13"})
    public void standardIPDoesNotMatchPattern(String ipPart) {
        String log = ipPart + correctLogPart;
        assertThatThrownBy(() -> logRecordParser.parseLog(log))
            .isInstanceOf(ParsingLogException.class)
            .hasMessageContaining("Ip address has invalid format: "+ipPart);
    }

    @ParameterizedTest
    @CsvSource({"80.91.33.256","1111.1.1.1"})
    public void standardIPHasInvalidValues(String ipPart) {
        String log = ipPart + correctLogPart;
        assertThatThrownBy(() -> logRecordParser.parseLog(log))
            .isInstanceOf(ParsingLogException.class)
            .hasMessageContaining("Ip address has invalid format: "+ipPart);
    }

    @ParameterizedTest
    @CsvSource({"1::::","::::1","::1::","1:1::1:1::1"})
    public void IPv6HasManyEmptyBlocks(String ipPart) {
        String log = ipPart + correctLogPart;
        assertThatThrownBy(() -> logRecordParser.parseLog(log))
            .isInstanceOf(ParsingLogException.class)
            .hasMessageContaining("Ip address has invalid format: "+ipPart);
    }

    @ParameterizedTest
    @CsvSource({"1:1:1:1:1:1:1:1::","1:1:1:1:1:1:1::1","::1:1:1:1:1:1:1:1"})
    public void IPv6HasAllPartsAndEmptyBlock(String ipPart) {
        String log = ipPart + correctLogPart;
        assertThatThrownBy(() -> logRecordParser.parseLog(log))
            .isInstanceOf(ParsingLogException.class)
            .hasMessageContaining("Ip address has invalid format: "+ipPart);
    }

    @ParameterizedTest
    @CsvSource({"1:1:1:1:1:1G:1:1","1:1+:1:1:1:1:1::1"})
    public void IPv6DoesNotMatchPattern(String ipPart) {
        String log = ipPart + correctLogPart;
        assertThatThrownBy(() -> logRecordParser.parseLog(log))
            .isInstanceOf(ParsingLogException.class)
            .hasMessageContaining("Ip address has invalid format: "+ipPart);
    }

    @Test
    public void IPv6PartsHaveMoreThanFourChars() {
        String log = "1:1:1:1:1:11111:1:1" + correctLogPart;
        assertThatThrownBy(() -> logRecordParser.parseLog(log))
            .isInstanceOf(ParsingLogException.class)
            .hasMessageContaining("Ip address has invalid format: ");
    }

    @Test
    public void IPv6HasNotEnoughParts() {
        String log = "1:1:1:1:1:1:1" + correctLogPart;
        assertThatThrownBy(() -> logRecordParser.parseLog(log))
            .isInstanceOf(ParsingLogException.class)
            .hasMessageContaining("Ip address has invalid format: ");
    }

    @Test
    public void IPv6HasTooManyParts() {
        String log = "1:1:1:1:1:1:1:1:1" + correctLogPart;
        assertThatThrownBy(() -> logRecordParser.parseLog(log))
            .isInstanceOf(ParsingLogException.class)
            .hasMessageContaining("Ip address has invalid format: ");
    }
}
