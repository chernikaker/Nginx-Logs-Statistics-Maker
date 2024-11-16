package backend.academy.project.commandline;

import backend.academy.project.commandline.exception.DateValidationException;
import backend.academy.project.commandline.exception.EmptyPathValidationException;
import backend.academy.project.commandline.exception.FileNamingException;
import backend.academy.project.commandline.exception.FilterValidationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class CommandLineArgsValidatorTest {

    private final CommandLineArgs jArgs = Mockito.mock(CommandLineArgs.class);

    @Test
    public void DefaultFieldsTest() {
        makeDefaultArgsMock(jArgs);
        assertDoesNotThrow(() -> CommandLineArgsValidator.validate(jArgs));
    }

    @Test
    public void OneDatePresentedTest() {
        makeOneDateArgsMock(jArgs, true);
        assertDoesNotThrow(() -> CommandLineArgsValidator.validate(jArgs));
        makeOneDateArgsMock(jArgs, false);
        assertDoesNotThrow(() -> CommandLineArgsValidator.validate(jArgs));
    }

    @Test
    public void correctPresentedDatesTest() {
        makeCorrectDatesArgsMock(jArgs);
        assertDoesNotThrow(() -> CommandLineArgsValidator.validate(jArgs));
    }

    @Test
    public void correctPresentedFilenameTest() {
        makePathMock(jArgs, "report");
        assertDoesNotThrow(() -> CommandLineArgsValidator.validate(jArgs));
    }

    @Test
    public void correctPresentedFilterTest() {
        makeCorrectFilterMock(jArgs);
        assertDoesNotThrow(() -> CommandLineArgsValidator.validate(jArgs));
    }

    @Test
    public void dateFromIsBeforeToTest() {
        makeInvalidDatesArgsMock(jArgs);
        assertThatThrownBy(() -> CommandLineArgsValidator.validate(jArgs))
            .isInstanceOf(DateValidationException.class)
            .hasMessage("Date from " + jArgs.from().orElseThrow() + " is after date to " + jArgs.to().orElseThrow(

            ));
    }

    @Test
    public void emptyFileNameTest() {
        makePathMock(jArgs,"   ");
        assertThatThrownBy(() -> CommandLineArgsValidator.validate(jArgs))
            .isInstanceOf(FileNamingException.class)
            .hasMessageContaining("file name is invalid");
    }

    @Test
    public void invalidFileNameTest() {
        makePathMock(jArgs,"report:");
        assertThatThrownBy(() -> CommandLineArgsValidator.validate(jArgs))
            .isInstanceOf(FileNamingException.class)
            .hasMessageContaining("file name is invalid");
    }

    @Test
    public void invalidFilterFieldWithoutValueTest() {
        makeFilterWithoutValueMock(jArgs);
        assertThatThrownBy(() -> CommandLineArgsValidator.validate(jArgs))
            .isInstanceOf(FilterValidationException.class)
            .hasMessageContaining("Argument [--filter-field] is used without [--filter-value]");
    }

    @Test
    public void invalidFilterValueWithoutFieldTest() {
        makeFilterValueWithoutFieldMock(jArgs);
        assertThatThrownBy(() -> CommandLineArgsValidator.validate(jArgs))
            .isInstanceOf(FilterValidationException.class)
            .hasMessageContaining("Argument [--filter-value] is used without [--filter-field]");
    }

    @Test
    public void invalidEmptyPathTest() {
        makeEmptyPathMock(jArgs);
        assertThatThrownBy(() -> CommandLineArgsValidator.validate(jArgs))
            .isInstanceOf(EmptyPathValidationException.class)
            .hasMessage("Path is empty");
    }


    private static void makeDefaultArgsMock(CommandLineArgs jArgs){
        Mockito.when(jArgs.pathToLogs()).thenReturn("logs*");
        Mockito.when(jArgs.from()).thenReturn(Optional.empty());
        Mockito.when(jArgs.to()).thenReturn(Optional.empty());
        Mockito.when(jArgs.filterField()).thenReturn(FilterFieldType.NONE);
        Mockito.when(jArgs.filterValue()).thenReturn("");
        Mockito.when(jArgs.filename()).thenReturn("statistics_report");
    }

    private static void makeCorrectDatesArgsMock(CommandLineArgs jArgs){
        makeDefaultArgsMock(jArgs);
        Mockito.when(jArgs.from()).thenReturn(Optional.of(LocalDateTime.of(2005, 8, 9, 0, 0, 0)));
        Mockito.when(jArgs.to()).thenReturn(Optional.of(LocalDateTime.of(2005, 8, 10, 0, 0, 0)));
    }

    private static void makeCorrectFilterMock(CommandLineArgs jArgs){
        makeDefaultArgsMock(jArgs);
        Mockito.when(jArgs.filterField()).thenReturn(FilterFieldType.STATUS);
        Mockito.when(jArgs.filterValue()).thenReturn("GET");
    }

    private static void makeOneDateArgsMock(CommandLineArgs jArgs, boolean from){
        makeDefaultArgsMock(jArgs);
        if (from) {
            Mockito.when(jArgs.from()).thenReturn(Optional.of(LocalDateTime.of(2005, 8, 9, 0, 0, 0)));
        } else {
            Mockito.when(jArgs.to()).thenReturn(Optional.of(LocalDateTime.of(2005, 8, 10, 0, 0, 0)));
        }
    }

    private static void makeInvalidDatesArgsMock(CommandLineArgs jArgs){
        makeDefaultArgsMock(jArgs);
        Mockito.when(jArgs.from()).thenReturn(Optional.of(LocalDateTime.of(2005, 8, 9, 0, 0, 0)));
        Mockito.when(jArgs.to()).thenReturn(Optional.of(LocalDateTime.of(2005, 8, 8, 0, 0, 0)));
    }

    private static void makePathMock(CommandLineArgs jArgs, String invalidName) {
        makeDefaultArgsMock(jArgs);
        Mockito.when(jArgs.filename()).thenReturn(invalidName);
    }

    private static void makeFilterWithoutValueMock(CommandLineArgs jArgs) {
        makeDefaultArgsMock(jArgs);
        Mockito.when(jArgs.filterField()).thenReturn(FilterFieldType.STATUS);
    }

    private static void makeFilterValueWithoutFieldMock(CommandLineArgs jArgs) {
        makeDefaultArgsMock(jArgs);
        Mockito.when(jArgs.filterValue()).thenReturn("GET");
    }

    private static void makeEmptyPathMock(CommandLineArgs jArgs) {
        makeDefaultArgsMock(jArgs);
        Mockito.when(jArgs.pathToLogs()).thenReturn("  ");
    }
}
