package net.lightning.trealm;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AbyssLevel {
    public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3;
    public static final int MIN_COMBAT_ROOMS = 3, MAX_COMBAT_ROOMS = 5;
    public static final int MIN_TRAP_ROOMS = 2, MAX_TRAP_ROOMS = 8;
    public static final int MIN_LOOT_ROOMS = 2, MAX_LOOT_ROOMS = 5;
    public static final int INITIAL_HALLWAY_DECAY = 24, HALLWAY_DECAY_SOFT_THRESHOLD = INITIAL_HALLWAY_DECAY / 3;

    public final long seed;
    public final Random random, randomVariant;
    public final Map<Coordinates, Tile> level = new HashMap<>();
    public final List<Coordinates> gaps = new ArrayList<>(64);
    public int minX, minZ, maxX, maxZ;
    public int hallwayDecay;
    public boolean isValid;
    public Tile[][] grid;

    public AbyssLevel() {
        this(new Random(System.nanoTime()).nextInt());
    }
    public AbyssLevel(long seed) {
        this.seed = seed;
        this.random = new Random(seed);
        this.randomVariant = new Random(seed);
        System.out.printf("Seed: %d\n", seed);
    }

    public boolean pick() {
        final int hallwayDecay = this.hallwayDecay;
        return (hallwayDecay == 0) ? this.random.nextBoolean() :
            this.random.nextInt(0, hallwayDecay) < HALLWAY_DECAY_SOFT_THRESHOLD;
    }
    public AbyssLevel generateTile(Coordinates coordinates, Structure.Type structureType) {
        final Map<Coordinates, Tile> level = this.level;

        final Tile northTile = level.get(coordinates.north());
        final Tile eastTile = level.get(coordinates.east());
        final Tile southTile = level.get(coordinates.south());
        final Tile westTile = level.get(coordinates.west());

        final boolean north = (northTile != null && northTile.hasEntrance(SOUTH)) || (northTile == null && this.pick());
        final boolean east = (eastTile != null && eastTile.hasEntrance(WEST)) || (eastTile == null && this.pick());
        final boolean south = (southTile != null && southTile.hasEntrance(NORTH)) || (southTile == null && this.pick());
        final boolean west = (westTile != null && westTile.hasEntrance(EAST)) || (westTile == null && this.pick());

        final Random random = this.random;
        Tile.Orientation orientation = null;
        Structure.Entrances entrances = null;
        final int entranceCount = (north ? 1 : 0) + (east ? 1 : 0) + (south ? 1 : 0) + (west ? 1 : 0);
        // TODO: Add variation. (flips)
        switch (entranceCount) {
            case 4:
                orientation = Tile.Orientation.from(random.nextInt(0, 4));
                entrances = Structure.Entrances.FOUR_WAY;
                break;
            case 3:
                orientation =
                    !north ? Tile.Orientation.UP :
                    !east ?  Tile.Orientation.RIGHT :
                    !south ? Tile.Orientation.DOWN :
                             Tile.Orientation.LEFT;
                entrances = Structure.Entrances.THREE_WAY;
                break;
            case 2:
                if (south && west) orientation = Tile.Orientation.UP;
                else if (north && west) orientation = Tile.Orientation.RIGHT;
                else if (north && east) orientation = Tile.Orientation.DOWN;
                else if (south && east) orientation = Tile.Orientation.LEFT;
                if (orientation != null) entrances = Structure.Entrances.TURN;
                else if (east /*&& west*/) orientation = Tile.Orientation.UP;
                else /*if (north && south)*/ orientation = Tile.Orientation.RIGHT;
                if (entrances == null) entrances = Structure.Entrances.STRAIGHT;
                break;
            case 1, 0:
                orientation =
                    south ? Tile.Orientation.UP :
                    west ?  Tile.Orientation.RIGHT :
                    north ? Tile.Orientation.DOWN :
                            Tile.Orientation.LEFT;
                entrances = Structure.Entrances.DEAD_END;
                break;
            default: throw new AssertionError("Unexpected entrance count: " + entranceCount);
        }

        return this.generateTile(coordinates, new Tile(orientation, Structure.random(structureType, entrances, this.randomVariant)));
    }
    public AbyssLevel generateTile(Coordinates coordinates, Tile tile) {
        final int x = coordinates.x, z = coordinates.z;
        if (x < this.minX) this.minX = x;
        if (z < this.minZ) this.minZ = z;
        if (x > this.maxX) this.maxX = x;
        if (z > this.maxZ) this.maxZ = z;
        this.level.put(coordinates, tile);

        final List<Coordinates> gaps = this.gaps;
        if (tile.hasEntrance(NORTH)) gaps.add(coordinates.north());
        if (tile.hasEntrance(EAST)) gaps.add(coordinates.east());
        if (tile.hasEntrance(SOUTH)) gaps.add(coordinates.south());
        if (tile.hasEntrance(WEST)) gaps.add(coordinates.west());

        return this;
    }
    public AbyssLevel generateDungeon() {
        final Random random = this.random;
        int combatRooms = random.nextInt(MIN_COMBAT_ROOMS, MAX_COMBAT_ROOMS);
        int trapRooms = random.nextInt(MIN_TRAP_ROOMS, MAX_TRAP_ROOMS);
        int puzzleRooms = random.nextInt(MIN_LOOT_ROOMS, MAX_LOOT_ROOMS);

        this.generateTile(new Coordinates(0, 0), Structure.Type.ROOM_START);

        final List<Coordinates> gaps = this.gaps;
        final Map<Coordinates, Tile> level = this.level;
        while (!gaps.isEmpty()) {
            final int index = 0; //random.nextInt(0, gaps.size());
            final Coordinates coordinates = gaps.remove(index);
            final Tile tile = level.get(coordinates);
            if (tile != null) continue;

            if (this.hallwayDecay > 0) {
                this.hallwayDecay++;
                this.generateTile(coordinates, Structure.Type.HALLWAY);
                continue;
            }

            final Tile northTile = level.get(coordinates.north());
            final Tile eastTile = level.get(coordinates.east());
            final Tile southTile = level.get(coordinates.south());
            final Tile westTile = level.get(coordinates.west());
            if (
                (northTile != null && northTile.structure.type != Structure.Type.HALLWAY) ||
                    (eastTile != null && eastTile.structure.type != Structure.Type.HALLWAY) ||
                    (southTile != null && southTile.structure.type != Structure.Type.HALLWAY) ||
                    (westTile != null && westTile.structure.type != Structure.Type.HALLWAY)
            ) {
                this.generateTile(coordinates, Structure.Type.HALLWAY);
                continue;
            }

            int i = 1;
            final Structure.Type[] types = {Structure.Type.HALLWAY, null, null, null};
            if (combatRooms > 0) types[i++] = Structure.Type.ROOM_COMBAT;
            if (trapRooms > 0) types[i++] = Structure.Type.ROOM_TRAP;
            if (puzzleRooms > 0) types[i++] = Structure.Type.ROOM_LOOT;
            if (i == 1) {
                this.isValid = true;
                this.hallwayDecay = INITIAL_HALLWAY_DECAY;
                this.generateTile(coordinates, Structure.Type.ROOM_END);
                continue;
            }

            final Structure.Type type = types[random.nextInt(0, i)];
            switch (type) {
                case ROOM_COMBAT -> combatRooms--;
                case ROOM_TRAP -> trapRooms--;
                case ROOM_LOOT -> puzzleRooms--;
            }

            this.generateTile(coordinates, type);
        }
        return this;
    }
    public Tile[][] toGrid() {
        if (this.grid != null) {
            return this.grid;
        }

        final int minX = this.minX, minZ = this.minZ, maxX = this.maxX, maxZ = this.maxZ;
        final int totalX = maxX - minX + 1, totalZ = maxZ - minZ + 1;
        final Map<Coordinates, Tile> level = this.level;
        final Tile[][] grid = new Tile[totalX][totalZ];
        for (int x = maxX, i = totalX; x >= minX; x--) {
            final Tile[] row = grid[--i];
            for (int z = maxZ, j = totalZ; z >= minZ; z--) {
                row[--j] = level.remove(new Coordinates(x, z));
            }
        }

        if (!level.isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            final Formatter formatter = new Formatter(sb);
            formatter.format("Incorrect bounds for x and z: x=%d..%d, z=%d..%d\nOut of Bounds:\n", minX, maxX, minZ, maxZ);
            for (final Coordinates coordinates : level.keySet()) {
                sb.append(coordinates.toString()).append('\n');
            }
            final String message = sb.toString();
            System.err.println(message);
            throw new AssertionError("FAILED!!");
        }

        this.grid = grid;
        return grid;
    }

    public static String stringifyGrid(Tile[][] grid, AbyssLevel level) {
        final StringBuilder sb = new StringBuilder();
        final Formatter formatter = new Formatter(sb);
        formatter.format("Dimensions: %d..%d X %d..%d, Decay: %d\n", level.minX, level.maxX, level.minZ, level.maxZ, level.hallwayDecay);
        for (int z = grid[0].length - 1; z >= 0; z--) {
            for (final Tile[] tiles : grid) {
                Tile.appendTo(sb, tiles[z]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    public static AbyssLevel genLevel() {
        AbyssLevel level;
        do {
            level = new AbyssLevel().generateDungeon();
        } while (!level.isValid);
        level.toGrid();
        return level;
    }
    public static void main(String[] args) {
        final long startTime = System.nanoTime();
        AbyssLevel level = null;
        for (int i = 0; i < 2048; i++) {
            level = genLevel();
        }
        final long endTime = System.nanoTime();

//        final int[] genCount = Structure.genCount;
//        final int totalHallways = genCount[0] + genCount[1] + genCount[2] + genCount[3] + genCount[4];
//        final int totalStartRooms = genCount[5] + genCount[6] + genCount[7] + genCount[8] + genCount[9];
//        final int totalCombatRooms = genCount[10] + genCount[11] + genCount[12] + genCount[13] + genCount[14];
//        final int totalLootRooms = genCount[15] + genCount[16] + genCount[17] + genCount[18] + genCount[19];
//        final int totalTrapRooms = genCount[20] + genCount[21] + genCount[22] + genCount[23] + genCount[24];
//        final int totalEndRooms = genCount[25] + genCount[26] + genCount[27] + genCount[28] + genCount[29];
//        final float total = totalHallways + totalStartRooms + totalCombatRooms + totalLootRooms + totalTrapRooms + totalEndRooms;
//
//        final int total_4 = genCount[0] + genCount[5] + genCount[10] + genCount[15] + genCount[20];
//        final int total_3 = genCount[1] + genCount[6] + genCount[11] + genCount[16] + genCount[21];
//        final int total_s = genCount[2] + genCount[7] + genCount[12] + genCount[17] + genCount[22];
//        final int total_t = genCount[3] + genCount[8] + genCount[13] + genCount[18] + genCount[23];
//        final int total_1 = genCount[4] + genCount[9] + genCount[14] + genCount[19] + genCount[24];
//
//        StringBuilder sb = new StringBuilder("  4,     3,     s,     t,     1\n");
//        Formatter formatter = new Formatter(sb);
//        formatter.format("%05d, %05d, %05d, %05d, %05d : %.3f HALLWAY\n", genCount[0], genCount[1], genCount[2], genCount[3], genCount[4], totalHallways / total);
//        formatter.format("%05d, %05d, %05d, %05d, %05d : %.3f ROOM_START\n", genCount[5], genCount[6], genCount[7], genCount[8], genCount[9], totalStartRooms / total);
//        formatter.format("%05d, %05d, %05d, %05d, %05d : %.3f ROOM_COMBAT\n", genCount[10], genCount[11], genCount[12], genCount[13], genCount[14], totalCombatRooms / total);
//        formatter.format("%05d, %05d, %05d, %05d, %05d : %.3f ROOM_LOOT\n", genCount[15], genCount[16], genCount[17], genCount[18], genCount[19], totalLootRooms / total);
//        formatter.format("%05d, %05d, %05d, %05d, %05d : %.3f ROOM_TRAP\n", genCount[20], genCount[21], genCount[22], genCount[23], genCount[24], totalTrapRooms / total);
//        formatter.format("%05d, %05d, %05d, %05d, %05d : %.3f ROOM_END\n", genCount[25], genCount[26], genCount[27], genCount[28], genCount[29], totalEndRooms / total);
//        formatter.format("%.3f, %.3f, %.3f, %.3f, %.3f\n", total_4 / total, total_3 / total, total_s / total, total_t / total, total_1 / total);
//        sb.append(stringifyGrid(level.toGrid(), level)).append('\n');
//        formatter.format("Execution time: %.4f milliseconds\n", (double) (endTime - startTime) / 1_000_000);
//        System.out.print(sb);
    }

    public static class Tile {
        public final Orientation orientation;
        public final Structure structure;

        public Tile(Orientation orientation, Structure structure) {
            this.orientation = orientation;
            this.structure = structure;
        }

        public enum Orientation {
            UP(),
            RIGHT(),
            DOWN(),
            LEFT(),
            UP_FLIPPED(),
            LEFT_FLIPPED(),
            DOWN_FLIPPED(),
            RIGHT_FLIPPED();

            public static Orientation from(int id) {
                return values()[id];
            }
        }

        public boolean hasEntrance(int direction) {
            return this.structure.entrances.has(direction - this.orientation.ordinal());
        }

        @Override
        public String toString() {
            return this.appendTo(new StringBuilder()).toString();
        }
        public StringBuilder appendTo(StringBuilder sb) {
            return Tile.appendTo(sb, this);
        }

        public static StringBuilder appendTo(StringBuilder sb, Tile tile) {
            if (tile == null) {
                return sb.append(' ');
            }

            final Structure structure = tile.structure;
            switch (structure.type) {
                case ROOM_START -> sb.append("\u001B[32m");  // green
                case ROOM_COMBAT -> sb.append("\u001B[31m"); // red
                case ROOM_TRAP -> sb.append("\u001B[34m");   // blue
                case ROOM_LOOT -> sb.append("\u001B[33m"); // yellow
                case ROOM_END -> sb.append("\u001B[35m");    // magenta
            }

            final Orientation orientation = tile.orientation;
            return sb.append(switch (structure.entrances) {
                case FOUR_WAY -> '┼';
                case THREE_WAY -> switch (orientation) {
                    case UP    -> '┬';
                    case RIGHT -> '┤';
                    case DOWN  -> '┴';
                    case LEFT  -> '├';
                    default -> 'A';
                };
                case TURN -> switch (orientation) {
                    case UP    -> '╗';
                    case RIGHT -> '┘';
                    case DOWN  -> '└';
                    case LEFT  -> '┌';
                    default -> 'B';
                };
                case STRAIGHT -> (orientation == Orientation.UP || orientation == Orientation.DOWN) ? '─' : '│';
                case DEAD_END -> switch (orientation) {
                    case UP    -> '.';
                    case RIGHT -> '<';
                    case DOWN  -> '^';
                    case LEFT  -> '>';
                    default -> 'C';
                };
            }).append("\u001B[0m"); // reset to white
        }
    }
    public static class Structure {
        public enum Type {
            HALLWAY(),
            ROOM_START(),
            ROOM_COMBAT(),
            ROOM_LOOT(),
            ROOM_TRAP(),
            ROOM_END(),
        }
        public enum Entrances {
            FOUR_WAY(),
            THREE_WAY(),
            STRAIGHT(),
            TURN(),
            DEAD_END();

            public boolean has(int direction) {
                direction &= 3;
                return switch (direction) {
                    case NORTH -> this == FOUR_WAY;
                    case EAST -> !(this == TURN || this == DEAD_END);
                    case SOUTH -> this != STRAIGHT;
                    case WEST -> this != DEAD_END;
                    default -> throw new AssertionError("Unexpected value: " + direction);
                };
            }
        }

        public static final int[] roomCounts = {
         // 4, 3, s, t, 1
            1, 1, 3, 1, 2, // HALLWAY
            0, 0, 0, 0, 0, // ROOM_START
            4, 0, 0, 0, 0, // ROOM_COMBAT
            2, 0, 0, 0, 0, // ROOM_LOOT
            0, 0, 0, 0, 0, // ROOM_TRAP
            0, 0, 0, 0, 0, // ROOM_END
        };

//        public static final int[] genCount = {
//            // 4, 3, s, t, 1
//            0, 0, 0, 0, 0, // HALLWAY
//            0, 0, 0, 0, 0, // ROOM_START
//            0, 0, 0, 0, 0, // ROOM_COMBAT
//            0, 0, 0, 0, 0, // ROOM_LOOT
//            0, 0, 0, 0, 0, // ROOM_TRAP
//            0, 0, 0, 0, 0, // ROOM_END
//        };

        public final Type type;
        public final Entrances entrances;
        public final String name;

        public Structure(Type type, Entrances entrances, String name) {
            this.type = type;
            this.name = name;
            this.entrances = entrances;
        }

        public static Structure random(Type type, Entrances entrances, Random random) {
//            genCount[type.ordinal() * 5 + entrances.ordinal()]++;
            final int roomCount = roomCounts[type.ordinal() * 5 + entrances.ordinal()];
            return new Structure(type, entrances, (roomCount == 0) ? null :
                String.format("%s_%s_%d", type.toString().toLowerCase(), entrances.toString().toLowerCase(), random.nextInt(roomCount))
            );
        }
    }
    public static class Coordinates {
        public final int x, z;

        public Coordinates(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public Coordinates add(int x, int z) {
            return new Coordinates(this.x + x, this.z + z);
        }
        public Coordinates north() {
            return this.add(0, 1);
        }
        public Coordinates east() {
            return this.add(1, 0);
        }
        public Coordinates south() {
            return this.add(0, -1);
        }
        public Coordinates west() {
            return this.add(-1, 0);
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof Coordinates otherCoordinates) {
                return (this.x == otherCoordinates.x && this.z == otherCoordinates.z);
            }
            return false;
        }
        @Override
        public int hashCode() {
            return (this.z << 8) ^ this.x;
        }
        @Override
        public String toString() {
            return this.appendTo(new StringBuilder()).toString();
        }
        public StringBuilder appendTo(StringBuilder sb) {
            return sb.append('(').append(this.x).append(',').append(this.z).append(')');
        }
    }
}
