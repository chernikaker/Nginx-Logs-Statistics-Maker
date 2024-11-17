package backend.academy.project.commandline;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandLineArgsTest {

    @Test
    public void allArgsPresentedAndCorrectTest() {
        String[] args = new String[] {
            "--path", "logs/2024*",
            "--from", "2024-01-01",
            "--to", "2024-02-01",
            "--format", "adoc",
            "--filename", "report",
            "--filter-field", "status",
            "--filter-value", "GET"
        };
        CommandLineArgs jArgs = new CommandLineArgs();
        JCommander helloCmd = JCommander.newBuilder()
            .addObject(jArgs)
            .build();
        helloCmd.parse(args);
        assertEquals("logs/2024*", jArgs.pathToLogs());
        assertTrue(jArgs.from().isPresent());
        assertTrue(jArgs.to().isPresent());
        assertEquals(LocalDateTime.of(2024,1,1,0,0,0), jArgs.from().orElseThrow());
        assertEquals(LocalDateTime.of(2024,2,1,0,0,0), jArgs.to().orElseThrow());
        assertEquals(OutputFileType.ADOC, jArgs.type());
        assertEquals("report", jArgs.filename());
        assertEquals(FilterFieldType.STATUS, jArgs.filterField());
        assertEquals("GET", jArgs.filterValue());
    }

    @Test
    public void allArgsCorrectNoFormatTest() {
        String[] args = new String[] {
            "--path", "logs/2024*",
            "--from", "2024-01-01",
            "--to", "2024-02-01",
            "--filename", "report",
            "--filter-field", "status",
            "--filter-value", "GET"
        };
        CommandLineArgs jArgs = CommandLineArgsParser.getArgs(args);
        assertEquals("logs/2024*", jArgs.pathToLogs());
        assertTrue(jArgs.from().isPresent());
        assertTrue(jArgs.to().isPresent());
        assertEquals(LocalDateTime.of(2024,1,1,0,0,0), jArgs.from().orElseThrow());
        assertEquals(LocalDateTime.of(2024,2,1,0,0,0), jArgs.to().orElseThrow());
        assertEquals(OutputFileType.MARKDOWN, jArgs.type());
        assertEquals("report", jArgs.filename());
        assertEquals(FilterFieldType.STATUS, jArgs.filterField());
        assertEquals("GET", jArgs.filterValue());
    }

    @Test
    public void allArgsCorrectNoFromDateTest() {
        String[] args = new String[] {
            "--path", "logs/2024*",
            "--to", "2024-02-01",
            "--format", "adoc",
            "--filename", "report",
            "--filter-field", "status",
            "--filter-value", "GET"
        };
        CommandLineArgs jArgs = CommandLineArgsParser.getArgs(args);
        assertEquals("logs/2024*", jArgs.pathToLogs());
        assertFalse(jArgs.from().isPresent());
        assertTrue(jArgs.to().isPresent());
        assertEquals(LocalDateTime.of(2024,2,1,0,0,0), jArgs.to().orElseThrow());
        assertEquals(OutputFileType.ADOC, jArgs.type());
        assertEquals("report", jArgs.filename());
        assertEquals(FilterFieldType.STATUS, jArgs.filterField());
        assertEquals("GET", jArgs.filterValue());
    }

    @Test
    public void allArgsCorrectNoFilenameTest() {
        String[] args = new String[] {
            "--path", "logs/2024*",
            "--from", "2024-01-01",
            "--format", "adoc",
            "--filter-field", "status",
            "--filter-value", "GET"
        };
        CommandLineArgs jArgs = CommandLineArgsParser.getArgs(args);
        assertEquals("logs/2024*", jArgs.pathToLogs());
        assertFalse(jArgs.to().isPresent());
        assertTrue(jArgs.from().isPresent());
        assertEquals(LocalDateTime.of(2024,1,1,0,0,0), jArgs.from().orElseThrow());
        assertEquals(OutputFileType.ADOC, jArgs.type());
        assertEquals(OutputFileType.ADOC, jArgs.type());
        assertEquals("statistics_report", jArgs.filename());
        assertEquals(FilterFieldType.STATUS, jArgs.filterField());
        assertEquals("GET", jArgs.filterValue());
    }

    @Test
    public void allArgsCorrectNoToDateTest() {
        String[] args = new String[] {
            "--path", "logs/2024*",
            "--from", "2024-01-01",
            "--format", "adoc",
            "--filename", "report",
            "--filter-field", "status",
            "--filter-value", "GET"
        };
        CommandLineArgs jArgs = CommandLineArgsParser.getArgs(args);
        assertEquals("logs/2024*", jArgs.pathToLogs());
        assertFalse(jArgs.to().isPresent());
        assertTrue(jArgs.from().isPresent());
        assertEquals(LocalDateTime.of(2024,1,1,0,0,0), jArgs.from().orElseThrow());
        assertEquals(OutputFileType.ADOC, jArgs.type());
        assertEquals(OutputFileType.ADOC, jArgs.type());
        assertEquals(FilterFieldType.STATUS, jArgs.filterField());
        assertEquals("GET", jArgs.filterValue());
    }

    @Test
    public void allArgsCorrectNoFilterTest() {
        String[] args = new String[] {
            "--path", "logs/2024*",
            "--from", "2024-01-01",
            "--format", "adoc",
            "--filename", "report"
        };
        CommandLineArgs jArgs = CommandLineArgsParser.getArgs(args);
        assertEquals("logs/2024*", jArgs.pathToLogs());
        assertFalse(jArgs.to().isPresent());
        assertTrue(jArgs.from().isPresent());
        assertEquals(LocalDateTime.of(2024,1,1,0,0,0), jArgs.from().orElseThrow());
        assertEquals(OutputFileType.ADOC, jArgs.type());
        assertEquals(OutputFileType.ADOC, jArgs.type());
        assertEquals("report", jArgs.filename());
        assertEquals(FilterFieldType.NONE, jArgs.filterField());
        assertEquals("", jArgs.filterValue());
    }

    @Test
    public void NoRequiredPathTest() {
        String[] args = new String[] {};
        CommandLineArgs jArgs = new CommandLineArgs();
        assertThatThrownBy(() -> CommandLineArgsParser.getArgs(args))
            .isInstanceOf(ParameterException.class)
            .hasMessageContaining("--path");
    }

    @Test
    public void IncorrectFormatTest() {
        String[] args = new String[] {
            "--path", "logs/2024*",
            "--format", "txt"
        };
        assertThatThrownBy(() -> CommandLineArgsParser.getArgs(args))
            .isInstanceOf(ParameterException.class)
            .hasMessageContaining("--format");
    }

    @Test
    public void IncorrectDateFromTest() {
        String[] args = new String[] {
            "--path", "logs/2024*",
            "--from", "invalid_date"
        };
        assertThatThrownBy(() -> CommandLineArgsParser.getArgs(args))
            .isInstanceOf(ParameterException.class)
            .hasMessage("Invalid ISO8601 local date: invalid_date");
    }

    @Test
    public void IncorrectDateToTest() {
        String[] args = new String[] {
            "--path", "logs/2024*",
            "--to", "invalid_date"
        };

        assertThatThrownBy(() -> CommandLineArgsParser.getArgs(args))
            .isInstanceOf(ParameterException.class)
            .hasMessage("Invalid ISO8601 local date: invalid_date");
    }

    @Test
    public void IncorrectFilterFieldTest() {
        String[] args = new String[] {
            "--path", "logs/2024*",
            "--filter-field", "error",
            "--filter-value", "GET"
        };
        assertThatThrownBy(() -> CommandLineArgsParser.getArgs(args))
            .isInstanceOf(ParameterException.class)
            .hasMessageContaining("--filter-field");
    }

    @Test
    public void PathFlagWithoutValueError() {
        String[] args = new String[] {"--path"};
        assertThatThrownBy(() -> CommandLineArgsParser.getArgs(args))
            .isInstanceOf(ParameterException.class)
            .hasMessageContaining("--path");
    }
}
