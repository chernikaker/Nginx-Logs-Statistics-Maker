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

/**
 * Утилитарный класс, рассчитывающий статистику для потока логов.<br><br>
 * Для рассчета 95 перцентиля размера ответа изпользуется <b>DDSketch.</b><br>
 * DDSketch — это алгоритм для агрегации и хранения статистики распределения значений в потоке данных.
 * Он обеспечивает высокую точность при оценке квантилей и других статистических показателей.<br><br>
 *
 * Для расчета количества уникальных IP (предположительно на большом количестве данных)
 * используется <b>HyperLogLog.</b><br>
 * HyperLogLog (HLL) — это вероятностный алгоритм для оценки количества уникальных элементов в больших наборах данных.
 * Он эффективно использует память и вычислительные ресурсы.
 */
public class StatisticsCollector {

    // Параметры DDSketch (точность и перцентиль)
    private final static double RELATIVE_ACCURACY = 0.001;
    private final static double QUANTILE = 0.95;
    // Параметры HyperLogLog (количестов регистров, ширина регистров)
    private final static int LOG_2_M = 14;
    private final static int REGISTER_WIDTH = 5;

    private StatisticsCollector() {}

    public static LogInfoReport calculateLogStatistics(Stream<LogRecord> logRecords, CommandLineArgs args) {
        // предикат фильтрации по датам
        Predicate<LogRecord> checkDateRange = (logRecord -> {
            if (args.from().isPresent() && logRecord.timeLocal().isBefore(args.from().orElseThrow())) {
                return false;
            }
            return args.to().isEmpty() || !logRecord.timeLocal().isAfter(args.to().orElseThrow());
        });
        // аккумулятор количества логов
        AtomicLong logsCount = new AtomicLong();
        // аккумулятор используемых ресурсов
        Map<String, Long> resourceFrequency = new HashMap<>();
        // аккумулятор кодов ответа
        Map<Integer, Long> codeAnswerFrequency = new HashMap<>();
        // аккумулятор типов запроса
        Map<RequestType, Long> requestTypeFrequency = new HashMap<>();
        // аккумулятор количества отправленных байт
        AtomicLong totalBytesSent = new AtomicLong();
        // аккумулятор количества уникальных IP
        HLL ipHll = new HLL(LOG_2_M, REGISTER_WIDTH);
        // аккумулятор 95 перцентиля
        DDSketch percentileSketch = DDSketches.unboundedDense(RELATIVE_ACCURACY);
        // фильтрация по дате и полю
        Stream<LogRecord> filteredRecords = logRecords.filter(checkDateRange);
        if (args.filterField() != FilterFieldType.NONE) {
            filteredRecords = filteredRecords.filter(
                log -> Objects.requireNonNull(log.getValueByFieldName(args.filterField())).matches(args.filterValue()));
        }
        // подсчет статистики
        filteredRecords.forEach(log -> {
            logsCount.getAndIncrement();
            resourceFrequency.merge(log.requestResource(), 1L, Long::sum);
            codeAnswerFrequency.merge(log.status(), 1L, Long::sum);
            requestTypeFrequency.merge(log.requestType(), 1L, Long::sum);
            totalBytesSent.addAndGet(log.bytesSent());
            percentileSketch.accept(log.bytesSent());
            ipHll.addRaw(log.remoteAddress().hashCode());
        });
        if (logsCount.get() == 0) {
            throw new LogsNotFoundException("No logs found matching user parameters");
        }
        // заполнение полей отчета
        LogInfoReport report = new LogInfoReport();
        report.logsCount(logsCount.get());
        report.resourceFrequency(resourceFrequency);
        report.codeAnswerFrequency(codeAnswerFrequency);
        report.requestTypeFrequency(requestTypeFrequency);
        report.avgAnswerSize(logsCount.get() > 0 ?  (double) totalBytesSent.get() / logsCount.get() : 0.0);
        report.percentile95AnswerSize(percentileSketch.getValueAtQuantile(QUANTILE));
        report.uniqueIPCount(ipHll.cardinality());
        return report;
    }
}
