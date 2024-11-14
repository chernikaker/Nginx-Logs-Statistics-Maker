package backend.academy.project.logs;

import java.time.LocalDateTime;

@SuppressWarnings("RecordComponentNumber")
public record LogRecord(
     String remoteAddress,
     String remoteUser,
     LocalDateTime timeLocal,
     RequestType requestType,
     String requestedResource,
     String httpVersion,
     int status,
     long bytesSent,
     String httpReferer,
     String httpUserAgent
    )
{}
