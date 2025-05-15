package com.rentalapp.util;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for file operations.
 */
public class FileUtil {
    private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());
    
    // Base directory for data files
    private static final String DATA_DIR = "src/main/resources/data/";
    // Base directory for file uploads
    private static final String UPLOAD_DIR = "src/main/resources/uploads/";
    
    /**
     * Initialize the necessary directories if they don't exist.
     */
    public static void initializeDirectories() {
        createDirectoryIfNotExists(DATA_DIR);
        createDirectoryIfNotExists(UPLOAD_DIR + "payment-slips/");
    }
    
    /**
     * Creates a directory if it doesn't exist.
     * 
     * @param directoryPath the path of the directory to create
     */
    public static void createDirectoryIfNotExists(String directoryPath) {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                LOGGER.info("Created directory: " + directoryPath);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to create directory: " + directoryPath, e);
            }
        }
    }
    
    /**
     * Read all lines from a file.
     * 
     * @param fileName the name of the file to read
     * @return a list of strings, each representing a line in the file
     */
    public static List<String> readAllLines(String fileName) {
        List<String> lines = new ArrayList<>();
        Path filePath = Paths.get(DATA_DIR + fileName);
        
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
                LOGGER.info("Created file: " + filePath);
                return lines;
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Failed to create file: " + filePath, e);
                return lines;
            }
        }
        
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "r");
             FileChannel channel = file.getChannel()) {
            
            // Acquire a shared lock for reading
            try (FileLock lock = channel.lock(0, Long.MAX_VALUE, true)) {
                BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()));
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading file: " + fileName, e);
        }
        
        return lines;
    }
    
    /**
     * Write all lines to a file, overwriting any existing content.
     * 
     * @param fileName the name of the file to write to
     * @param lines the lines to write to the file
     * @return true if successful, false otherwise
     */
    public static boolean writeAllLines(String fileName, List<String> lines) {
        Path filePath = Paths.get(DATA_DIR + fileName);
        
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "rw");
             FileChannel channel = file.getChannel()) {
            
            // Acquire an exclusive lock for writing
            try (FileLock lock = channel.lock()) {
                // Truncate the file to zero length
                channel.truncate(0);
                
                // Write all lines
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()))) {
                    for (String line : lines) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
                
                return true;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error writing to file: " + fileName, e);
            return false;
        }
    }
    
    /**
     * Append a line to a file.
     * 
     * @param fileName the name of the file to append to
     * @param line the line to append
     * @return true if successful, false otherwise
     */
    public static boolean appendLine(String fileName, String line) {
        Path filePath = Paths.get(DATA_DIR + fileName);
        
        try (RandomAccessFile file = new RandomAccessFile(filePath.toFile(), "rw");
             FileChannel channel = file.getChannel()) {
            
            // Acquire an exclusive lock for writing
            try (FileLock lock = channel.lock()) {
                // Move to the end of the file
                file.seek(file.length());
                
                // Append the line
                file.writeBytes(line + System.lineSeparator());
                
                return true;
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error appending to file: " + fileName, e);
            return false;
        }
    }
    
    /**
     * Generate a unique ID.
     * 
     * @return a unique ID
     */
    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Saves an uploaded file to the appropriate directory.
     * 
     * @param inputStream the input stream of the uploaded file
     * @param fileName the name of the file
     * @param subDirectory the subdirectory within the upload directory
     * @return the path to the saved file
     * @throws IOException if an I/O error occurs
     */
    public static String saveUploadedFile(InputStream inputStream, String fileName, String subDirectory) throws IOException {
        String directory = UPLOAD_DIR + subDirectory;
        createDirectoryIfNotExists(directory);
        
        // Generate unique filename to prevent overwriting
        String uniqueFileName = generateUniqueId() + "_" + fileName;
        Path filePath = Paths.get(directory, uniqueFileName);
        
        // Copy the file
        Files.copy(inputStream, filePath);
        
        return filePath.toString();
    }
    
    /**
     * Deletes a file.
     * 
     * @param filePath the path of the file to delete
     * @return true if successful, false otherwise
     */
    public static boolean deleteFile(String filePath) {
        try {
            return Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error deleting file: " + filePath, e);
            return false;
        }
    }
}
