package backend.academy.project.readers;

import backend.academy.project.readers.exception.FindingFilesException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилитарный класс, осуществляющий чтение из локальных файлов по заданному glob шаблону.<br><br>
 * Glob-шаблоны — это простой способ сопоставления имен файлов с помощью подстановочных символов.
 * Они используются для выбора групп файлов по определенным критериям в различных операционных системах и инструментах.<br><br>
 * Основные подстановочные символы: <br>
 * <code>*</code>: Соответствует любому количеству символов.<br>
 * <code>?</code>: Соответствует ровно одному символу.<br>
 * <code>[ ]</code>: Соответствует любому одному символу из указанного диапазона или набора. <br>
 * <code>{ }</code>: Соответствует любому из указанных вариантов. <br>
 * <code>**</code>: Соответствует любому количеству директорий.
 */
public class FileSearcher {

    private FileSearcher() {}

    public static List<Path> getLogFiles(String globPattern, Path root) {
        List<Path> logFiles = new ArrayList<>();
        // если glob содержит только имя файла без директорий (например *.txt, log*),
        // на соответствие проверяются найденные файлы из всех директорий
        //
        // если glob содержит элементы пути (например **/*.txt, log/info/*),
        // на соответствие проверяется путь к файл относительно корневой директории
        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attribs) {
                FileSystem fs = FileSystems.getDefault();
                PathMatcher matcher = fs.getPathMatcher("glob:" + globPattern);
                Path relativePath = root.relativize(file);
                Path name = relativePath.getFileName();
                // если glob содержит элементы пути, второе условие всегда неверно
                if (matcher.matches(relativePath) || matcher.matches(name)) {
                    logFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        };
        try {
            Files.walkFileTree(root, matcherVisitor);
            return logFiles;
        } catch (Exception e) {
            throw new FindingFilesException("Error finding logs from root " + root + " with glob " + globPattern, e);
        }
    }
}
