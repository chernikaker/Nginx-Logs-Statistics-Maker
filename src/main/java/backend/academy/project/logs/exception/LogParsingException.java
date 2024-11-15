package backend.academy.project.logs.exception;

public class LogParsingException extends RuntimeException {

    public LogParsingException(String message) {
        super(message);
    }

    public LogParsingException(String message, Throwable cause) {
        super(message, cause);
    }

}
