package backend.academy.project.readers;

import backend.academy.project.logs.LogRecord;
import backend.academy.project.logs.LogRecordParser;
import backend.academy.project.readers.exception.OpeningFileException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class LocalFileLogsReader implements LogsReader {

    private final FileSearcher searcher = new FileSearcher();
    private final LogRecordParser parser = new LogRecordParser();

    private final String globPath;
    private final List<String> logFileNames = new ArrayList<>();

    public LocalFileLogsReader(String globPath) {
        this.globPath = globPath;
    }

    @Override
    public Stream<LogRecord> readLogLines() {
        Path currentPath = Paths.get("src", "main", "resources").toAbsolutePath();

        List<Path> logFiles = searcher.getLogFiles(globPath, currentPath);
        Stream<String> logRecordStream = Stream.empty();
        for (Path logFile : logFiles) {
            try {
                logFileNames.add(logFile.getFileName().toString());
                logRecordStream = Stream.concat(logRecordStream, Files.lines(logFile));
            } catch (IOException e) {
                throw new OpeningFileException("Can't open file " + logFile, e);
            }
        }
        return logRecordStream.map(parser::parseLog);
    }

    @Override
    public List<String> getLogFileNames() {
        return Collections.unmodifiableList(logFileNames);
    }
}
