package net.lightning.trealm;

import com.mojang.brigadier.CommandDispatcher;
import net.lightning.trealm.AbyssLevel.Tile;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;


public class PlaceStructureCommand {

    public static final int ROOM_SIZE = 35;

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("startabyss")
          /*.then(CommandManager.argument("structure_id", StringArgumentType.string())
            .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos()) */
            .executes(context -> {
//                String structureId = StringArgumentType.getString(context, "structure_id");
//                BlockPos pos = BlockPosArgumentType.getBlockPos(context, "pos");
                placeAbyssLevel(context.getSource().getWorld());
                return 1;
            }));
    }

    public static long placeAbyssLevel(ServerWorld world) {
        AbyssLevel abyssLevel = AbyssLevel.genLevel();
        Tile[][] level = abyssLevel.toGrid();

        for (int x = 0; x < level.length; x++){
            for (int z = 0; z < level[0].length; z++) {
                Tile currentTile = level[x][z];
                if (currentTile != null) {
                    BlockPos pos = new BlockPos(x * ROOM_SIZE, 200, z * ROOM_SIZE);
                    PlaceStructureCommand.placeStructure(world, currentTile.structure.name, pos);
                }
            }
        }
        return abyssLevel.seed;
    }

    public static int placeStructure(ServerWorld world, String structureId, BlockPos pos) {
        StructureTemplateManager structureManager = world.getStructureTemplateManager();
        Identifier structureIdentifier = new Identifier(MOD_NAMESPACE, structureId);
        StructureTemplate template = structureManager.getTemplate(structureIdentifier).orElse(null);

        if (template != null) {
            StructurePlacementData placementData = new StructurePlacementData();
            template.place(world, pos, pos, placementData, world.getRandom(), 2);

            return 1;
        }
        return 0;
    }
}