package backend.academy.project.readers;

import backend.academy.project.logs.LogRecord;
import java.util.List;
import java.util.stream.Stream;

public interface LogsReader {

    Stream<LogRecord> readLogLines();

    List<String> getLogFileNames();
}
