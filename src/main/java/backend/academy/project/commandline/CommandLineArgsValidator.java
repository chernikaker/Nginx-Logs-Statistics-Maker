package backend.academy.project.commandline;

import backend.academy.project.commandline.exception.DateValidationException;
import backend.academy.project.commandline.exception.EmptyPathValidationException;
import backend.academy.project.commandline.exception.FileNamingException;
import backend.academy.project.commandline.exception.FilterValidationException;
import java.time.LocalDateTime;

/**
 * Класс-валидатор параметров командной строки
 */
public class CommandLineArgsValidator {

    private static final String FILENAME_REGEX = "^[^<>:\"/\\\\|?*]+$";

    private CommandLineArgsValidator() {}

    public static void validate(CommandLineArgs args)  {
       if (args.from().isPresent() && args.to().isPresent()) {
           validateDates(args.from().orElseThrow(), args.to().orElseThrow());
       }
       validateFilter(args.filterField(), args.filterValue());
       validateFilename(args.filename());
       validatePath(args.pathToLogs());
    }

    /**
     * @param from начальная дата
     * @param to конечная дата
     * @throws DateValidationException Когда конечная дата раньше начальной
     */
    private static void validateDates(LocalDateTime from, LocalDateTime to) {
        if (to.isBefore(from)) {
            throw new DateValidationException("Date from " + from + " is after date to " + to);
        }
    }

    /**
     * @param filterField поле для фильтрации
     * @param filterValue регулярное выражение для фильтрации
     * @throws FilterValidationException Когда было введено только поле или только значение
     * (фильтрация невозможна)
     */
    private static void validateFilter(FilterFieldType filterField, String filterValue) {
        if (filterField == FilterFieldType.NONE && !filterValue.isEmpty()) {
            throw new FilterValidationException("Argument [--filter-value] is used without [--filter-field]");
        }
        if (filterField != FilterFieldType.NONE && filterValue.isEmpty()) {
            throw new FilterValidationException("Argument [--filter-field] is used without [--filter-value]");
        }
    }

    /**
     * Регулярное выражение FILENAME_REGEX соответствует правилам наименования файлов.<br>
     * Запрещены следующие символы:<br>
     * < > : " / \\ | ? *
     * @param filename название файла итогового отчета
     * @throws FileNamingException Когда название файла не соответствует правилам наименования
     */
    private static void validateFilename(String filename) {
        if (!filename.matches(FILENAME_REGEX) || filename.isBlank()) {
            throw new FileNamingException("file name is invalid: " + filename);
        }
    }

    /**
     * @param path шаблон пути к локальным файлам или URL
     * @throws EmptyPathValidationException когда путь пустой
     */
    private static void validatePath(String path) {
        if (path.isBlank()) {
            throw new EmptyPathValidationException("Path is empty");
        }
    }
}
