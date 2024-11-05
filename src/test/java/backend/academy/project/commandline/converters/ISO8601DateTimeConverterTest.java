package backend.academy.project.commandline.converters;

import com.beust.jcommander.ParameterException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ISO8601DateTimeConverterTest {

    private final ISO8601DateTimeConverter converter = new ISO8601DateTimeConverter();

    @ParameterizedTest
    @CsvSource({"2005-08-09T18:31:42"})
    public void convertLocalDateTime(String sampleDateTime) {
        LocalDateTime dateTime = assertDoesNotThrow(() -> converter.convert(sampleDateTime));
        assertEquals(LocalDateTime.of(2005, 8, 9, 18, 31, 42), dateTime);
    }

    @ParameterizedTest
    @CsvSource({"2005-08-09"})
    public void convertLocalDateOnly(String sampleDateTime) {
        LocalDateTime dateTime = assertDoesNotThrow(() -> converter.convert(sampleDateTime));
        assertEquals(LocalDateTime.of(2005, 8, 9, 0, 0, 0), dateTime);
    }

    @ParameterizedTest
    @CsvSource({"errorDate","20050809","20050809T183142","09-08-2005T18:31:42"})
    public void invalidLocalDateTimeFormat(String sampleDateTime) {
        assertThatThrownBy(() -> converter.convert(sampleDateTime))
            .isInstanceOf(ParameterException.class)
            .hasMessageContaining("Invalid ISO8601 local date: " + sampleDateTime);
          }
}
