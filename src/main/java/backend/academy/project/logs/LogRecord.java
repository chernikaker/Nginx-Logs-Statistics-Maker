package backend.academy.project.logs;

import backend.academy.project.commandline.FilterFieldType;
import java.time.LocalDateTime;

@SuppressWarnings("RecordComponentNumber")
public record LogRecord(
     String remoteAddress,
     String remoteUser,
     LocalDateTime timeLocal,
     RequestType requestType,
     String requestResource,
     String httpVersion,
     int status,
     long bytesSent,
     String httpReferer,
     String httpUserAgent
    ) {

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

