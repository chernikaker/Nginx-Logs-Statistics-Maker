package backend.academy.project.readers.exception;

public class EmptyOrNullPathException extends RuntimeException {

    public EmptyOrNullPathException(String message) {
        super(message);
    }
}
