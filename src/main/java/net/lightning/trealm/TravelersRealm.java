package net.lightning.trealm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TravelersRealm implements ModInitializer {
	public static final String MOD_NAMESPACE = "trealm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

	@Override
	public void onInitialize() {
        for (ItemRegistrable item : TravelersRealmDataGenerator.ITEMS) {
            item.registerItem();
        }

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((e) -> {
            for (ItemRegistrable item : TravelersRealmDataGenerator.ITEMS) {
                e.add(item);
            }
        });

		LOGGER.info("Mod initialized!");
	}
}
