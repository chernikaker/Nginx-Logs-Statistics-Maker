package backend.academy.project.readers;

public class LogsReaderCreator {

    private static final String URL_REGEX = "https?:\\/\\/[^\\s/$.?#].[^\\s]*";

    private LogsReaderCreator() {}

    public static LogsReader getReaderByPath(String path) {
        if (path.matches(URL_REGEX)) {
            return new UrlLogsReader(path);
        }
        return new LocalFileLogsReader(path);
    }
}
