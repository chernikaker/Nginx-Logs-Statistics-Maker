package backend.academy.project.readers;

import backend.academy.project.logs.LogRecord;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class LocalFileLogsReader extends LogsReader {

    // по умолчанию корневой директорией поиска является текущая директория
    private static final Path DEFAULT_ROOT_PATH = Paths.get("").toAbsolutePath();
    private final Path rootPath;
    private final String globPath;


    public LocalFileLogsReader(String globPath, Path rootPath) {
        this.globPath = globPath;
        this.rootPath = rootPath;
    }

    public LocalFileLogsReader(String globPath) {
        this(globPath, DEFAULT_ROOT_PATH);
    }

    @Override
    public Stream<LogRecord> readLogLines() {
        logSourceNames.clear();
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
                // в случае успешной обработки строк файла в логи добавляем его в информацию
                logRecordStream = Stream.concat(logRecordStream, logs);
                logSourceNames.add(fileName.toString());
            } catch (Exception e) {
                // в случае ошибки при работе с файлом пропускаем его, давая возможность обработать остальные
                logger.warn("Can't process file {}. Current file skipped", logFile, e);
            }
        }
        return logRecordStream;
    }
}
