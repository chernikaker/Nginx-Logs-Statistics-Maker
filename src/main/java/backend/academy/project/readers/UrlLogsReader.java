package backend.academy.project.readers;

import backend.academy.project.logs.LogRecord;
import backend.academy.project.logs.LogRecordParser;
import backend.academy.project.logs.exception.LogParsingException;
import backend.academy.project.readers.exception.ReadingFromUrlException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import static com.google.common.net.HttpHeaders.USER_AGENT;

public class UrlLogsReader extends LogsReader {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private final String path;

    public UrlLogsReader(String path) {
        this.path = path;
    }

    @Override
    public Stream<LogRecord> readLogLines() {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(path))
            .header("User-Agent", USER_AGENT)
            .GET()
            .build();

        try {
            Stream<LogRecord> logs = HTTP_CLIENT
                .send(request, HttpResponse.BodyHandlers.ofString())
                .body()
                .lines()
                .map(tryParseLog)
                .filter(Objects::nonNull);
            logSourceNames.add(path);
            return logs;

        } catch (IOException | InterruptedException e) {
            throw new ReadingFromUrlException("Error while reading log lines from " + path, e);
        }
    }

}
