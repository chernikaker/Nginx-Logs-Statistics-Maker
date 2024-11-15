package backend.academy.project.readers;

import backend.academy.project.logs.LogRecord;
import backend.academy.project.logs.LogRecordParser;
import backend.academy.project.logs.exception.LogParsingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LocalFileLogsReader extends LogsReader {

    private static final Path ROOT_PATH = Paths.get("src", "main", "resources").toAbsolutePath();
    private final String globPath;


    public LocalFileLogsReader(String globPath) {
        this.globPath = globPath;
    }

    @Override
    public Stream<LogRecord> readLogLines() {

        List<Path> logFiles = FileSearcher.getLogFiles(globPath, ROOT_PATH);
        Stream<LogRecord> logRecordStream = Stream.empty();
        for (Path logFile : logFiles) {
            try {
                Stream<LogRecord> logs = Files
                    .lines(logFile)
                    .map(tryParseLog)
                    .filter(Objects::nonNull);
                logRecordStream = Stream.concat(logRecordStream, logs);
                logSourceNames.add(logFile.getFileName().toString());
            }  catch (IOException e) {
                logger.warn("Can't open file {}. Current file skipped", logFile, e);
            }
        }
        return logRecordStream;
    }

}
