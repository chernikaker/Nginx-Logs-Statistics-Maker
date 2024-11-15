package backend.academy.project.readers;

import backend.academy.project.readers.exception.FindingFilesException;
import java.io.IOException;
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

public class FileSearcher {

    private FileSearcher() {
        throw new UnsupportedOperationException("FileSearcher should not be instantiated");
    }

    public static List<Path> getLogFiles(String globPattern, Path root) {

        List<Path> logFiles = new ArrayList<>();

        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attribs) {
                FileSystem fs = FileSystems.getDefault();
                String newGlobPattern =  globPattern;

                PathMatcher matcher = fs.getPathMatcher("glob:" + newGlobPattern);
                Path relativePath = root.relativize(file);
                if (matcher.matches(relativePath)) {
                    logFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        };
        try {
            Files.walkFileTree(root, matcherVisitor);
            return logFiles;
        } catch (IOException e) {
            throw new FindingFilesException("Error finding logs from root " + root + " with glob " + globPattern, e);
        }
    }
}
