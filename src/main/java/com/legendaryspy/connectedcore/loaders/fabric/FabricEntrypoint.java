//? if fabric {
package com.legendaryspy.connectedcore.loaders.fabric;

import com.legendaryspy.connectedcore.ConnectedCore;
import com.legendaryspy.connectedcore.command.ConnectedCoreCommands;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

public final class FabricEntrypoint implements ModInitializer {
	private static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		LOGGER.info("Initializing Connected Core on Fabric");
		ConnectedCore.initialize(FabricLoader.getInstance().getConfigDir());
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ConnectedCoreCommands.register(dispatcher));
	}
}
//?}
