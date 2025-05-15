package com.rentalapp.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ApplicationInitializer is a ServletContextListener that initializes
 * all required data directories and resources when the application starts.
 */
public class ApplicationInitializer implements ServletContextListener {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);
    
    // Define data directory paths
    private static final String DATA_DIR = "src/main/resources/data";
    private static final String UPLOADS_DIR = "src/main/resources/uploads";
    private static final String PAYMENT_SLIPS_DIR = "src/main/resources/uploads/payment-slips";
    
    /**
     * Initialize application when the web application is starting up
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing Vehicle Rental Application...");
        
        try {
            // Create data directories if they don't exist
            createDirectory(DATA_DIR);
            createDirectory(UPLOADS_DIR);
            createDirectory(PAYMENT_SLIPS_DIR);
            
            // Create initial data files if they don't exist
            createDataFile(DATA_DIR, "vehicles.txt");
            createDataFile(DATA_DIR, "users.txt");
            createDataFile(DATA_DIR, "bookings.txt");
            createDataFile(DATA_DIR, "admins.txt");
            createDataFile(DATA_DIR, "payments.txt");
            createDataFile(DATA_DIR, "reviews.txt");
            
            // Initialize default admin account if no admins exist
            initializeDefaultAdmin();
            
            logger.info("Application initialization completed successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize application", e);
        }
    }
    
    /**
     * Clean up application resources when the web application is shutting down
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Shutting down Vehicle Rental Application...");
    }
    
    /**
     * Create a directory if it doesn't exist
     */
    private void createDirectory(String dirPath) {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
                logger.info("Created directory: {}", dirPath);
            } catch (Exception e) {
                logger.error("Failed to create directory: {}", dirPath, e);
            }
        }
    }
    
    /**
     * Create an empty data file if it doesn't exist
     */
    private void createDataFile(String dirPath, String fileName) {
        Path filePath = Paths.get(dirPath, fileName);
        if (!Files.exists(filePath)) {
            try {
                Files.createFile(filePath);
                logger.info("Created data file: {}", filePath);
            } catch (Exception e) {
                logger.error("Failed to create data file: {}", filePath, e);
            }
        }
    }
    
    /**
     * Initialize a default admin account if the admins.txt file is empty
     */
    private void initializeDefaultAdmin() {
        Path adminsFile = Paths.get(DATA_DIR, "admins.txt");
        try {
            if (Files.exists(adminsFile) && Files.size(adminsFile) == 0) {
                // Format: id|username|password|fullName|email|role
                String defaultAdmin = "admin1|admin|admin123|System Administrator|admin@rentalapp.com|ADMIN";
                Files.write(adminsFile, defaultAdmin.getBytes());
                logger.info("Created default admin account");
            }
        } catch (Exception e) {
            logger.error("Failed to initialize default admin account", e);
        }
    }
}