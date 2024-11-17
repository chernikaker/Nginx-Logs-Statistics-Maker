package backend.academy.project.report.data;

import backend.academy.project.report.data.exception.CodesInfoNotFoundException;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * Класс-контейнер для информации о кодах возвращаемого HTTP ответа
 * и соответствующих им названиях<br><br>
 * При инициализации класса информация считывается из JSON
 */
public class AnswerCodeCollection {

    private static final Map<Integer, String> CODE_NAMES = new HashMap<>();
    private static final String JSON_CODE_FILE = "/http_status_codes.json";

    static {
        try (InputStreamReader reader = new InputStreamReader(
            Objects.requireNonNull(AnswerCodeCollection.class.getResourceAsStream(JSON_CODE_FILE)),
            StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<Integer, String>>() {}.getType();
            Map<Integer, String> tempMap = new Gson().fromJson(reader, type);
            CODE_NAMES.putAll(tempMap);
        } catch (Exception e) {
            throw new CodesInfoNotFoundException("Can't process codes file", e);
        }
    }

    private AnswerCodeCollection() {}

    public static String getAnswerInfoByCode(int code) {
        String answer = CODE_NAMES.get(code);
        if (answer == null) {
            throw new NoSuchElementException("Code is not found: " + code);
        }
        return answer;
    }
}
