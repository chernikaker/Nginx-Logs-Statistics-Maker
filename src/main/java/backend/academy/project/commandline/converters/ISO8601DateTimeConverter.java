package backend.academy.project.commandline.converters;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

/**
 *  Класс-конвертер строкового представления времени в формате ISO8601 в объект
 *  LocalDateTime.
 *  Поддерживает форматы:<br>
 *  <ul>
 *      <li>'YYYY-MM-ddThh:mm:ss'</li>
 *      <li>'YYYY-MM-dd'</li>
 *  </ul>
 *  В случае парсинга даты без времени устанавливает время на начало дня.
 * @see LocalDateTime
 */
public class ISO8601DateTimeConverter implements IStringConverter<Optional<LocalDateTime>> {

    @Override
    public Optional<LocalDateTime> convert(String dateString) {
        try {
            return Optional.of(LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } catch (DateTimeParseException e) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
                return Optional.of(localDate.atStartOfDay());
            } catch (DateTimeParseException ex) {
                throw new ParameterException("Invalid ISO8601 local date: " + dateString, ex);
            }
        }
    }
}
