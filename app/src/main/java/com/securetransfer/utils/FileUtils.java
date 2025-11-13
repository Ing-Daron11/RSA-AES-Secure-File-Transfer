package com.securetransfer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    
    /**
     * Saves byte array to a file, creating directories if needed
     */
    public static void saveFile(String filePath, byte[] data) throws IOException {
        File file = new File(filePath);
        
        // Create parent directories if they don't exist
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        
        // Write data to file
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            fos.flush();
        }
    }

    /**
     * Reads a file and returns its content as byte array
     */
    public static byte[] readFile(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }

    /**
     * Checks if a file exists
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * Deletes a file
     */
    public static boolean deleteFile(String filePath) {
        return new File(filePath).delete();
    }
}
