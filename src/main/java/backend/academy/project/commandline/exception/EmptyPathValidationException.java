package backend.academy.project.commandline.exception;

public class EmptyPathValidationException extends RuntimeException {

    public EmptyPathValidationException(String message) {
        super(message);
    }
}
