package backend.academy.project.readers;

import backend.academy.project.logs.LogRecord;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LocalFileLogsReader extends LogsReader {

    private final Path rootPath;
    private final String globPath;


    public LocalFileLogsReader(String globPath, Path rootPath) {
        this.globPath = globPath;
        this.rootPath = rootPath;
    }

    public LocalFileLogsReader(String globPath) {
        this(globPath, Paths.get("").toAbsolutePath());
    }

    @Override
    public Stream<LogRecord> readLogLines() {

        List<Path> logFiles = FileSearcher.getLogFiles(globPath, rootPath);
        Stream<LogRecord> logRecordStream = Stream.empty();
        for (Path logFile : logFiles) {
            try {
                Path fileName = logFile.getFileName();
                if (fileName == null) {
                    throw new NullPointerException("fileName of file " + logFile + " is null");
                }
                Stream<LogRecord> logs = Files
                    .lines(logFile)
                    .map(tryParseLog)
                    .filter(Objects::nonNull);
                logRecordStream = Stream.concat(logRecordStream, logs);
                logSourceNames.add(fileName.toString());

            } catch (Exception e) {
                logger.warn("Can't process file {}. Current file skipped", logFile, e);
            }
        }
        return logRecordStream;
    }
}
