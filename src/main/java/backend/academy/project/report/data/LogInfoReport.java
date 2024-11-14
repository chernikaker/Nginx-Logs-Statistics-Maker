package backend.academy.project.report.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Map;

@Getter
@Setter

public class LogInfoReport {
    private long logsCount;
    private Map<String, Long> resourceFrequency;
    private Map<Integer, Long> codeAnswerFrequency;
    private double avgAnswerSize;
    private double percentile95AnswerSize;
}
