package com.legendaryspy.connectedcore.config;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public final class ConnectedCoreConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String FILE_NAME = "connected-core.properties";
    private static final String FIREWORK_KEY = "FireworkBoosting";

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
        try {
            Files.createDirectories(configDirectory);
        } catch (IOException exception) {
            LOGGER.warn("Unable to create config directory {}. Falling back to in-memory defaults.", configDirectory, exception);
            return inMemory();
        }

        boolean fireworkBoosting = false;

        if (Files.exists(resolvedPath)) {
            Properties properties = new Properties();
            try (Reader reader = Files.newBufferedReader(resolvedPath)) {
                properties.load(reader);
                String value = properties.getProperty(FIREWORK_KEY);
                if (value != null) {
                    fireworkBoosting = Boolean.parseBoolean(value);
                }
            } catch (IOException exception) {
                LOGGER.warn("Failed to read config {}. Rewriting defaults.", resolvedPath, exception);
            }
        } else {
            LOGGER.info("Creating default Connected Core config at {}", resolvedPath);
        }

        ConnectedCoreConfig config = new ConnectedCoreConfig(resolvedPath, fireworkBoosting);
        config.save();
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

        Properties properties = new Properties();
        properties.setProperty(FIREWORK_KEY, enabledToString(fireworkBoosting));

        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                properties.store(writer, "Connected Core configuration");
            }
        } catch (IOException exception) {
            LOGGER.warn("Failed to write config file {}.", configPath, exception);
        }
    }

    private static String enabledToString(boolean enabled) {
        return enabled ? "True" : "False";
    }
}
