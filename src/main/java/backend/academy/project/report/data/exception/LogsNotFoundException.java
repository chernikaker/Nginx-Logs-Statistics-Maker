package backend.academy.project.report.data.exception;

public class LogsNotFoundException extends RuntimeException {

    public LogsNotFoundException(String message) {
        super(message);
    }
}
