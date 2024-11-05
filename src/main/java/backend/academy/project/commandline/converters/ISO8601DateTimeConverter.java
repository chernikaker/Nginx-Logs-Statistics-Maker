package backend.academy.project.commandline.converters;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ISO8601DateTimeConverter implements IStringConverter<LocalDateTime> {

    @Override
    public LocalDateTime convert(String dateString) {
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            try {
                LocalDate localDate = LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
                return localDate.atStartOfDay();
            } catch (DateTimeParseException ex) {
                throw new ParameterException("Invalid ISO8601 local date: " + dateString, ex);
            }
        }
    }
}
