package backend.academy.project.report.view;

import backend.academy.project.commandline.OuputFileType;

public class SimpleWriterFactory {

    public StatisticsWriter createWriter(OuputFileType fileType, String filename) {
        return switch (fileType) {
            case MARKDOWN -> new MarkdownStatisticsWriter(filename);
            case ADOC -> new AdocStatisticsWriter(filename);
        };
    }
}
