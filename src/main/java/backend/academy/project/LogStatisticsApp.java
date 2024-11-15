package backend.academy.project;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.commandline.CommandLineArgsParser;
import backend.academy.project.commandline.CommandLineArgsValidator;
import backend.academy.project.logs.LogRecord;
import backend.academy.project.readers.LogsReader;
import backend.academy.project.readers.LogsReaderCreator;
import backend.academy.project.report.data.LogInfoReport;
import backend.academy.project.report.data.StatisticsCollector;
import backend.academy.project.report.view.SimpleWriterFactory;
import backend.academy.project.report.view.StatisticsWriter;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class LogStatisticsApp {

    private static final Path REPORT_PATH = Paths.get("").toAbsolutePath();
    private final PrintStream out;

    public LogStatisticsApp(PrintStream out) {
        this.out = out;
    }

    public void run(String[] args) {
        out.println(makeStatistics(args));
    }

    private String makeStatistics(String[] args) {
        try {
            CommandLineArgs arguments = CommandLineArgsParser.getArgs(args);
            CommandLineArgsValidator.validate(arguments);
            LogsReader reader = LogsReaderCreator.getReaderByPath(arguments.pathToLogs());
            Stream<LogRecord> lines = reader.readLogLines();
            List<String> processedResources = reader.getLogSourceNames();
            if (processedResources.isEmpty()) {
                return "No files found";
            }
            LogInfoReport report = StatisticsCollector.calculateLogStatistics(lines, arguments);
            SimpleWriterFactory writerFactory = new SimpleWriterFactory();
            StatisticsWriter viewer = writerFactory.createWriter(arguments.type(), arguments.filename());
            viewer.writeResultsToFile(REPORT_PATH, report, arguments, processedResources);
            return "Report is written successfully to directory " + REPORT_PATH;
        } catch (Exception e) {
            return "Error occured: " + e.getMessage();
        }
    }

}
