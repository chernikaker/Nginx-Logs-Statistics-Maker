package backend.academy.project.report.view;

import backend.academy.Main;
import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.report.data.AnswerCodeContainer;
import backend.academy.project.report.data.LogInfoReport;
import java.util.List;
import java.util.Map;


public class AdocStatisticsFileWriter extends StatisticsFileWriter {

    private static final String TABLE_BORDER = "|====";
    private static final String TABLE_CELL = "|";

    protected AdocStatisticsFileWriter() {
        super("statistics_report.adoc");
    }

    @Override
    protected String makeReportText(LogInfoReport report, CommandLineArgs args, List<String> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append("= Logs statistics report\n");
        sb.append(makeSourceTable(sources));
        sb.append(makeCommonInfoTable(report, args));
        sb.append(makeResourcesTable(report));
        sb.append(makeAnswerCodeTable(report));
        return sb.toString();
    }

    private String makeCommonInfoTable(LogInfoReport report, CommandLineArgs args) {
        StringBuilder sb = new StringBuilder();
        sb.append("== Common info\n");
        sb.append("[cols=2]").append('\n');
        sb.append(TABLE_BORDER).append('\n');
        sb.append(TABLE_CELL).append("Metric ").append(TABLE_CELL).append("Value").append("\n");
        sb.append(TABLE_CELL).append("Start date").append('\n').append(TABLE_CELL).append(args.from().isPresent() ? args.from().get() : "-").append("\n");
        sb.append(TABLE_CELL).append("End date").append('\n').append(TABLE_CELL).append(args.to().isPresent() ? args.to().get() : "-").append("\n");
        sb.append(TABLE_CELL).append("Request number").append('\n').append(TABLE_CELL).append(report.logsCount()).append("\n");
        sb.append(TABLE_CELL).append("Average answer size").append('\n').append(TABLE_CELL).append(report.avgAnswerSize()).append("\n");
        sb.append(TABLE_CELL).append("95p of answer size").append('\n').append(TABLE_CELL).append(report.percentile95AnswerSize()).append("\n");
        sb.append(TABLE_BORDER).append('\n');
        return sb.toString();
    }

    private String makeResourcesTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("== Most frequently used resources (top " + TOP_RESULTS + ")\n");
        sb.append("[cols=2]").append('\n');
        sb.append(TABLE_BORDER).append('\n');
        sb.append(TABLE_CELL).append("Resource ").append(TABLE_CELL).append("Usages").append("\n");
        List<Map.Entry<String, Long>> topResources = getTopByFrequency(report.resourceFrequency());
        for (Map.Entry<String, Long> entry : topResources) {
            sb.append(TABLE_CELL).append(entry.getKey()).append('\n').append(TABLE_CELL).append(entry.getValue()).append("\n");
        }
        sb.append(TABLE_BORDER).append('\n');
        return sb.toString();
    }

    private String makeAnswerCodeTable(LogInfoReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("== Most frequently appeared answer codes (top " + TOP_RESULTS + ")\n");
        sb.append("[cols=3]").append('\n');
        sb.append(TABLE_BORDER).append('\n');
        sb.append(TABLE_CELL).append("Code ").append(TABLE_CELL).append("Description ").append(TABLE_CELL).append("Amount").append("\n");
        List<Map.Entry<Integer, Long>> topAnswers = getTopByFrequency(report.codeAnswerFrequency());
        for (Map.Entry<Integer, Long> entry : topAnswers) {
            sb.append(TABLE_CELL+'\n').append(entry.getKey()).append(TABLE_CELL+'\n').append(AnswerCodeContainer.getAnswerInfoByCode(
                entry.getKey())).append(TABLE_CELL+'\n').append(entry.getValue()).append("\n");
        }
        sb.append(TABLE_BORDER).append('\n');
        return sb.toString();
    }

    private String makeSourceTable(List<String> sources) {
        StringBuilder sb = new StringBuilder();
        sb.append("== Processed source files or URL\n");
        sb.append("[cols=2]").append('\n');
        sb.append(TABLE_BORDER).append('\n');
        sb.append(TABLE_CELL).append("Source ").append("\n");
        for (String source : sources) {
            sb.append(TABLE_CELL).append(source).append("\n");
        }
        sb.append(TABLE_BORDER).append('\n');
        return sb.toString();
    }
}
