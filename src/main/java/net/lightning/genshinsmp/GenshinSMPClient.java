package net.lightning.genshinsmp;

import net.fabricmc.api.ClientModInitializer;

import static net.lightning.genshinsmp.GenshinSMP.LOGGER;

public class GenshinSMPClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client mod initialized!");
    }
}
