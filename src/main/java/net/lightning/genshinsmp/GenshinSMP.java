package net.lightning.genshinsmp;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.FoodComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenshinSMP implements ModInitializer {
	public static final String MOD_ID = "genshinsmp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Mod initialized!");

		Item.Settings henskullSettings = new FabricItemSettings();
		henskullSettings.food(new FoodComponent.Builder()
			.hunger(3)
			.saturationModifier(1.2f)
			.snack()
			.alwaysEdible()
			.statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200), 1)
			.build()
		);

		Item henskull = registerItem("henskull", new Item(henskullSettings));

		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((FabricItemGroupEntries entries) -> {
			entries.add(henskull);
		});
	}

	public static Item registerItem(String name, Item item) {
		return Registry.register(Registries.ITEM, new Identifier(MOD_ID, name), item);
	}
}
