package com.LegendarySpy.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();
    private static final File configFile = new File("config/connected_core.properties");

    // Default values
    public static boolean fireworkBoostEnabled = true;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        // Create file if it doesn't exist
        if (!configFile.exists()) {
            createDefaultConfig();
        }

        // Load properties from file
        try (FileInputStream inputStream = new FileInputStream(configFile)) {
            properties.load(inputStream);
            fireworkBoostEnabled = Boolean.parseBoolean(properties.getProperty("fireworkBoostEnabled", "true"));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load config; using default values.");
        }
    }

    private static void createDefaultConfig() {
        properties.setProperty("fireworkBoostEnabled", String.valueOf(fireworkBoostEnabled));

        // Save default properties to file
        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            properties.store(outputStream, "Mixins Configuration File");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to create default config.");
        }
    }

    public static void saveConfig() {
        // Update properties
        properties.setProperty("fireworkBoostEnabled", String.valueOf(fireworkBoostEnabled));

        // Save to file
        try (FileOutputStream outputStream = new FileOutputStream(configFile)) {
            properties.store(outputStream, "Mixins Configuration File");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save config.");
        }
    }
}
