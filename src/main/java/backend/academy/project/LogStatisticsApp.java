package backend.academy.project;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.logs.LogRecord;
import backend.academy.project.readers.LocalFileLogsReader;
import backend.academy.project.readers.LogsReader;
import backend.academy.project.readers.UrlLogsReader;
import backend.academy.project.report.data.LogInfoReport;
import backend.academy.project.report.data.StatisticsCollector;
import backend.academy.project.report.view.MarkdownStatisticsFileWriter;
import backend.academy.project.report.view.SimpleStatisticsWriterFactory;
import backend.academy.project.report.view.StatisticsFileWriter;
import com.beust.jcommander.JCommander;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class LogStatisticsApp {

    private static final Path reportPath = Paths.get("src", "main", "resources").toAbsolutePath();

    public String report(String[] args) {

        try {
            CommandLineArgs arguments = getArgs(args);
            LogsReader reader = getReader(arguments.pathToLogs());
            Stream<LogRecord> lines = reader.readLogLines();
            List<String> processedResources = reader.getLogFileNames();
            if (processedResources.isEmpty()) {
                return "No files found";
            }
            StatisticsCollector statistics = new StatisticsCollector();
            LogInfoReport report = statistics.calculateLogStatistics(lines, arguments);
            StatisticsFileWriter viewer = new SimpleStatisticsWriterFactory().createStatisticsFileWriter(arguments.type());
            viewer.writeResultsToFile(reportPath, report, arguments, processedResources);
            return "Report is written successfully to file "+ reportPath;
        } catch (Exception e) {
            return "Error occured "+e.getMessage();
        }
    }

    private static CommandLineArgs getArgs(String[] args){
        CommandLineArgs jArgs = new CommandLineArgs();
        JCommander helloCmd = JCommander.newBuilder()
            .addObject(jArgs)
            .build();
        helloCmd.parse(args);
        return jArgs;
    }

    //TODO: rewrite
    private static LogsReader getReader(String path) {
        try {
            new URL(path);
            return new UrlLogsReader(path);
        } catch (MalformedURLException e) {
            return new LocalFileLogsReader(path);
        }
    }
}
