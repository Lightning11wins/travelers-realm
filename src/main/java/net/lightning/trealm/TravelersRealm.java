package net.lightning.trealm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.lightning.trealm.VisionItem.Frame;
import net.lightning.trealm.VisionItem.Gem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.GenerationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TravelersRealm implements ModInitializer {
    public static final String MOD_NAMESPACE = "trealm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

    @Override
    public void onInitialize() {
        // Register all the items in the mod.
        for (ItemRegistrable item : TravelersRealmDataGenerator.ITEMS) {
            item.registerItem();
        }

        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Feature.UNDERGROUND_ORES,
            ModWorldGenerator.ModPlacedFeatures.ELEMENTAL_ORE_PLACED_KEY
        );

        // Create a new creative tab and add all the new items to it.
        Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(MOD_NAMESPACE, "visions_tab"),
            FabricItemGroup.builder()
                .displayName(Text.of("Travelers Realm"))
                .icon(() -> {
                    final Random random = new Random(System.currentTimeMillis());
                    final VisionItem.Frame[] frames = Frame.values();
                    final Frame frame = frames[random.nextInt(frames.length)];
                    final Gem[] gems = Gem.values();
                    final Gem gem = gems[random.nextInt(gems.length)];
                    return new ItemStack(VisionItem.getVisionItem(frame, gem));
                })
                .entries((displayContext, entries) -> {
                    for (Frame frame : Frame.values()) {
                        for (Gem gem : Gem.values()) {
                            entries.add(VisionItem.getVisionItem(frame, gem));
                        }
                        entries.add(frame.itemData);
                    }
                    for (Gem gem : Gem.values()) {
                        entries.add(gem.itemData);
                    }
                })
                .build()
        );

        // We're done! :)
        LOGGER.info("Mod initialized!");
    }
}
