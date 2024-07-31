package net.lightning.trealm;

import net.fabricmc.api.ClientModInitializer;

import static net.lightning.trealm.TravelersRealm.LOGGER;

public class TravelersRealmClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client mod initialized!");
    }
}
