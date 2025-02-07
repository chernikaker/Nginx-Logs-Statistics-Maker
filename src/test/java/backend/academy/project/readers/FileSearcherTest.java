package backend.academy.project.readers;

import backend.academy.project.readers.exception.FindingFilesException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileSearcherTest {

    private final Path rootDir = FileSystems.getDefault().getPath("src","test", "resources", "logsFileTestDirectory");

    @Test
    public void oneLogFileInDirectoryTest() {
        String glob = "log*";
        Path newRoot = rootDir.resolve("oneLogOneDirectory");
        List<Path> logFiles = assertDoesNotThrow(() -> FileSearcher.getLogFiles(glob, newRoot));
        assertEquals(1, logFiles.size());
        assertEquals(newRoot.resolve("logFile1.txt"), logFiles.getFirst());
    }

    @Test
    public void oneLogFileInMultipleDirsTest() {
        String glob = "**/log*";
        Path newRoot = rootDir.resolve("oneLogMultipleDirectories");
        List<Path> logFiles = assertDoesNotThrow(() -> FileSearcher.getLogFiles(glob, newRoot));
        assertEquals(1, logFiles.size());
        assertEquals(newRoot.resolve("dir1","dir2","logFile2.txt"), logFiles.getFirst());
    }

    @Test
    public void manyLogsInOneDirTest() {
        String glob = "log*";
        Path newRoot = rootDir.resolve("manyLogsOneDirectory");
        List<Path> logFiles = assertDoesNotThrow(() -> FileSearcher.getLogFiles(glob, newRoot));
        assertEquals(3, logFiles.size());
        assertTrue(logFiles.contains(newRoot.resolve("logFile3.txt")));
        assertTrue(logFiles.contains(newRoot.resolve("logFile4.txt")));
        assertTrue(logFiles.contains(newRoot.resolve("logFile2024-09-21.txt")));
    }

    @Test
    public void globWithDirectoryTest() {
        String glob = "oneLogMultipleDirectories/dir1/dir2/log*";
        List<Path> logFiles = assertDoesNotThrow(() -> FileSearcher.getLogFiles(glob, rootDir));
        assertEquals(1, logFiles.size());
        assertEquals(rootDir.resolve("oneLogMultipleDirectories/dir1/dir2/logFile2.txt"), logFiles.getFirst());
    }

    @Test
    public void globWithDirectorySkipTest() {
        String glob = "oneLogMultipleDirectories/**/log*";
        List<Path> logFiles = assertDoesNotThrow(() -> FileSearcher.getLogFiles(glob, rootDir));
        assertEquals(1, logFiles.size());
        assertEquals(rootDir.resolve("oneLogMultipleDirectories/dir1/dir2/logFile2.txt"), logFiles.getFirst());
    }

    @Test
    public void allLogsInFoldersTest() {
        String glob = "**/log*";
        List<Path> logFiles = assertDoesNotThrow(() -> FileSearcher.getLogFiles(glob, rootDir));
        assertEquals(5, logFiles.size());
        assertTrue(logFiles.contains(rootDir.resolve("manyLogsOneDirectory", "logFile3.txt")));
        assertTrue(logFiles.contains(rootDir.resolve("manyLogsOneDirectory", "logFile4.txt")));
        assertTrue(logFiles.contains(rootDir.resolve("manyLogsOneDirectory", "logFile2024-09-21.txt")));
        assertTrue(logFiles.contains(rootDir.resolve("oneLogMultipleDirectories", "dir1", "dir2", "logFile2.txt")));
        assertTrue(logFiles.contains(rootDir.resolve("oneLogOneDirectory", "logFile1.txt")));
    }

    @Test
    public void allLogsTest() {
        String glob = "log*";
        List<Path> logFiles = assertDoesNotThrow(() -> FileSearcher.getLogFiles(glob, rootDir));
        assertEquals(6, logFiles.size());
        assertTrue(logFiles.contains(rootDir.resolve("manyLogsOneDirectory", "logFile3.txt")));
        assertTrue(logFiles.contains(rootDir.resolve("manyLogsOneDirectory", "logFile4.txt")));
        assertTrue(logFiles.contains(rootDir.resolve("manyLogsOneDirectory", "logFile2024-09-21.txt")));
        assertTrue(logFiles.contains(rootDir.resolve("oneLogMultipleDirectories", "dir1", "dir2", "logFile2.txt")));
        assertTrue(logFiles.contains(rootDir.resolve("oneLogOneDirectory", "logFile1.txt")));
        assertTrue(logFiles.contains(rootDir.resolve("logFile5.txt")));
    }

    @Test
    public void invalidRootPath() {
        Path invalidRoot = rootDir.resolve("invalidRoot");
        String glob = "log*";
        assertThatThrownBy(() -> FileSearcher.getLogFiles(glob, invalidRoot))
            .isInstanceOf(FindingFilesException.class)
            .hasMessage("Error finding logs from root "+invalidRoot+" with glob " + glob);
    }

    @Test
    public void FilesNotFoundByGlobTest() {
        String glob = "NotFoundLog*";
        List<Path> logFiles = assertDoesNotThrow(() -> FileSearcher.getLogFiles(glob, rootDir));
        assertEquals(0, logFiles.size());
    }
}
