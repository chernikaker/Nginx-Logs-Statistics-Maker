package backend.academy.project.logs.exception;

public class ParsingLogException extends RuntimeException {

    public ParsingLogException(String message) {
        super(message);
    }

    public ParsingLogException(String message, Throwable cause) {
        super(message, cause);
    }

}
