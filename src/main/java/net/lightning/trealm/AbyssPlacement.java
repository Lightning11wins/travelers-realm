package net.lightning.trealm;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;

import java.nio.file.FileSystemNotFoundException;

import static net.lightning.trealm.AbyssLevel.Structure.DEFAULT_STRUCTURE;
import static net.lightning.trealm.AbyssLevel.Structure.Type.ROOM_END;
import static net.minecraft.block.Block.NOTIFY_LISTENERS;

public class AbyssPlacement {
    public static final int ROOM_SIZE = 35;
    public static ServerWorld abyssWorld;
    public static int NEXT_SAFE_X = 0;

    public final BlockPos origin;
    public final AbyssLevel.Tile[][] grid;
    public final long seed;

    public static AbyssPlacement newAbyssLevel() {
        final BlockPos blockPos = new BlockPos(0, 0, NEXT_SAFE_X);
        final AbyssPlacement abyssPlacement = new AbyssPlacement(blockPos);
        NEXT_SAFE_X += abyssPlacement.grid.length * ROOM_SIZE + 1;
        return abyssPlacement;
    }

    public AbyssPlacement(BlockPos origin, AbyssLevel.Tile[][] grid, long seed) {
        this.origin = origin;
        this.grid = grid;
        this.seed = seed;
        this.placeAbyssLevel();
    }
    public AbyssPlacement(BlockPos origin) {
        this.origin = origin;

        final AbyssLevel abyssLevel = AbyssLevel.genLevel();
        this.grid = abyssLevel.toGrid();
        this.seed = abyssLevel.seed;
        this.placeAbyssLevel();
    }

    public void placeAbyssLevel() {
        final BlockPos origin = this.origin;
        final AbyssLevel.Tile[][] grid = this.grid;
        final ServerWorld abyssWorld = AbyssPlacement.abyssWorld;
        final StructureTemplateManager structureTemplateManager = abyssWorld.getStructureTemplateManager();
        final StructureTemplate defaultStructureTemplate = structureTemplateManager
            .getTemplate(DEFAULT_STRUCTURE)
            .orElseThrow(() -> new FileSystemNotFoundException("Unknown default structure " + DEFAULT_STRUCTURE));

        final int initialI = grid[0].length - 1;
        for (int x = grid.length - 1; x >= 0; x--) {
            final AbyssLevel.Tile[] row = grid[x];
            for (int i = initialI, z = 0; i >= 0; i--, z++) {
                final AbyssLevel.Tile currentTile = row[i];
                if (currentTile != null) {
                    final AbyssLevel.Tile.Orientation orientation = currentTile.orientation;
                    final int direction = orientation.ordinal(), roomSize = ROOM_SIZE;
                    final int offsetX = ((direction + 1) / 2 % 2), offsetZ = (direction / 2 % 2);
                    final int offsetY = (currentTile.structure.type == ROOM_END) ? -12 : 0;
                    final BlockPos pos = origin.add(roomSize * (x + offsetX) - offsetX, offsetY, roomSize * (z + offsetZ) - offsetZ);

                    final StructurePlacementData placementData = new StructurePlacementData()
                        .setRotation(switch (orientation) {
                            case UP, UP_FLIPPED -> BlockRotation.NONE;
                            case RIGHT, LEFT_FLIPPED -> BlockRotation.CLOCKWISE_90;
                            case DOWN, DOWN_FLIPPED -> BlockRotation.CLOCKWISE_180;
                            case LEFT, RIGHT_FLIPPED -> BlockRotation.COUNTERCLOCKWISE_90;
                        })
                        .setMirror(orientation.isFlipped() ? BlockMirror.LEFT_RIGHT : BlockMirror.NONE);
                    structureTemplateManager
                        .getTemplate(currentTile.structure.identifier)
                        .orElse(defaultStructureTemplate)
                        .place(abyssWorld, pos, pos, placementData, abyssWorld.getRandom(), NOTIFY_LISTENERS);
                }
            }
        }
    }

    public String getStringMap() {
        return AbyssLevel.toString(this.grid);
    }

    public static AbyssPlacement build() {
        return new AbyssPlacement(new BlockPos(0, 200, 0));
    }
}
