package backend.academy.project.logs;

import backend.academy.project.commandline.FilterFieldType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogRecord {

    private String remoteAddress;
    private String remoteUser;
    private LocalDateTime timeLocal;
    private RequestType requestType;
    private String requestResource;
    private String httpVersion;
    private int status;
    private long bytesSent;
    private String httpReferer;
    private String httpUserAgent;

    public String getValueByFieldName(FilterFieldType type) {
        return switch (type) {
            case REMOTE_ADDRESS -> remoteAddress;
            case REMOTE_USER -> remoteUser;
            case TIME_LOCAL -> timeLocal.toString();
            case REQUEST_TYPE -> requestType.toString();
            case REQUEST_RESOURCE -> requestResource;
            case HTTP_VERSION -> httpVersion;
            case STATUS -> String.valueOf(status);
            case BYTES_SENT -> String.valueOf(bytesSent);
            case HTTP_REFERER -> httpReferer;
            case HTTP_USERAGENT -> httpUserAgent;
            case NONE -> null;
        };
    }
}

