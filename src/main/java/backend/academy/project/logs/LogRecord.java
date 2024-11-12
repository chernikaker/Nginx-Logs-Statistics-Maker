package backend.academy.project.logs;

import java.time.LocalDateTime;

public record LogRecord
    (String remoteAddress,
     String remoteUser,
     LocalDateTime timeLocal,
     RequestType requestType,
     String requestedResource,
     String HTTPVersion,
     int status,
     int bytesSent,
     String httpReferer,
     String httpUserAgent
    )
{}
