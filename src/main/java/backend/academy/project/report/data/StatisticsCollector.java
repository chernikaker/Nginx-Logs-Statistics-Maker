package backend.academy.project.report.data;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.logs.LogRecord;
import com.datadoghq.sketch.ddsketch.DDSketch;
import com.datadoghq.sketch.ddsketch.DDSketches;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StatisticsCollector {


    public LogInfoReport calculateLogStatistics(Stream<LogRecord> logRecords, CommandLineArgs args) {

        Predicate<LogRecord> checkDateRange = (logRecord -> {
            if(args.from().isPresent() && logRecord.timeLocal().isBefore(args.from().get())) {
                return false;
            }
            return args.to().isEmpty() || !logRecord.timeLocal().isAfter(args.from().get());
        });

        AtomicLong logsCount = new AtomicLong();
        Map<String, Long> resourceFrequency = new HashMap<>();
        Map<Integer, Long> codeAnswerFrequency = new HashMap<>();
        AtomicLong totalBytesSent = new AtomicLong();


        double relativeAccuracy = 0.01;
        double quantile = 0.95;
        DDSketch sketch = DDSketches.unboundedDense(relativeAccuracy);

        logRecords.filter(checkDateRange).forEach(log -> {
            logsCount.getAndIncrement();
            resourceFrequency.merge(log.requestedResource(), 1L, Long::sum);
            codeAnswerFrequency.merge(log.status(), 1L, Long::sum);
            totalBytesSent.addAndGet(log.bytesSent());
             sketch.accept(log.bytesSent());
        });


        LogInfoReport report = new LogInfoReport();
        report.logsCount(logsCount.get());
        report.resourceFrequency(resourceFrequency);
        report.codeAnswerFrequency(codeAnswerFrequency);
        report.avgAnswerSize(logsCount.get() > 0 ?  (double) totalBytesSent.get() / logsCount.get() : 0.0);
        report.percentile95AnswerSize(sketch.getValueAtQuantile(quantile));

        return report;
    }
}
