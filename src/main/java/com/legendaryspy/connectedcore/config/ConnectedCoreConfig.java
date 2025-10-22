package com.legendaryspy.connectedcore.config;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public final class ConnectedCoreConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FILE_NAME = "connected-core.toml";
    private static final String LEGACY_FILE_NAME = "connected-core.properties";
    private static final String FIREWORK_KEY = "FireworkBoosting";
    private static final boolean DEFAULT_FIREWORK_BOOSTING = false;
    private static final String CONFIG_HEADER = "# Connected Core config";

    private final Path configPath;
    private boolean fireworkBoosting;

    private ConnectedCoreConfig(Path configPath, boolean fireworkBoosting) {
        this.configPath = configPath;
        this.fireworkBoosting = fireworkBoosting;
    }

    public static ConnectedCoreConfig inMemory() {
        return new ConnectedCoreConfig(null, false);
    }

    public static ConnectedCoreConfig load(Path configDirectory) {
        Path resolvedPath = configDirectory.resolve(FILE_NAME);
        Path legacyPath = configDirectory.resolve(LEGACY_FILE_NAME);
        try {
            Files.createDirectories(configDirectory);
        } catch (IOException exception) {
            LOGGER.warn("Unable to create config directory {}. Falling back to in-memory defaults.", configDirectory, exception);
            return inMemory();
        }

        boolean fireworkBoosting = DEFAULT_FIREWORK_BOOSTING;
        boolean migrated = false;

        if (Files.exists(resolvedPath)) {
            try {
                fireworkBoosting = readBooleanFromToml(resolvedPath, FIREWORK_KEY, DEFAULT_FIREWORK_BOOSTING);
            } catch (IOException exception) {
                LOGGER.warn("Failed to read config {}. Rewriting defaults.", resolvedPath, exception);
            }
        } else if (Files.exists(legacyPath)) {
            fireworkBoosting = readBooleanFromLegacyProperties(legacyPath, FIREWORK_KEY, DEFAULT_FIREWORK_BOOSTING);
            migrated = true;
        } else {
            LOGGER.info("Creating default Connected Core config at {}", resolvedPath);
        }

        ConnectedCoreConfig config = new ConnectedCoreConfig(resolvedPath, fireworkBoosting);
        config.save();
        if (migrated) {
            try {
                Files.deleteIfExists(legacyPath);
                LOGGER.info("Removed legacy Connected Core config at {}", legacyPath);
            } catch (IOException exception) {
                LOGGER.warn("Failed to delete legacy config file {}.", legacyPath, exception);
            }
        }
        LOGGER.info("Loaded Connected Core config from {}", resolvedPath);
        return config;
    }

    public boolean isFireworkBoostingEnabled() {
        return fireworkBoosting;
    }

    public boolean updateFireworkBoosting(boolean enabled) {
        if (this.fireworkBoosting == enabled) {
            return false;
        }

        this.fireworkBoosting = enabled;
        save();
        return true;
    }

    public void save() {
        if (configPath == null) {
            LOGGER.warn("Skipping config save because no file path is available.");
            return;
        }

        try {
            Path parent = configPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(configPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                writer.write(CONFIG_HEADER);
                writer.newLine();
                writer.write(FIREWORK_KEY + " = " + enabledToString(fireworkBoosting));
                writer.newLine();
            }
        } catch (IOException exception) {
            LOGGER.warn("Failed to write config file {}.", configPath, exception);
        }
    }

    private static String enabledToString(boolean enabled) {
        return enabled ? "true" : "false";
    }

    private static boolean readBooleanFromToml(Path path, String key, boolean defaultValue) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String sanitized = stripTomlComment(line);
                if (sanitized.isEmpty()) {
                    continue;
                }

                int equalsIndex = sanitized.indexOf('=');
                if (equalsIndex < 0) {
                    continue;
                }

                String entryKey = sanitized.substring(0, equalsIndex).trim();
                if (!key.equals(entryKey)) {
                    continue;
                }

                String value = sanitized.substring(equalsIndex + 1).trim();
                if (value.equalsIgnoreCase("true")) {
                    return true;
                }
                if (value.equalsIgnoreCase("false")) {
                    return false;
                }

                LOGGER.warn("Invalid boolean value '{}' for key {} in {}. Using default {}.", value, key, path, defaultValue);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private static String stripTomlComment(String line) {
        int hashIndex = line.indexOf('#');
        if (hashIndex >= 0) {
            line = line.substring(0, hashIndex);
        }
        return line.trim();
    }

    private static boolean readBooleanFromLegacyProperties(Path legacyPath, String key, boolean defaultValue) {
        Properties properties = new Properties();
        try (BufferedReader reader = Files.newBufferedReader(legacyPath)) {
            properties.load(reader);
            String value = properties.getProperty(key);
            if (value != null) {
                return Boolean.parseBoolean(value);
            }
        } catch (IOException exception) {
            LOGGER.warn("Failed to read legacy config {}. Falling back to defaults.", legacyPath, exception);
        }
        return defaultValue;
    }
}
