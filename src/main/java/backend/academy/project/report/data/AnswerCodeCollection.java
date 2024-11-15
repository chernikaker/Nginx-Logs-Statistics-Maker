package backend.academy.project.report.data;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class AnswerCodeCollection {

    private static final Map<Integer, String> codeNames;

    static {
        codeNames = new HashMap<>();
        codeNames.put(100, "Continue");
        codeNames.put(101, "Switching Protocols");
        codeNames.put(102, "Processing");
        codeNames.put(103, "Early Hints");
        codeNames.put(200, "OK");
        codeNames.put(201, "Created");
        codeNames.put(202, "Accepted");
        codeNames.put(203, "Non-Authoritative Information");
        codeNames.put(204, "No Content");
        codeNames.put(205, "Reset Content");
        codeNames.put(206, "Partial Content");
        codeNames.put(207, "Multi-Status");
        codeNames.put(208, "Already Reported");
        codeNames.put(226, "IM Used");
        codeNames.put(300, "Multiple Choices");
        codeNames.put(301, "Moved Permanently");
        codeNames.put(302, "Found");
        codeNames.put(303, "See Other");
        codeNames.put(304, "Not Modified");
        codeNames.put(305, "Use Proxy");
        codeNames.put(307, "Temporary Redirect");
        codeNames.put(308, "Permanent Redirect");
        codeNames.put(400, "Bad Request");
        codeNames.put(401, "Unauthorized");
        codeNames.put(402, "Payment Required");
        codeNames.put(403, "Forbidden");
        codeNames.put(404, "Not Found");
        codeNames.put(405, "Method Not Allowed");
        codeNames.put(406, "Not Acceptable");
        codeNames.put(407, "Proxy Authentication Required");
        codeNames.put(408, "Request Timeout");
        codeNames.put(409, "Conflict");
        codeNames.put(410, "Gone");
        codeNames.put(411, "Length Required");
        codeNames.put(412, "Precondition Failed");
        codeNames.put(413, "Content Too Large");
        codeNames.put(414, "URI Too Long");
        codeNames.put(415, "Unsupported Media Type");
        codeNames.put(416, "Range Not Satisfiable");
        codeNames.put(417, "Expectation Failed");
        codeNames.put(418, "I'm a teapot");
        codeNames.put(421, "Misdirected Request");
        codeNames.put(422, "Unprocessable Content (WebDAV)");
        codeNames.put(423, "Locked (WebDAV)");
        codeNames.put(424, "Failed Dependency (WebDAV)");
        codeNames.put(425, "Too Early Experimental");
        codeNames.put(426, "Upgrade Required");
        codeNames.put(428, "Precondition Required");
        codeNames.put(429, "Too Many Requests");
        codeNames.put(431, "Request Header Fields Too Large");
        codeNames.put(451, "Unavailable For Legal Reasons");
        codeNames.put(500, "Internal Server Error");
        codeNames.put(501, "Not Implemented");
        codeNames.put(502, "Bad Gateway");
        codeNames.put(503, "Service Unavailable");
        codeNames.put(504, "Gateway Timeout");
        codeNames.put(505, "HTTP Version Not Supported");
        codeNames.put(506, "Variant Also Negotiates");
        codeNames.put(507, "Insufficient Storage (WebDAV)");
        codeNames.put(508, "Loop Detected (WebDAV)");
        codeNames.put(510, "Not Extended");
        codeNames.put(511, "Network Authentication Required");
    }

    public static String getAnswerInfoByCode(int code) {
        String answer = codeNames.get(code);
        if (answer == null) {
            throw new NoSuchElementException("Code is not found: " + code);
        }
        return answer;
    }
}
