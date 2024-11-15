package backend.academy.project;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.commandline.CommandLineArgsParser;
import backend.academy.project.logs.LogRecord;
import backend.academy.project.readers.LogsReader;
import backend.academy.project.report.data.LogInfoReport;
import backend.academy.project.report.data.StatisticsCollector;
import backend.academy.project.report.view.SimpleStatisticsWriterFactory;
import backend.academy.project.report.view.StatisticsFileWriter;
import backend.academy.project.commandline.CommandLineArgsValidator;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class LogStatisticsApp {

    private static final Path reportPath = Paths.get("src", "main", "resources").toAbsolutePath();

    public String report(String[] args) {
        try {
            CommandLineArgs arguments = CommandLineArgsParser.getArgs(args);
            CommandLineArgsValidator.validate(arguments);
            LogsReader reader = LogsReader.getReaderByPath(arguments.pathToLogs());
            Stream<LogRecord> lines = reader.readLogLines();
            List<String> processedResources = reader.getLogSourceNames();
            if (processedResources.isEmpty()) {
                return "No files found";
            }
            LogInfoReport report = StatisticsCollector.calculateLogStatistics(lines, arguments);
            StatisticsFileWriter viewer = new SimpleStatisticsWriterFactory().createStatisticsFileWriter(arguments.type(), arguments.filename());
            viewer.writeResultsToFile(reportPath, report, arguments, processedResources);
            return "Report is written successfully to directory "+ reportPath;
        } catch (Exception e) {
            return "Error occured: "+e.getMessage();
        }
    }

}
