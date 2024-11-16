package backend.academy.project.readers;

import backend.academy.project.readers.exception.EmptyOrNullPathException;

public class LogsReaderCreator {

    private static final String URL_REGEX = "https?:\\/\\/[^\\s/$.?#].[^\\s]*";

    private LogsReaderCreator() {}

    public static LogsReader getReaderByPath(String path) {
        if (path == null || path.isBlank()) {
            throw new EmptyOrNullPathException("Path cannot be null or blank");
        }
        if (path.matches(URL_REGEX)) {
            return new UrlLogsReader(path);
        }
        return new LocalFileLogsReader(path);
    }
}
