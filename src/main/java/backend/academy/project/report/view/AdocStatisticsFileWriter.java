package backend.academy.project.report.view;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.logs.RequestType;
import backend.academy.project.report.data.LogInfoReport;
import static  backend.academy.project.report.data.AnswerCodeContainer.getAnswerInfoByCode;
import java.util.List;
import java.util.Map;


public class AdocStatisticsFileWriter extends StatisticsFileWriter {

    private static final String TABLE_BORDER = "|====";
    private static final String TABLE_CELL = "|";

    protected AdocStatisticsFileWriter(String filename) {
        super(filename+".adoc");
    }

    @Override
    protected String makeReportText(LogInfoReport report, CommandLineArgs args, List<String> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append("= Logs statistics report\n");
        sb.append(makeSourceTable(sources));
        sb.append(makeCommonInfoTable(report, args));
        sb.append(makeResourcesTable(report));
        sb.append(makeAnswerCodeTable(report));
        sb.append(makeRequestFrequencyTable(report));
        return sb.toString();
    }

    private String makeCommonInfoTable(LogInfoReport report, CommandLineArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("== Common info\n");
        sb.append("[cols=2]").append('\n');
        sb.append(TABLE_BORDER).append('\n');
        sb.append(buildTableHeader(List.of("Metric", "Value")));
        sb.append(buildCell("Date from"));
        sb.append(buildCell(args.from().isPresent() ? args.from().get().toString() : "-"));
        sb.append(buildCell("Date to"));
        sb.append(buildCell(args.to().isPresent() ? args.to().get().toString() : "-"));
        sb.append(buildCell("Logs amount"));
        sb.append(buildCell(String.valueOf(report.logsCount())));
        sb.append(buildCell("Unique IP amount"));
        sb.append(buildCell(String.valueOf(report.uniqueIPCount())));
        sb.append(buildCell("Average bytes sent"));
        sb.append(buildCell(String.valueOf(report.avgAnswerSize())));
        sb.append(buildCell("95p bytes sent"));
        sb.append(buildCell(String.valueOf(report.percentile95AnswerSize())));
        sb.append(TABLE_BORDER).append('\n');
        return sb.toString();
    }

    private String makeResourcesTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("== Most frequently used resources (top " + TOP_RESULTS + ")\n");
        sb.append("[cols=2]").append('\n');
        sb.append(TABLE_BORDER).append('\n');
        sb.append(buildTableHeader(List.of("Resource", "Usages")));
        List<Map.Entry<String, Long>> topResources = getTopByFrequency(report.resourceFrequency(), TOP_RESULTS);
        for (Map.Entry<String, Long> entry : topResources) {
            sb.append(buildCell(entry.getKey()));
            sb.append(buildCell(entry.getValue().toString()));
        }
        sb.append(TABLE_BORDER).append('\n');
        return sb.toString();
    }

    private String makeAnswerCodeTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("== Most frequently appeared answer codes (top " + TOP_RESULTS + ")\n");
        sb.append("[cols=3]").append('\n');
        sb.append(TABLE_BORDER).append('\n');
        sb.append(buildTableHeader(List.of("Code", "Description", "Amount")));
        List<Map.Entry<Integer, Long>> topAnswers = getTopByFrequency(report.codeAnswerFrequency(), TOP_RESULTS);
        for (Map.Entry<Integer, Long> entry : topAnswers) {
            sb.append(buildCell(entry.getKey().toString()));
            sb.append(buildCell(getAnswerInfoByCode(entry.getKey())));
            sb.append(buildCell(entry.getValue().toString()));
        }
        sb.append(TABLE_BORDER).append('\n');
        return sb.toString();
    }

    private String makeRequestFrequencyTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("== HTTP request types frequency");
        sb.append("[cols=2]").append('\n');
        sb.append(TABLE_BORDER).append('\n');
        sb.append(buildTableHeader(List.of("Type", "Amount")));
        Map<RequestType, Long> requestTypes = report.requestTypeFrequency();
        for (Map.Entry<RequestType, Long> entry : getTopByFrequency(requestTypes, requestTypes.size())) {
            sb.append(buildCell(entry.getKey().toString()));
            sb.append(buildCell(entry.getValue().toString()));
        }
        sb.append(TABLE_BORDER).append('\n');
        return sb.toString();
    }

    private String makeSourceTable(List<String> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append("== Processed source files or URL\n");
        sb.append("[cols=1]").append('\n');
        sb.append(TABLE_BORDER).append('\n');
        sb.append(buildTableHeader(List.of("Source")));
        for (String source : sources) {
            sb.append(buildCell(source));
        }
        sb.append(TABLE_BORDER).append('\n');
        return sb.toString();
    }

    private String buildCell(String data){
        return TABLE_CELL + data + '\n';
    }

    private String buildTableHeader(List<String> names) {
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            sb.append(TABLE_CELL).append(name).append(' ');
        }
        sb.append('\n');
        return sb.toString();
    }
}
