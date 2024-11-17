package backend.academy.project.logs;

import backend.academy.project.logs.exception.LogParsingException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LogRecordParserDateTest {

    private static final String CORRECT_LOG_START = "80.91.33.133 - - [";
    private static final String CORRECT_LOG_END = "] \"GET /downloads/product_1 HTTP/1.1\" 304 0 \"-\" \"Debian APT-HTTP/1.3 (0.8.16~exp12ubuntu10.17)\"";

    @ParameterizedTest
    @CsvSource({"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"})
    public void correctDateParsingDifferentMonth(String month) {
        String date ="04/" + month + "/2015:07:06:08 +0000";
        String logInfo = CORRECT_LOG_START + date + CORRECT_LOG_END;
        LogRecord log = assertDoesNotThrow(() -> LogRecordParser.parseLog(logInfo));
        assertEquals(LocalDateTime.of(2015, getMonthNum(month), 4, 7, 6, 8), log.timeLocal());
    }

    @Test
    public void incorrectDateFormatParsing() {
        String date ="04-Apr-2015-07:06:08 +0000";
        String logInfo = CORRECT_LOG_START + date + CORRECT_LOG_END;
        assertThatThrownBy(() -> LogRecordParser.parseLog(logInfo))
            .isInstanceOf(LogParsingException.class)
            .hasMessage("Can't parse date: " + date);
    }

    private static int getMonthNum(String month){
        return switch (month) {
            case "Jan" -> 1;
            case "Feb" -> 2;
            case "Mar" -> 3;
            case "Apr" -> 4;
            case "May" -> 5;
            case "Jun" -> 6;
            case "Jul" -> 7;
            case "Aug" -> 8;
            case "Sep" -> 9;
            case "Oct" -> 10;
            case "Nov" -> 11;
            case "Dec" -> 12;
            default -> -1;
        };
    }
}
