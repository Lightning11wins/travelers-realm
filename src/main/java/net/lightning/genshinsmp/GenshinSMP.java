package net.lightning.genshinsmp;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenshinSMP implements ModInitializer {
	public static final String MOD_ID = "genshinsmp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final ModItem HENSKULL = new ModItem(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(3)
        .saturationModifier(1.2f)
        .snack()
        .alwaysEdible()
        .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200), 1)
        .build()
	)).setName("henskull").setTexture("item/henskull").setDisplayName("HenSkull");
    public static final ModItem FRAME1 = new ModItem().setName("frame1").setTexture("item/vision/frame/socket1").setDisplayName("Hexagonal Frame");
    public static final ModItem FRAME2 = new ModItem().setName("frame2").setTexture("item/vision/frame/socket2").setDisplayName("Circular Frame");
    public static final VisionItem[] VISION_ITEMS = VisionItem.constructAll();

	@Override
	public void onInitialize() {
        ModItem.initializeItems();
		LOGGER.info("Mod initialized!");
	}
}
