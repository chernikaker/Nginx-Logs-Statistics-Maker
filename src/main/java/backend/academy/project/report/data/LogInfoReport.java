package backend.academy.project.report.data;

import backend.academy.project.logs.RequestType;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogInfoReport {
    private long logsCount;
    private Map<String, Long> resourceFrequency;
    private Map<Integer, Long> codeAnswerFrequency;
    private double avgAnswerSize;
    private double percentile95AnswerSize;
    // дополнительная статистика - количество уникальных IP
    private long uniqueIPCount;
    // дополнительная статистика - частота типов запросов
    private Map<RequestType, Long> requestTypeFrequency;
}
