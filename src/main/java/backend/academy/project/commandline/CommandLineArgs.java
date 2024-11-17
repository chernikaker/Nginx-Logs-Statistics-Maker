package backend.academy.project.commandline;

import backend.academy.project.commandline.converters.ISO8601DateTimeConverter;
import com.beust.jcommander.Parameter;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Getter;

@Getter
public class CommandLineArgs {

    @Parameter(
        names = {"--path", "--P"},
        description = "Path to log file(s) or URL",
        required = true
    )
    private String pathToLogs;

    @Parameter(
        names = {"--format"},
        description = "Format of output"
    )
    private OutputFileType type = OutputFileType.MARKDOWN;

    @Parameter(
        names = {"--from"},
        description = "Lower bound of logs time",
        converter = ISO8601DateTimeConverter.class
    )
    private Optional<LocalDateTime> from = Optional.empty();

    @Parameter(
        names = {"--to"},
        description = "Upper bound of logs time",
        converter = ISO8601DateTimeConverter.class
    )
    private Optional<LocalDateTime> to = Optional.empty();

    @Parameter(
        names = {"--filter-field"},
        description = "Field of the log to filter by"
    )
    private FilterFieldType filterField = FilterFieldType.NONE;

    @Parameter(
        names = {"--filter-value"},
        description = "Regex value of log field to filter by"
    )
    private String filterValue = "";

    // дополнительный опциональный параметр - выбор имени файла с отчетом
    @Parameter(
        names = {"--filename"},
        description = "Name of the report file"
    )
    private String filename = "statistics_report";
}
