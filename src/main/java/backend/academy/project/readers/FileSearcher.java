package backend.academy.project.readers;

import java.io.FileNotFoundException;
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

    public static List<Path> getLogFiles(String globPattern, Path root) throws FileNotFoundException{

        List<Path> logFiles = new ArrayList<>();

        FileVisitor<Path> matcherVisitor = new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attribs) {
                FileSystem fs = FileSystems.getDefault();
                String newGlobPattern = globPattern.startsWith("**\\") ? globPattern : "**/" + globPattern;

                PathMatcher matcher = fs.getPathMatcher("glob:"+newGlobPattern);
                if (matcher.matches(file)) {
                    logFiles.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        };
        try {
            Files.walkFileTree(root, matcherVisitor);
            return logFiles;
        } catch (IOException e) {
            throw new FileNotFoundException("Error while finding log files from root "+root+" with glob " + globPattern);
        }
    }
}
