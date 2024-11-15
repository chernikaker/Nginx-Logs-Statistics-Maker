package backend.academy.project.report.view;

import java.util.List;


public class AdocStatisticsFileWriter extends StatisticsFileWriter {

    private static final String TABLE_BORDER = "|====";
    private static final String TABLE_CELL = "|";
    private static final String HEADER_SYMBOL = "=";
    private static final String FILE_EXTENSION = ".adoc";

    protected AdocStatisticsFileWriter(String filename) {
        super(filename+FILE_EXTENSION);
    }

    @Override
    protected String buildCell(String data){
        return TABLE_CELL + data + '\n';
    }

    @Override
    protected String buildTableHeader(List<String> names) {
        StringBuilder sb = new StringBuilder();
        for (String name : names) {
            sb.append(TABLE_CELL).append(name).append(' ');
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
        return sb.toString();
    }

    @Override
    protected String buildTableStart(String name, int cols){
        StringBuilder sb = new StringBuilder();
        sb.append(HEADER_SYMBOL)
            .append(HEADER_SYMBOL)
            .append(" ")
            .append(name)
            .append('\n');
        sb.append("[cols=").append(cols).append("]").append('\n');
        sb.append(TABLE_BORDER).append('\n');
        return sb.toString();
    }

    @Override
    protected  String buildTableEnd() {
        return TABLE_BORDER+"\n";
    }

    @Override
    protected String buildHeader(String info){
        return HEADER_SYMBOL+" "+info+'\n';
    }
}
