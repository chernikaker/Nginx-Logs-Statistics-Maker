package backend.academy.project.readers.exception;

public class LogFilesNotFound extends RuntimeException {

    public LogFilesNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
