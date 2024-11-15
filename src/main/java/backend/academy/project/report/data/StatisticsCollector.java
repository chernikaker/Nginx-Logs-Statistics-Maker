package backend.academy.project.report.data;

import backend.academy.project.commandline.CommandLineArgs;
import backend.academy.project.commandline.FilterFieldType;
import backend.academy.project.logs.LogRecord;
import backend.academy.project.logs.RequestType;
import backend.academy.project.report.data.exception.LogsNotFoundException;
import com.datadoghq.sketch.ddsketch.DDSketch;
import com.datadoghq.sketch.ddsketch.DDSketches;
import com.google.common.hash.Hasher;
import net.agkn.hll.HLL;
import org.apache.commons.codec.digest.MurmurHash3;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class StatisticsCollector {

    public static LogInfoReport calculateLogStatistics(Stream<LogRecord> logRecords, CommandLineArgs args) {


        Predicate<LogRecord> checkDateRange = (logRecord -> {
            if (args.from().isPresent() && logRecord.timeLocal().isBefore(args.from().get())) {
                return false;
            }
            return args.to().isEmpty() || !logRecord.timeLocal().isAfter(args.from().get());
        });

        AtomicLong logsCount = new AtomicLong();
        Map<String, Long> resourceFrequency = new HashMap<>();
        Map<Integer, Long> codeAnswerFrequency = new HashMap<>();
        Map<RequestType, Long> requestTypeFrequency = new HashMap<>();
        AtomicLong totalBytesSent = new AtomicLong();
        HLL hll = new HLL(14, 5);

        double relativeAccuracy = 0.01;
        double quantile = 0.95;
        DDSketch sketch = DDSketches.unboundedDense(relativeAccuracy);
        logRecords = logRecords.filter(checkDateRange);
        if(args.filterField()!= FilterFieldType.NONE) {
            logRecords = logRecords.filter(
                log -> Objects.requireNonNull(log.getValueByFieldName(args.filterField())).matches(args.filterValue()));
        }
        logRecords.forEach(log -> {
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
        report.percentile95AnswerSize(sketch.getValueAtQuantile(quantile));
        report.uniqueIPCount(hll.cardinality());
        return report;
    }
}
