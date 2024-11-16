package backend.academy.project.readers;

import backend.academy.project.logs.LogRecord;
import backend.academy.project.logs.LogRecordParser;
import backend.academy.project.logs.exception.LogParsingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LogsReader {

    protected Logger logger =  LogManager.getLogger();
    protected final List<String> logSourceNames = new ArrayList<>();

    protected final Function<String, LogRecord> tryParseLog = (log -> {
            try {
                return LogRecordParser.parseLog(log);
            } catch (LogParsingException e) {
                logger.warn("{} Current line skipped", e.getMessage());
                return null;
            }
        });

    public abstract Stream<LogRecord> readLogLines();

    public List<String> getLogSourceNames() {
        return Collections.unmodifiableList(logSourceNames);
    }

}
