package net.lightning.genshinsmp;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenshinSMP implements ModInitializer {
	public static final String MOD_ID = "genshinsmp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Mod initialized!");
	}
}
