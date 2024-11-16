package backend.academy.project.report.data;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.commandline.FilterFieldType;
import backend.academy.project.logs.LogRecord;
import backend.academy.project.logs.RequestType;
import backend.academy.project.report.data.exception.LogsNotFoundException;
import com.datadoghq.sketch.ddsketch.DDSketch;
import com.datadoghq.sketch.ddsketch.DDSketches;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.agkn.hll.HLL;

public class StatisticsCollector {

    private final static double RELATIVE_ACCURACY = 0.01;
    private final static double QUANTILE = 0.95;
    private final static int LOG_2_M = 14;
    private final static int REGISTER_WIDTH = 5;

    private StatisticsCollector() {}

    public static LogInfoReport calculateLogStatistics(Stream<LogRecord> logRecords, CommandLineArgs args) {

        Predicate<LogRecord> checkDateRange = (logRecord -> {
            if (args.from().isPresent() && logRecord.timeLocal().isBefore(args.from().orElseThrow())) {
                return false;
            }
            return args.to().isEmpty() || !logRecord.timeLocal().isAfter(args.to().orElseThrow());
        });

        AtomicLong logsCount = new AtomicLong();
        Map<String, Long> resourceFrequency = new HashMap<>();
        Map<Integer, Long> codeAnswerFrequency = new HashMap<>();
        Map<RequestType, Long> requestTypeFrequency = new HashMap<>();
        AtomicLong totalBytesSent = new AtomicLong();
        HLL hll = new HLL(LOG_2_M, REGISTER_WIDTH);


        DDSketch sketch = DDSketches.unboundedDense(RELATIVE_ACCURACY);
        Stream<LogRecord> filteredRecords = logRecords.filter(checkDateRange);
        if (args.filterField() != FilterFieldType.NONE) {
            filteredRecords = filteredRecords.filter(
                log -> Objects.requireNonNull(log.getValueByFieldName(args.filterField())).matches(args.filterValue()));
        }
        filteredRecords.forEach(log -> {
            logsCount.getAndIncrement();
            resourceFrequency.merge(log.requestResource(), 1L, Long::sum);
            codeAnswerFrequency.merge(log.status(), 1L, Long::sum);
            requestTypeFrequency.merge(log.requestType(), 1L, Long::sum);
            totalBytesSent.addAndGet(log.bytesSent());
            sketch.accept(log.bytesSent());
            hll.addRaw(log.remoteAddress().hashCode());
        });

        if (logsCount.get() == 0) {
            throw new LogsNotFoundException("No logs found after filtering");
        }
        LogInfoReport report = new LogInfoReport();
        report.logsCount(logsCount.get());
        report.resourceFrequency(resourceFrequency);
        report.codeAnswerFrequency(codeAnswerFrequency);
        report.requestTypeFrequency(requestTypeFrequency);
        report.avgAnswerSize(logsCount.get() > 0 ?  (double) totalBytesSent.get() / logsCount.get() : 0.0);
        report.percentile95AnswerSize(sketch.getValueAtQuantile(QUANTILE));
        report.uniqueIPCount(hll.cardinality());
        return report;
    }
}
