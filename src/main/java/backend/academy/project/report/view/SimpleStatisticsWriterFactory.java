package backend.academy.project.report.view;

import backend.academy.project.commandline.OuputFileType;

public class SimpleStatisticsWriterFactory {

    public StatisticsFileWriter createStatisticsFileWriter(OuputFileType fileType) {
        return switch (fileType) {
            case MARKDOWN -> new MarkdownStatisticsFileWriter();
            case ADOC -> new AdocStatisticsFileWriter();
        };
    }
}
