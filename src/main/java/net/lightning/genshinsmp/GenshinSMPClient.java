package net.lightning.genshinsmp;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;

public class GenshinSMPClient implements ClientModInitializer {
    public static final String MOD_ID = GenshinSMP.MOD_ID;
    public static final Logger LOGGER = GenshinSMP.LOGGER;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client mod initialized!");
    }
}
