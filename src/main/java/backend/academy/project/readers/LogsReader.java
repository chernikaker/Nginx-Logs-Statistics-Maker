package backend.academy.project.readers;

import java.util.stream.Stream;

public interface LogsReader {

    Stream<String> readLogLines(String path);
}
