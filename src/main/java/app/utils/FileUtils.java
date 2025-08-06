package app.utils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import app.utils.ImageFileVerificator.ImageType;

public class FileUtils {
    private static final String UPLOAD_DIR = "uploads/";

    public static String handleUploads(MultipartFile file, ImageType type) throws IllegalStateException, IOException {
        Path uploadDir = getAbsolutePathFromRelative(UPLOAD_DIR);

        String randomNewFileName = UUID.randomUUID().toString();
        String fileExt = "." + type.name().toLowerCase();

        Path newFileSave = Path.of(randomNewFileName + fileExt);

        file.transferTo(uploadDir.resolve(newFileSave));

        return randomNewFileName + fileExt;
    }

    public static Path getAbsolutePathFromRelative(String relativePath) {
        return Paths.get(relativePath).toAbsolutePath();
    }
}
