package backend.academy.project.readers;

import backend.academy.project.readers.exception.OpeningFileException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class LocalFileLogsReader implements LogsReader {

    private final FileSearcher searcher = new FileSearcher();

    @Override
    public Stream<String> readLogLines(String globPath) {
        Path currentPath = Paths.get("src", "main", "resources").toAbsolutePath();

        List<Path> logFiles = searcher.getLogFiles(globPath, currentPath);
        Stream<String> logRecordStream = Stream.empty();
        for (Path logFile : logFiles) {
            try {
                logRecordStream = Stream.concat(logRecordStream, Files.lines(logFile));
            } catch (IOException e) {
                throw new OpeningFileException("Can't open file " + logFile, e);
            }
        }
        return logRecordStream;
    }
}
