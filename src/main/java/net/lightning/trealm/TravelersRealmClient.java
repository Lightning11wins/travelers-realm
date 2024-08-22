package net.lightning.trealm;

import net.fabricmc.api.ClientModInitializer;
import net.lightning.trealm.AdeptusStoveBlock.AdeptalStoveScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

import static net.lightning.trealm.TravelersRealm.LOGGER;

public class TravelersRealmClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HandledScreens.register(TravelersRealmDataGenerator.ADEPTAL_STOVE_SCREEN_HANDLER, AdeptalStoveScreen::new);

        LOGGER.info("Client mod initialized!");
    }


}
