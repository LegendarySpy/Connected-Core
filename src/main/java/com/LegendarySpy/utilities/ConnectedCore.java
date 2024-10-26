package com.LegendarySpy.utilities;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("connected_core")
public class ConnectedCore {
    public static final String MOD_ID = "connected_core";
    private static final Logger LOGGER = LogManager.getLogger();


    public ConnectedCore(IEventBus modEventBus) {
        // Register the setup method for mod loading
        modEventBus.addListener(this::setup);

        // Load config
        Config.loadConfig();

        LOGGER.info("Connected Core initialized!");
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Connected Core setup phase");
    }
}