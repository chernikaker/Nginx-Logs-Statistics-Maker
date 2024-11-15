package backend.academy.project.readers.exception;

public class FindingFilesException extends RuntimeException {

    public FindingFilesException(String message) {
        super(message);
    }

    public FindingFilesException(String message, Throwable cause) {
        super(message, cause);
    }
}
