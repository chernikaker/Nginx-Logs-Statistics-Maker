package backend.academy.project.report.view;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.report.data.LogInfoReport;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class StatisticsFileWriter {

    protected static final int TOP_RESULTS = 5;
    protected final String filename;

    protected StatisticsFileWriter(String filename) {
        this.filename = filename;
    }

    protected abstract String makeReportText(LogInfoReport report, CommandLineArgs args,  List<String> resources);

    public void writeResultsToFile(Path directoryPath, LogInfoReport report, CommandLineArgs args, List<String> resources) {
        String reportText = makeReportText(report, args, resources);
        if (Files.isRegularFile(directoryPath)) {
            throw new RuntimeException("Path is a regular file, not directory: "+directoryPath);
        }
        Path filePath = directoryPath.resolve(filename);
        if (Files.exists(filePath)) {
            throw new RuntimeException("File already exists: " + filePath);
        }
        try {
            Files.writeString(filePath, reportText, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } catch (IOException e) {
            throw new RuntimeException("Can't write statistics to file " + filePath);
        }
    }

    protected <K> List<Map.Entry<K, Long>> getTopByFrequency(Map<K, Long> data) {
        return data.entrySet()
            .stream()
            .sorted(Map.Entry.<K, Long>comparingByValue().reversed())
            .limit(TOP_RESULTS)
            .collect(Collectors.toList());
    }
}
