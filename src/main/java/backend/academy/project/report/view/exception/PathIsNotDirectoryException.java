package backend.academy.project.report.view.exception;

public class PathIsNotDirectoryException extends RuntimeException {

    public PathIsNotDirectoryException(String message) {
        super(message);
    }
}
