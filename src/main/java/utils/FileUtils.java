package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FileUtils {
    public static final String PDF_FILE_EXTENSION = "pdf";

    private FileUtils() {
        throw new UnsupportedOperationException("You're trying to create instance of utility class.");
    }

    /**
     * Get files from selected folder and its sub-folders by specified file extension.
     *
     * @param pathToFolder  folder to get files from.
     * @param fileExtension file extension to filter necessary files.
     * @return list of Paths to found files.
     * @throws IOException in case of IO-related problems.
     */
    public static List<Path> getFilesFromFolderByExtension(Path pathToFolder, String fileExtension)
            throws IOException {
        if (!Files.isDirectory(pathToFolder)) {
            throw new IllegalArgumentException("Path must be a directory!");
        }

        List<Path> result;
        try (Stream<Path> walk = Files.walk(pathToFolder)) {
            result = walk
                    .filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().endsWith(fileExtension))
                    .collect(Collectors.toList());
        }
        return result;
    }
}
