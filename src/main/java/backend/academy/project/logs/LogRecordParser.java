package backend.academy.project.logs;

import backend.academy.project.logs.exception.LogParsingException;
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

/**
 *  Утилитарный класс, отвечающий за парсинг лога из строки на основе регулярных выражений
 */
@SuppressWarnings("MagicNumber")
public class LogRecordParser {

    private static final String STANDARD_IP_REGEX = "((?:\\d{1,3}\\.){3}\\d{1,3})";
    private static final String IPV6_SYMBOLS_REGEX = "^[0-9a-fA-F:]+$";
    private static final String LOG_REGEX = "(\\S+) - (\\S+) \\[(.*?)] \"(.*?)\" (\\d{3}) (\\d+) \"(.*?)\" \"(.*?)\"";
    private static final String REQUEST_REGEX = "(GET|POST|PUT|DELETE|HEAD|OPTIONS|PATCH) (.+?) (HTTP/\\d+\\.\\d+$)";

    private static final int MAX_IP_BLOCK_VALUE = 255;
    private static final int IPV6_PARTS = 8;
    private static final int IPV6_BLOCK_LEN = 4;
    private static final int IPV6_BLOCKS_WITHOUT_SKIP = 2;

    private LogRecordParser() {}

    public static LogRecord parseLog(String logLine) {
        // проверка общего шаблона
        Pattern logPattern = Pattern.compile(LOG_REGEX);
        Matcher logMatcher = logPattern.matcher(logLine);
        if (!logMatcher.matches()) {
            throw new LogParsingException("Can't parse log, invalid format: " + logLine);
        }
        // проверка IP - обычного или Ipv6
        String remoteIp = logMatcher.group(1);
        if (!isValidStandardIp(remoteIp) && !isValidIpv6(remoteIp)) {
            throw new LogParsingException("Ip address has invalid format: " + remoteIp);
        }
        // парсинг локального времени из строки
        String localDate = logMatcher.group(3);
        LocalDateTime dateTime = parseDateFromLogFormat(localDate);
        // разделение запроса на тип, ресурс и HTTP версию
        String request = logMatcher.group(4);
        Pattern requestPattern = Pattern.compile(REQUEST_REGEX);
        Matcher requestMatcher = requestPattern.matcher(request);
        if (!requestMatcher.matches()) {
            throw new LogParsingException("Can't parse request: " + request);
        }
        String remoteUser = logMatcher.group(2);
        RequestType requestType = RequestType.valueOf(requestMatcher.group(1));
        String resource = requestMatcher.group(2);
        String httpVersion = requestMatcher.group(3);
        int status = Integer.parseInt(logMatcher.group(5));
        int bytesSent = Integer.parseInt(logMatcher.group(6));
        String httpReferer = logMatcher.group(7);
        String httpUserAgent = logMatcher.group(8);
        return new LogRecord(
            remoteIp,
            remoteUser,
            dateTime,
            requestType,
            resource,
            httpVersion,
            status,
            bytesSent,
            httpReferer,
            httpUserAgent
        );
    }

    /**
     * Проверяет строку на соответствие стандартному формату IP.<br>
     * Стандартный формат: n.n.n.n, где n - число в диапазоне [0,255]
     * @param ip Строковое представление IP
     * @return Является ли строка стандартным IP
     */
    private static boolean isValidStandardIp(String ip) {
        // проверка структуры
        if (!ip.matches(STANDARD_IP_REGEX)) {
            return false;
        }
        String[] ipParts =  ip.split("\\.");
        // проверка значений блоков
        for (String ipPart : ipParts) {
            int partValue = Integer.parseInt(ipPart);
            if (partValue > MAX_IP_BLOCK_VALUE) {
                return false;
            }
        }
        return true;
    }

    /**
     * Проверяет строку на соответствие формату IPv6.<br><br>
     * Полный формат: n:n:n:n:n:n:n:n, где n - число в диапазоне [0,FFFF] в 16с/с<br><br>
     * Сокращенный формат: n:n::n:n, где :: - некоторое число блоков вида 0000.
     * Может встечаться только 1 раз и стоять в любой части IP.
     * @param ip Строковое представление IP
     * @return Является ли строка IPv6
     */
    private static boolean isValidIpv6(String ip) {
        //проверка  символов строки
        if (!ip.matches(IPV6_SYMBOLS_REGEX)) {
            return false;
        }
        // проверка сокращения :: на единственность
        String[] partsWithNoEmptyBlocks =  ip.split("::", -1);
        if (partsWithNoEmptyBlocks.length > IPV6_BLOCKS_WITHOUT_SKIP) {
            return false;
        }
        // проверка существования сокращения
        boolean emptyParts = ip.contains("::");
        // разделение на блоки и проверка каждого блока по длине
        List<String> ipParts = new ArrayList<>();
        for (String ipPart : partsWithNoEmptyBlocks) {
             Collections.addAll(ipParts, ipPart.split(":"));
        }
        for (String part : ipParts) {
            if (part.length() > IPV6_BLOCK_LEN) {
                return false;
            }
        }
        // если пропуска блоков не было - в IP содержится ровно 8 частей
        // иначе - строго меньше
        return (!emptyParts && ipParts.size() == IPV6_PARTS)
            || (emptyParts && ipParts.size() < IPV6_PARTS);
    }

    /**
     * Парсит строку с временем nginx лога в LocalDateTime.<br><br>
     * В nginx логах время определяется шаблоном dd/MMM/yyyy:HH:mm:ss Z
     * @param logDate Строковое представление даты
     * @return LocalDateTime объект
     * @throws LogParsingException если невозможно спарсить дату
     */
    private static LocalDateTime parseDateFromLogFormat(String logDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(logDate, formatter);
            return offsetDateTime.toLocalDateTime();
        } catch (DateTimeParseException e) {
            throw new LogParsingException("Can't parse date: " + logDate, e);
        }
    }
}
