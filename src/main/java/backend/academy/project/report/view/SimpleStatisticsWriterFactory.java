package backend.academy.project.report.view;

import backend.academy.project.commandline.OuputFileType;

public class SimpleStatisticsWriterFactory {

    public StatisticsFileWriter createStatisticsFileWriter(OuputFileType fileType, String filename) {
        return switch (fileType) {
            case MARKDOWN -> new MarkdownStatisticsFileWriter(filename);
            case ADOC -> new AdocStatisticsFileWriter(filename);
        };
    }
}
