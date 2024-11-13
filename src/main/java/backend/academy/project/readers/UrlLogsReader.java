package backend.academy.project.readers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;
import static com.google.common.net.HttpHeaders.USER_AGENT;

public class UrlLogsReader implements LogsReader {

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    @Override
    public Stream<String> readLogLines(String path) {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(path))
            .header("User-Agent", USER_AGENT)
            .GET()
            .build();

        try {
            return HTTP_CLIENT
                .send(request, HttpResponse.BodyHandlers.ofString())
                .body()
                .lines();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error while reading log lines from " + path, e);
        }
    }
}
