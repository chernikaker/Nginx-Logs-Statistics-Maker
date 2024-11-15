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

public class AnswerCodeCollection {

    private AnswerCodeCollection() {}

    private static final Map<Integer, String> CODE_NAMES = new HashMap<>();

    static {
        try (InputStreamReader reader = new InputStreamReader(
            Objects.requireNonNull(AnswerCodeCollection.class.getResourceAsStream("/http_status_codes.json")),
            StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<Integer, String>>() {}.getType();
            Map<Integer, String> tempMap = new Gson().fromJson(reader, type);
            CODE_NAMES.putAll(tempMap);
        } catch (Exception e) {
            throw new CodesInfoNotFoundException("Can't process codes file", e);
        }
    }

    public static String getAnswerInfoByCode(int code) {
        String answer = CODE_NAMES.get(code);
        if (answer == null) {
            throw new NoSuchElementException("Code is not found: " + code);
        }
        return answer;
    }
}
