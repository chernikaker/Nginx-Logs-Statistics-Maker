package backend.academy;

import backend.academy.project.LogStatisticsApp;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Main {
    public static void main(String[] args) {

        LogStatisticsApp app = new LogStatisticsApp(System.out);
        app.run(args);
    }
}
