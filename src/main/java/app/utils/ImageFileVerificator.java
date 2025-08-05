package app.utils;

import java.io.*;
import java.util.Arrays;

public class ImageFileVerificator {

    private static final byte[] PNG_MAGIC = { (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };
    private static final byte[] JPEG_MAGIC = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };
    private static final byte[] GIF87A_MAGIC = { 0x47, 0x49, 0x46, 0x38, 0x37, 0x61 };
    private static final byte[] GIF89A_MAGIC = { 0x47, 0x49, 0x46, 0x38, 0x39, 0x61 };
    private static final byte[] BMP_MAGIC = { 0x42, 0x4D };
    private static final byte[] WEBP_MAGIC = { 0x52, 0x49, 0x46, 0x46 };

    public enum ImageType {
        PNG, JPEG, GIF, BMP, WEBP, UNKNOWN
    }

    public static ImageType verifyImageType(InputStream inputStream) {
        try {
            if (inputStream.markSupported()) {
                inputStream.mark(16);
            }

            byte[] header = new byte[16];
            int bytesRead = inputStream.read(header);

            if (inputStream.markSupported()) {
                inputStream.reset();
            }

            if (bytesRead < 4) {
                return ImageType.UNKNOWN;
            }

            return identifyImageType(header);

        } catch (IOException e) {
            System.err.println("Error reading stream: " + e.getMessage());
            return ImageType.UNKNOWN;
        }
    }

    /**
     * Identifkasi image. yang nanti buat di save
     */
    private static ImageType identifyImageType(byte[] header) {
        // Check PNG
        if (header.length >= PNG_MAGIC.length &&
                Arrays.equals(Arrays.copyOf(header, PNG_MAGIC.length), PNG_MAGIC)) {
            return ImageType.PNG;
        }

        // Check JPEG
        if (header.length >= JPEG_MAGIC.length &&
                Arrays.equals(Arrays.copyOf(header, JPEG_MAGIC.length), JPEG_MAGIC)) {
            return ImageType.JPEG;
        }

        // Check GIF
        if (header.length >= GIF87A_MAGIC.length &&
                (Arrays.equals(Arrays.copyOf(header, GIF87A_MAGIC.length), GIF87A_MAGIC) ||
                        Arrays.equals(Arrays.copyOf(header, GIF89A_MAGIC.length), GIF89A_MAGIC))) {
            return ImageType.GIF;
        }

        // Check BMP
        if (header.length >= BMP_MAGIC.length &&
                Arrays.equals(Arrays.copyOf(header, BMP_MAGIC.length), BMP_MAGIC)) {
            return ImageType.BMP;
        }

        // Check WebP (perlu check RIFF di awal dan WEBP di offset 8)
        if (header.length >= 12 &&
                Arrays.equals(Arrays.copyOf(header, 4), WEBP_MAGIC) &&
                header[8] == 0x57 && header[9] == 0x45 && header[10] == 0x42 && header[11] == 0x50) {
            return ImageType.WEBP;
        }

        return ImageType.UNKNOWN;
    }
}