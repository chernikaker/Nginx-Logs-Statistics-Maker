package backend.academy.project.report.view;

import backend.academy.Main;
import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.report.data.AnswerCodeContainer;
import backend.academy.project.report.data.LogInfoReport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MarkdownStatisticsFileWriter extends StatisticsFileWriter {

    private static final String HORIZ_DELIM = ":---------:";
    private static final String VERT_DELIM = "|";

    protected MarkdownStatisticsFileWriter() {
        super("statistics_report.md");
    }

    @Override
    protected String makeReportText(LogInfoReport report, CommandLineArgs args,  List<String> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Logs statictics report ").append('\n');
        sb.append(makeSourceTable(sources));
        sb.append(makeCommonInfoTable(report, args));
        sb.append(makeResourcesTable(report));
        sb.append(makeAnswerCodeTable(report));
        return sb.toString();
    }

    private String makeCommonInfoTable(LogInfoReport report, CommandLineArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Common info").append('\n');
        sb.append(VERT_DELIM).append("Metric").append(VERT_DELIM).append("Value").append(VERT_DELIM).append('\n');
        sb.append(VERT_DELIM).append(HORIZ_DELIM).append(VERT_DELIM).append(HORIZ_DELIM).append(VERT_DELIM).append('\n');
        sb.append(VERT_DELIM).append("Start date").append(VERT_DELIM).append(args.from().isPresent() ? args.from().get():"-").append(VERT_DELIM).append('\n');
        sb.append(VERT_DELIM).append("End date").append(VERT_DELIM).append(args.to().isPresent() ? args.to().get():"-").append(VERT_DELIM).append('\n');
        sb.append(VERT_DELIM).append("Request number").append(VERT_DELIM).append(report.logsCount()).append(VERT_DELIM).append('\n');
        sb.append(VERT_DELIM).append("Average answer size").append(VERT_DELIM).append(report.avgAnswerSize()).append(VERT_DELIM).append('\n');
        sb.append(VERT_DELIM).append("95p of answer size").append(VERT_DELIM).append(report.percentile95AnswerSize()).append(VERT_DELIM).append('\n');
        return sb.toString();
    }

    private String makeResourcesTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Most frequently used resources (top "+TOP_RESULTS+")").append('\n');
        sb.append(VERT_DELIM).append("Resource").append(VERT_DELIM).append("Usages").append(VERT_DELIM).append('\n');
        sb.append(VERT_DELIM).append(HORIZ_DELIM).append(VERT_DELIM).append(HORIZ_DELIM).append(VERT_DELIM).append('\n');
        List<Map.Entry<String, Long>> topResources = getTopByFrequency(report.resourceFrequency());
        for (Map.Entry<String, Long> entry : topResources) {
            sb.append(VERT_DELIM).append(entry.getKey()).append(VERT_DELIM).append(entry.getValue()).append('\n');
        }
        return sb.toString();
    }

    private String makeAnswerCodeTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Most frequently appeared answer codes (top "+TOP_RESULTS+")").append('\n');
        sb.append(VERT_DELIM).append("Code").append(VERT_DELIM).append("Description").append(VERT_DELIM).append("Amount").append(VERT_DELIM).append('\n');
        sb.append(VERT_DELIM).append(HORIZ_DELIM).append(VERT_DELIM).append(HORIZ_DELIM).append(VERT_DELIM).append(HORIZ_DELIM).append(VERT_DELIM).append('\n');
        List<Map.Entry<Integer, Long>> topAnswers = getTopByFrequency(report.codeAnswerFrequency());
        for (Map.Entry<Integer, Long> entry : topAnswers) {
            sb.append(VERT_DELIM).append(entry.getKey()).append(VERT_DELIM).append(AnswerCodeContainer.getAnswerInfoByCode(
                entry.getKey())).append(VERT_DELIM).append(entry.getValue()).append('\n');
        }
        return sb.toString();
    }

    private String makeSourceTable(List<String> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append("## Processed source files or URL").append('\n');
        sb.append(VERT_DELIM).append("Source").append(VERT_DELIM).append('\n');
        sb.append(VERT_DELIM).append(HORIZ_DELIM).append(VERT_DELIM).append('\n');
        for (String source : sources) {
            sb.append(VERT_DELIM).append(source).append(VERT_DELIM).append('\n');
        }
        return sb.toString();
    }

}
