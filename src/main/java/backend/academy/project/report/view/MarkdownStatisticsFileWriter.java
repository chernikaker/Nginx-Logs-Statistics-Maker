package backend.academy.project.report.view;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.commandline.FilterFieldType;
import backend.academy.project.logs.RequestType;
import backend.academy.project.report.data.LogInfoReport;
import java.util.List;
import java.util.Map;
import static backend.academy.project.report.data.AnswerCodeCollection.getAnswerInfoByCode;

public class MarkdownStatisticsFileWriter extends StatisticsFileWriter {

    private static final String HORIZ_DELIM = ":---------:";
    private static final String VERT_DELIM = "|";

    protected MarkdownStatisticsFileWriter(String filename) {
        super(filename+".md");
    }

    @Override
    protected String makeReportText(LogInfoReport report, CommandLineArgs args,  List<String> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Logs statictics report ").append('\n');
        sb.append(makeSourceTable(sources));
        sb.append(makeCommonInfoTable(report, args));
        sb.append(makeResourcesTable(report));
        sb.append(makeAnswerCodeTable(report));
        sb.append(makeRequestTypeFrequencyTable(report));
        return sb.toString();
    }

    private String makeCommonInfoTable(LogInfoReport report, CommandLineArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Common info").append('\n');
        sb.append(buildTableHeader(List.of("Metric", "Value")));
        sb.append(buildCell("Date from"));
        sb.append(buildCell(args.from().isPresent() ? args.from().get().toString() : "-"));
        sb.append(buildRowEnd());
        sb.append(buildCell("Date to"));
        sb.append(buildCell(args.to().isPresent() ? args.to().get().toString() : "-"));
        sb.append(buildRowEnd());
        sb.append(buildCell("Filter "));
        sb.append(buildCell(args.filterField() == FilterFieldType.NONE ? "-" : args.filterField()+" = "+args.filterValue()));
        sb.append(buildRowEnd());
        sb.append(buildCell("Logs amount"));
        sb.append(buildCell(String.valueOf(report.logsCount())));
        sb.append(buildRowEnd());
        sb.append(buildCell("Unique IP amount"));
        sb.append(buildCell(String.valueOf(report.uniqueIPCount())));
        sb.append(buildRowEnd());
        sb.append(buildCell("Average bytes sent"));
        sb.append(buildCell(String.format("%.6f",report.avgAnswerSize())));
        sb.append(buildRowEnd());
        sb.append(buildCell("95p bytes sent"));
        sb.append(buildCell(String.format("%.6f",report.percentile95AnswerSize())));
        sb.append(buildRowEnd());
        return sb.toString();
    }

    private String makeResourcesTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Most frequently used resources (top "+TOP_RESULTS+")").append('\n');
        sb.append(buildTableHeader(List.of("Resource", "Usages")));
        List<Map.Entry<String, Long>> topResources = getTopByFrequency(report.resourceFrequency(), TOP_RESULTS);
        for (Map.Entry<String, Long> entry : topResources) {
            sb.append(buildCell(entry.getKey()));
            sb.append(buildCell(entry.getValue().toString()));
            sb.append(VERT_DELIM).append('\n');
        }
        return sb.toString();
    }

    private String makeRequestTypeFrequencyTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("## HTTP request types frequency").append('\n');
        sb.append(buildTableHeader(List.of("Type", "Amount")));
        Map<RequestType, Long> requestTypes = report.requestTypeFrequency();
        for (Map.Entry<RequestType, Long> entry : getTopByFrequency(requestTypes, requestTypes.size())) {
            sb.append(buildCell(entry.getKey().toString()));
            sb.append(buildCell(entry.getValue().toString()));
            sb.append(VERT_DELIM).append('\n');
        }
        return sb.toString();
    }

    private String makeAnswerCodeTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Most frequently appeared answer codes (top "+TOP_RESULTS+")").append('\n');
        sb.append(buildTableHeader(List.of("Code", "Description", "Amount")));
        List<Map.Entry<Integer, Long>> topAnswers = getTopByFrequency(report.codeAnswerFrequency(), TOP_RESULTS);
        for (Map.Entry<Integer, Long> entry : topAnswers) {
            sb.append(buildCell(entry.getKey().toString()));
            sb.append(buildCell(getAnswerInfoByCode(entry.getKey())));
            sb.append(buildCell(entry.getValue().toString()));
            sb.append(VERT_DELIM).append('\n');
        }
        return sb.toString();
    }

    private String makeSourceTable(List<String> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Processed source files or URL").append('\n');
        sb.append(buildTableHeader(List.of("Source")));
        for (String source : sources) {
            sb.append(buildCell(source));
            sb.append(VERT_DELIM).append('\n');
        }
        return sb.toString();
    }

    private String buildCell(String data){
        return VERT_DELIM+data;
    }

    private String buildRowEnd(){
        return VERT_DELIM+'\n';
    }

    private String buildTableHeader(List<String> names) {
        StringBuilder sb = new StringBuilder(VERT_DELIM);
        for (String name : names) {
            sb.append(name).append(VERT_DELIM);
        }
        sb.append('\n').append(VERT_DELIM);
        for (int i = 0; i < names.size(); i++) {
            sb.append(HORIZ_DELIM).append(VERT_DELIM);
        }
        sb.append('\n');
        return sb.toString();
    }
}
