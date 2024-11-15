package backend.academy.project.report.view;

import java.util.List;

public class MarkdownStatisticsWriter extends StatisticsWriter {

    private static final String HORIZ_DELIM = ":---------:";
    private static final char VERT_DELIM = '|';
    private static final char HEADER_SYMBOL = '#';
    private static final String FILE_EXTENSION = ".md";

    protected MarkdownStatisticsWriter(String filename) {
        super(filename + FILE_EXTENSION);
    }

    @Override
    protected String buildCell(String data) {
        return VERT_DELIM + data;
    }

    @Override
    protected String buildTableHeader(List<String> names) {
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

    @Override
    protected  String buildRow(List<String> data) {
        StringBuilder sb = new StringBuilder();
        for (String item : data) {
            sb.append(buildCell(item));
        }
        sb.append(VERT_DELIM + '\n');
        return sb.toString();
    }

    @Override
    protected String buildTableStart(String name, int cols) {
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER_SYMBOL)
            .append(HEADER_SYMBOL)
            .append(' ')
            .append(name)
            .append('\n');
        return sb.toString();
    }

    @Override
    protected  String buildTableEnd() {
        return "";
    }

    @Override
    protected String buildHeader(String info) {
        return HEADER_SYMBOL + ' ' + info + '\n';
    }

}
