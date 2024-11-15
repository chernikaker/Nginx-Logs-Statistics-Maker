package backend.academy.project.commandline.exception;

public class DateValidationException extends ValidationException {

    public DateValidationException(String message) {
        super(message);
    }

    public DateValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
