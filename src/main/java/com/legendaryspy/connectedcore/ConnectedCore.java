package com.legendaryspy.connectedcore;

import com.legendaryspy.connectedcore.config.ConnectedCoreConfig;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.Objects;

public final class ConnectedCore {
	public static final String MOD_ID = "connectedcore";
	private static final Logger LOGGER = LogUtils.getLogger();
	private static ConnectedCoreConfig config = ConnectedCoreConfig.inMemory();

	private ConnectedCore() {
	}

	public static void initialize(Path configDirectory) {
		Objects.requireNonNull(configDirectory, "configDirectory");
		config = ConnectedCoreConfig.load(configDirectory);
		LOGGER.info("Connected Core initialized. Firework boosting enabled: {}", isFireworkBoostingEnabled());
	}

	public static ConnectedCoreConfig getConfig() {
		return config;
	}

	public static boolean isFireworkBoostingEnabled() {
		return config != null && config.isFireworkBoostingEnabled();
	}

	public static boolean shouldPreventFireworkBoosting() {
		return !isFireworkBoostingEnabled();
	}

	public static boolean updateFireworkBoosting(boolean enabled) {
		if (config == null) {
			LOGGER.warn("Config not initialized. Cannot update firework boosting state.");
			return false;
		}

		boolean changed = config.updateFireworkBoosting(enabled);
		if (changed) {
			LOGGER.info("Firework boosting set to {}", enabled);
		}
		return changed;
	}
}
