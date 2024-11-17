package backend.academy.project.report.view;

import backend.academy.project.commandline.OutputFileType;

public class SimpleWriterFactory {

    private SimpleWriterFactory() {}

    public static StatisticsWriter createWriter(OutputFileType fileType, String filename) {
        return switch (fileType) {
            case MARKDOWN -> new MarkdownStatisticsWriter(filename);
            case ADOC -> new AdocStatisticsWriter(filename);
        };
    }
}
