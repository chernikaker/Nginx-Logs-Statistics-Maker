package backend.academy.project.report.view;

import java.util.List;

public class MarkdownStatisticsFileWriter extends StatisticsFileWriter {

    private static final String HORIZ_DELIM = ":---------:";
    private static final String VERT_DELIM = "|";
    private static final String HEADER_SYMBOL = "#";
    private static final String FILE_EXTENSION = ".md";

    protected MarkdownStatisticsFileWriter(String filename) {
        super(filename+FILE_EXTENSION);
    }

    @Override
    protected String buildCell(String data){
        return VERT_DELIM+data;
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
        sb.append(buildRowEnd());
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
        return sb.toString();
    }

    @Override
    protected  String buildTableEnd(){
        return "";
    }

    @Override
    protected String buildHeader(String info){
        return HEADER_SYMBOL+" "+info+'\n';
    }

    private String buildRowEnd(){
        return VERT_DELIM+'\n';
    }
}
