package net.lightning.trealm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.lightning.trealm.VisionItemRegistration.Frame;
import net.lightning.trealm.VisionItemRegistration.Gem;
import net.minecraft.SharedConstants;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.GenerationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TravelersRealm implements ModInitializer {
    public static final String MOD_NAMESPACE = "trealm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

    @Override
    public void onInitialize() {
        // Enable development mode.
        SharedConstants.isDevelopment = true;

        // Register commands.
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> PlaceStructureCommand.register(dispatcher));

        // Bind the abyss world.
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            AbyssPlacement.abyssWorld = server.getWorld(RegistryKey.of(RegistryKeys.WORLD, new Identifier(MOD_NAMESPACE, "abyss")));
        });

        // Register all the items in the mod.
        for (final Registration.RegistrableItem item : TravelersRealmDataGenerator.ITEMS) {
            item.register();
        }

        // Register all the blocks in the mod.
        for (final Registration.RegistrableBlock block : TravelersRealmDataGenerator.BLOCKS) {
            block.register();
        }

        // Pick a random vision as the icon for the visions creative tab.
        final Random random = new Random(System.currentTimeMillis());
        final Frame[] frames = Frame.values();
        final Gem[] gems = Gem.values();
        final ItemStack visionTabIcon = new ItemStack(VisionItemRegistration.getVisionItem(
            frames[random.nextInt(frames.length)],
            gems[random.nextInt(gems.length)]
        ));

        // Create a new creative tab for visions.
        Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(MOD_NAMESPACE, "visions_tab"),
            FabricItemGroup.builder()
                .displayName(Text.of("Visions"))
                .icon(() -> visionTabIcon)
                .entries((displayContext, entries) -> {
                    for (final Frame frame : Frame.values()) {
                        for (final Gem gem : Gem.values()) {
                            entries.add(VisionItemRegistration.getVisionItem(frame, gem));
                        }
                        entries.add(frame);
                    }
                    for (final Gem gem : Gem.values()) {
                        entries.add(gem);
                    }
                })
                .build()
        );

        // Modify existing creative tabs to include new items.
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register((entries) -> {
            for (final ItemConvertible foodItem : TravelersRealmDataGenerator.FOOD_ITEMS) {
                entries.add(foodItem);
            }
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((entries) -> {
            for (final ItemConvertible ingredientItem : TravelersRealmDataGenerator.INGREDIENT_ITEMS) {
                entries.add(ingredientItem);
            }
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register((entries) -> {
            entries.add(TravelersRealmDataGenerator.DEEPSLATE_ELEMENTAL_ORE);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register((entries) -> {
            entries.add(TravelersRealmDataGenerator.ELEMENTAL_BLOCK);
            entries.add(TravelersRealmDataGenerator.RAW_ELEMENTAL_BLOCK);
        });

        // Ore Generation.
        BiomeModifications.addFeature(
            BiomeSelectors.foundInOverworld(),
            GenerationStep.Feature.UNDERGROUND_ORES,
            ModWorldGenerator.ModPlacedFeatures.ELEMENTAL_ORE_PLACED_KEY
        );

        // We're done! :)
        LOGGER.info("Loaded");
    }
    private int placeBlock(ServerCommandSource source) {
        final ServerWorld world = source.getWorld();

        final Block[][] blocksToPlace = {
            {Blocks.STONE, Blocks.DIAMOND_BLOCK, Blocks.GOLD_BLOCK},
            {Blocks.IRON_BLOCK, Blocks.EMERALD_BLOCK, Blocks.REDSTONE_BLOCK},
            {Blocks.LAPIS_BLOCK, Blocks.OBSIDIAN, Blocks.NETHERITE_BLOCK}
        };

        final int arrayWidth = blocksToPlace[0].length;
        final int arrayHeight = blocksToPlace.length;

        BlockPos startPos = new BlockPos(0, 100, 0);

        while (this.isOccupied(world, startPos, arrayWidth, arrayHeight)){
            startPos = startPos.add(arrayWidth * 2, 0, 0);
        }

        for (int row = 0; row < blocksToPlace.length; row++) {
            for (int col = 0; col < blocksToPlace[row].length; col++) {
                final BlockPos pos = startPos.add(col, 0, row);
                world.setBlockState(pos, blocksToPlace[row][col].getDefaultState());
            }
        }
        final BlockPos finalStartPos = startPos;
        source.sendFeedback(() -> Text.literal("Placed block at " + finalStartPos.toShortString()), true);
        return 1;
    }

    private boolean isOccupied(ServerWorld world, BlockPos startPos, int width, int height) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                final BlockPos pos = startPos.add(col, 0, row);
                if (!world.getBlockState(pos).isAir()) {
                    return true;
                }
            }
        }
        return false;
    }
}
