package backend.academy.project.report.data;

import backend.academy.project.report.data.exception.CodesInfoNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.NoSuchElementException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AnswerCodeCollectionTest {

    @ParameterizedTest
    @ValueSource(ints = {200, 404, 500})
    public void getInfoAboutPresentedCodeTest(int code) {
        String info = assertDoesNotThrow(() -> AnswerCodeCollection.getAnswerInfoByCode(code));
        assertFalse(info.isBlank());
    }

    @Test
    public void getInfoAboutInvalidCodeTest() {
        assertThatThrownBy(() -> AnswerCodeCollection.getAnswerInfoByCode(999))
            .isInstanceOf(NoSuchElementException.class)
            .hasMessage("Code is not found: 999");
    }
}
