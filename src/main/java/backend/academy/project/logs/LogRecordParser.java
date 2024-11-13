package backend.academy.project.logs;

import backend.academy.project.logs.exception.ParsingLogException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"LineLength", "MagicNumber"})
public class LogRecordParser {

    private static final String STANDARD_IP_REGEX = "((?:\\d{1,3}\\.){3}\\d{1,3})";
    private static final String IPV6_SYMBOLS_REGEX = "^[0-9a-fA-F:]+$";
    private static final String LOG_REGEX = "(\\S+) - (\\S+) \\[(.*?)\\] \\\"(.*?)\\\" (\\d{3}) (\\d+) \\\"(.*?)\\\" \\\"(.*?)\\\"";
    private static final String REQUEST_REGEX = "(GET|POST|PUT|DELETE|HEAD|OPTIONS|PATCH) (.+?) (HTTP\\/\\d+\\.\\d+$)";

    public LogRecord parseLog(String logLine) {
        Pattern logPattern = Pattern.compile(LOG_REGEX);
        Matcher logMatcher = logPattern.matcher(logLine);
        if (!logMatcher.matches()) {
            throw new ParsingLogException("Can't parse log, invalid format: " + logLine);
        }
        String remoteIp = logMatcher.group(1);
        if (!isValidStardardIp(remoteIp) && !isValidIpv6(remoteIp)) {
            throw new ParsingLogException("Ip address has invalid format: " + remoteIp);
        }
        String request = logMatcher.group(4);
        Pattern requestPattern = Pattern.compile(REQUEST_REGEX);
        Matcher requestMatcher = requestPattern.matcher(request);
        if (!requestMatcher.matches()) {
            throw new ParsingLogException("Can't parse request: " + request);
        }
        String localDate = logMatcher.group(3);
        LocalDateTime dateTime = parseDateFromLogFormat(localDate);
        int status = Integer.parseInt(logMatcher.group(5));
        int bytesSent = Integer.parseInt(logMatcher.group(6));
        return new LogRecord(
            remoteIp,
            logMatcher.group(2),
            dateTime,
            RequestType.valueOf(requestMatcher.group(1)),
            requestMatcher.group(2),
            requestMatcher.group(3),
            status,
            bytesSent,
            logMatcher.group(7),
            logMatcher.group(8));
    }

    private boolean isValidStardardIp(String ip) {
        if (!ip.matches(STANDARD_IP_REGEX)) {
            return false;
        }
        String[] ipParts =  ip.split("\\.");
        for (String ipPart : ipParts) {
            int partValue = Integer.parseInt(ipPart);
            if (partValue > 255) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidIpv6(String ip) {
        if (!ip.matches(IPV6_SYMBOLS_REGEX)) {
            return false;
        }
        String[] partsWithNoEmptyBlocks =  ip.split("::", -1);
        if (partsWithNoEmptyBlocks.length > 2) {
            return false;
        }
        boolean emptyParts = ip.contains("::");
        List<String> ipParts = new ArrayList<>();
        for (String ipPart : partsWithNoEmptyBlocks) {
             Collections.addAll(ipParts, ipPart.split(":"));
        }
        for (String part : ipParts) {
            if (part.length() > 4) {
                return false;
            }
        }
        return (!emptyParts && ipParts.size() == 8) || (emptyParts && ipParts.size() < 8);
    }

    private LocalDateTime parseDateFromLogFormat(String logDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(logDate, formatter);
            return offsetDateTime.toLocalDateTime();
        } catch (DateTimeParseException e) {
            throw new ParsingLogException("Can't parse date: " + logDate, e);
        }
    }
}
