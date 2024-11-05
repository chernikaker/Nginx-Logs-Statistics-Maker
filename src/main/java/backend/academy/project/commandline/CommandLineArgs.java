package backend.academy.project.commandline;

import backend.academy.project.commandline.converters.ISO8601DateTimeConverter;
import com.beust.jcommander.Parameter;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CommandLineArgs {

    @Parameter(names = {"--path","--P"}, description = "Path to log file(s)", required = true)
    private String pathToLogs;

    @Parameter(names ={"--format"}, description = "Format of output")
    private OuputFileType type = OuputFileType.MARKDOWN;

    @Parameter(names={"--from"},
        description = "Lower bound of logs time",
        converter = ISO8601DateTimeConverter.class)
    private LocalDateTime from = LocalDateTime.MIN;

    @Parameter(names={"--to"},
        description = "Upper bound of logs time",
        converter = ISO8601DateTimeConverter.class)
    private LocalDateTime to = LocalDateTime.MAX;
}
