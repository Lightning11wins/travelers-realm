package net.lightning.trealm;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.lightning.trealm.VisionItem.Frame;
import net.lightning.trealm.VisionItem.Gem;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.GenerationStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

import static net.lightning.trealm.TravelersRealmDataGenerator.ELEMENTAL_INGOT;

public class TravelersRealm implements ModInitializer {
    public static final String MOD_NAMESPACE = "trealm";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAMESPACE);

    @Override
    public void onInitialize() {
        // Register all the items in the mod.
        for (ItemRegistrable item : TravelersRealmDataGenerator.ITEMS) {
            item.registerItem();
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("startabyss")
                    .executes(context -> placeBlock(context.getSource())));
        });
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
    private int placeBlock (ServerCommandSource source){
        ServerWorld world = source.getWorld();

        Block[][] blocksToPlace = {
                {Blocks.STONE, Blocks.DIAMOND_BLOCK, Blocks.GOLD_BLOCK},
                {Blocks.IRON_BLOCK, Blocks.EMERALD_BLOCK, Blocks.REDSTONE_BLOCK},
                {Blocks.LAPIS_BLOCK, Blocks.OBSIDIAN, Blocks.NETHERITE_BLOCK}
        };

        int arrayWidth = blocksToPlace[0].length;
        int arrayHeight = blocksToPlace.length;

        BlockPos startPos = new BlockPos(0, 100, 0);

        while (isOccupied(world, startPos, arrayWidth, arrayHeight)){
            startPos = startPos.add(arrayWidth * 2, 0, 0);
        }

        for (int row = 0; row < blocksToPlace.length; row++) {
            for (int col = 0; col < blocksToPlace[row].length; col++) {
                BlockPos pos = startPos.add(col, 0, row);
                world.setBlockState(pos, blocksToPlace[row][col].getDefaultState());
            }
        }
        BlockPos finalStartPos = startPos;
        source.sendFeedback(() -> Text.literal("Placed block at " + finalStartPos.toShortString()), true);
        return 1;
    }

    private boolean isOccupied(ServerWorld world, BlockPos startPos, int width, int height) {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                BlockPos pos = startPos.add(col, 0, row);
                if (!world.getBlockState(pos).isAir()) {
                    return true;
                }
            }
        }
        return false;
    }
}