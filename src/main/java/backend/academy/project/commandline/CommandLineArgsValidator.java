package backend.academy.project.commandline;

import backend.academy.project.commandline.exception.DateValidationException;
import backend.academy.project.commandline.exception.FileNamingException;
import backend.academy.project.commandline.exception.FilterValidationException;
import java.time.LocalDateTime;

public class CommandLineArgsValidator {

    private static final String FILENAME_REGEX = "^[^<>:\"/\\\\|?*]+$";
    private CommandLineArgsValidator() {}

    public static void validate(CommandLineArgs args)  {
       if (args.from().isPresent() && args.to().isPresent()) {
           validateDates(args.from().orElseThrow(), args.to().orElseThrow());
       }
       validateFilter(args.filterField(), args.filterValue());
    }

    private static void validateDates(LocalDateTime from, LocalDateTime to) {
        if (to.isBefore(from)) {
            throw new DateValidationException("Date from " + from + " is after date to " + to);
        }
    }

    private static void validateFilter(FilterFieldType filterField, String filterValue) {
        if (filterField == FilterFieldType.NONE && !filterValue.isEmpty()) {
            throw new FilterValidationException("Argument [--filter-value] is used without [--filter-field]");
        }
        if (filterField != FilterFieldType.NONE && filterValue.isEmpty()) {
            throw new FilterValidationException("Argument [--filter-field] is used without [--filter-value]");
        }
    }

    private static void validateFilename(String filename) {
        if (!filename.matches(FILENAME_REGEX)) {
            throw new FileNamingException("file name is invalid: " + filename);
        }
    }
}
