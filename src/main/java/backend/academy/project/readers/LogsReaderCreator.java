package backend.academy.project.readers;

import backend.academy.project.readers.exception.EmptyOrNullPathException;

/**
 * Утилитарный класс, определяющий тип считывателя по аргументу --path
 */
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
        // возвращается дефолтный считыватель,
        // так как проверка на локальный путь (glob) может быть необоснованно сложной
        // в случае ошибки пользователя информация не будет найдена по локальному пути
        return new LocalFileLogsReader(path);
    }
}
