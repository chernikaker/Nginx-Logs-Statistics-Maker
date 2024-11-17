package backend.academy.project.report.view;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.commandline.FilterFieldType;
import backend.academy.project.logs.RequestType;
import backend.academy.project.report.data.LogInfoReport;
import backend.academy.project.report.view.exception.PathIsNotDirectoryException;
import backend.academy.project.report.view.exception.WritingToFileException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import static backend.academy.project.report.data.AnswerCodeCollection.getAnswerInfoByCode;

/**
 * Класс, формирующий текстовый отчет из данных и записывающий его в файл
 */
public abstract class StatisticsWriter {

    protected static final int TOP_RESULTS = 5;
    protected static final String DOUBLE_FORMAT = "%.6f";
    protected static final int TWO_COLUMNS = 2;
    protected static final int THREE_COLUMNS = 3;
    protected static final int ONE_COLUMN = 1;
    protected final String filename;
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);

    protected StatisticsWriter(String filename) {
        this.filename = filename;
    }

    public void writeResultsToFile(
        Path directoryPath,
        LogInfoReport report,
        CommandLineArgs args,
        List<String> resources
    ) throws FileAlreadyExistsException {
        String reportText = makeReportText(report, args, resources);
        if (Files.isRegularFile(directoryPath)) {
            throw new PathIsNotDirectoryException("Path is a regular file, not directory: " + directoryPath);
        }
        Path filePath = directoryPath.resolve(filename);
        if (Files.exists(filePath)) {
            throw new FileAlreadyExistsException("File already exists: " + filePath);
        }
        try {
            Files.writeString(filePath, reportText, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new WritingToFileException("Can't write statistics to file " + filePath, e);
        }
    }

    protected String makeReportText(LogInfoReport report, CommandLineArgs args,  List<String> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildHeader("Logs statistics report"));
        // таблица используемых ресурсов с логами
        sb.append(makeSourceTable(sources));
        // таблица с общими статистиками
        sb.append(makeCommonInfoTable(report, args));
        // таблица наиболее популярных запрашиваемых ресурсов
        sb.append(makeResourcesTable(report));
        // таблица наиболее популярных кодов ответа с описанием
        sb.append(makeAnswerCodeTable(report));
        // таблица наиболее популярных типов запросов
        sb.append(makeRequestTypeFrequencyTable(report));
        return sb.toString();
    }

    protected String makeCommonInfoTable(LogInfoReport report, CommandLineArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildTableStart("Common info", TWO_COLUMNS));
        sb.append(buildTableHeader(List.of("Metric", "Value")));
        // далее параметры введенные пользователем
        // дата from
        String fromDateView =  args.from().isPresent() ? args.from().orElseThrow().format(formatter) : "-";
        sb.append(buildRow(List.of("Date from", fromDateView)));
        // дата to
        String toDateView = args.to().isPresent() ? args.to().orElseThrow().format(formatter) : "-";
        sb.append(buildRow(List.of("Date to", toDateView)));
        // фильтр по полю (поле + значение)
        String filterView =
            args.filterField() == FilterFieldType.NONE
            ? "-"
            : args.filterField() + " = " + args.filterValue();
        sb.append(buildRow(List.of("Filter", filterView)));
        String logCountView = String.valueOf(report.logsCount());
        // далее общие статистики
        sb.append(buildRow(List.of("Logs amount", logCountView)));
        String ipCountView = String.valueOf(report.logsCount());
        sb.append(buildRow(List.of("Unique IP amount", ipCountView)));
        String bytesSentView = String.format(DOUBLE_FORMAT, report.avgAnswerSize());
        sb.append(buildRow(List.of("Average bytes sent", bytesSentView)));
        String percentileView = String.format(DOUBLE_FORMAT, report.percentile95AnswerSize());
        sb.append(buildRow(List.of("95 percentile of bytes sent", percentileView)));
        sb.append(buildTableEnd());
        return sb.toString();
    }

    protected String makeResourcesTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildTableStart("Most frequently used resources (top " + TOP_RESULTS + ")", TWO_COLUMNS));
        sb.append(buildTableHeader(List.of("Resource", "Usages")));
        List<Map.Entry<String, Long>> topResources = getTopByFrequency(report.resourceFrequency(), TOP_RESULTS);
        for (Map.Entry<String, Long> entry : topResources) {
            String resource = entry.getKey();
            String usage = entry.getValue().toString();
            sb.append(buildRow(List.of(resource, usage)));
        }
        sb.append(buildTableEnd());
        return sb.toString();
    }

    protected String makeRequestTypeFrequencyTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildTableStart("HTTP request types frequency", TWO_COLUMNS));
        sb.append(buildTableHeader(List.of("Type", "Occurrences")));
        Map<RequestType, Long> requestTypes = report.requestTypeFrequency();
        for (Map.Entry<RequestType, Long> entry : getTopByFrequency(requestTypes, requestTypes.size())) {
            String type = entry.getKey().toString();
            String amount = entry.getValue().toString();
            sb.append(buildRow(List.of(type, amount)));
        }
        sb.append(buildTableEnd());
        return sb.toString();
    }

    protected String makeAnswerCodeTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildTableStart("Most frequently appeared answer codes (top " + TOP_RESULTS + ")", THREE_COLUMNS));
        sb.append(buildTableHeader(List.of("Code", "Description", "Amount")));
        List<Map.Entry<Integer, Long>> topAnswers = getTopByFrequency(report.codeAnswerFrequency(), TOP_RESULTS);
        for (Map.Entry<Integer, Long> entry : topAnswers) {
            String code = entry.getKey().toString();
            String description = getAnswerInfoByCode(entry.getKey());
            String amount = entry.getValue().toString();
            sb.append(buildRow(List.of(code, description, amount)));
        }
        sb.append(buildTableEnd());
        return sb.toString();
    }

    protected String makeSourceTable(List<String> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildTableStart(" Processed source files or URL", ONE_COLUMN));
        sb.append(buildTableHeader(List.of("Source")));
        for (String source : sources) {
            sb.append(buildRow(List.of(source)));
        }
        sb.append(buildTableEnd());
        return sb.toString();
    }

    protected <K> List<Map.Entry<K, Long>> getTopByFrequency(Map<K, Long> data, int limit) {
        return data.entrySet()
            .stream()
            .sorted(Map.Entry.<K, Long>comparingByValue().reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }

    protected abstract String buildCell(String data);

    protected abstract String buildTableHeader(List<String> names);

    protected abstract String buildRow(List<String> data);

    protected abstract String buildTableStart(String name, int cols);

    protected abstract String buildTableEnd();

    protected abstract String buildHeader(String info);
}
