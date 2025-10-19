//? if neoforge {
/*package com.legendaryspy.connectedcore.loaders.neoforge;

import com.legendaryspy.connectedcore.ConnectedCore;
import com.legendaryspy.connectedcore.command.ConnectedCoreCommands;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.slf4j.Logger;

@Mod(ConnectedCore.MOD_ID)
public final class NeoforgeEntrypoint {
	private static final Logger LOGGER = LogUtils.getLogger();

	public NeoforgeEntrypoint() {
		LOGGER.info("Initializing Connected Core on NeoForge");
		ConnectedCore.initialize(FMLPaths.CONFIGDIR.get());
		NeoForge.EVENT_BUS.addListener(NeoforgeEntrypoint::onRegisterCommands);
	}

	private static void onRegisterCommands(RegisterCommandsEvent event) {
		ConnectedCoreCommands.register(event.getDispatcher());
	}
}
*///?}
