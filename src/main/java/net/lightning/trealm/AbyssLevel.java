package net.lightning.trealm;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;

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
        int lootRooms = random.nextInt(MIN_LOOT_ROOMS, MAX_LOOT_ROOMS);

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
            if (lootRooms > 0) types[i++] = Structure.Type.ROOM_LOOT;
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
                case ROOM_LOOT -> lootRooms--;
            }

            this.generateTile(coordinates, type);
        }
        return this;
    }
    public Tile[][] toGrid() {
        if (this.grid != null) {
            return this.grid;
        }

        // TODO: Make this less confusing by removing useless premature optimizations.
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

    public static AbyssLevel genLevel() {
        AbyssLevel level;
        do {
            level = new AbyssLevel().generateDungeon();
        } while (!level.isValid);
        level.toGrid();
        return level;
    }
    public static String toString(Tile[][] grid) {
        final StringBuilder sb = new StringBuilder();
        for (int z = grid[0].length - 1; z >= 0; z--) {
            for (final AbyssLevel.Tile[] tiles : grid) {
                AbyssLevel.Tile.appendTo(sb, tiles[z]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    public static Tile[][] fromString(String string, Random random) {
        final Structure.Type hallway = Structure.Type.HALLWAY;

        final String[] strings = string.split("\n");
        final Iterator<String> lines = Arrays.stream(strings).iterator();
        final Tile[][] grid = new Tile[strings[0].length()][strings.length];

        for (int row = strings.length - 1; row >= 0; row--) {
            final char[] line = lines.next().trim().toCharArray();
            for (int col = 1; col < line.length; col++) {
                grid[col][row] = switch (line[col]) {
                    case '┼' -> new Tile(Tile.Orientation.UP, Structure.random(hallway, Structure.Entrances.FOUR_WAY, random));
                    case '┬' -> new Tile(Tile.Orientation.UP, Structure.random(hallway, Structure.Entrances.THREE_WAY, random));
                    case '┤' -> new Tile(Tile.Orientation.RIGHT, Structure.random(hallway, Structure.Entrances.THREE_WAY, random));
                    case '┴' -> new Tile(Tile.Orientation.DOWN, Structure.random(hallway, Structure.Entrances.THREE_WAY, random));
                    case '├' -> new Tile(Tile.Orientation.LEFT, Structure.random(hallway, Structure.Entrances.THREE_WAY, random));
                    case '┐' -> new Tile(Tile.Orientation.UP, Structure.random(hallway, Structure.Entrances.TURN, random));
                    case '┘' -> new Tile(Tile.Orientation.RIGHT, Structure.random(hallway, Structure.Entrances.TURN, random));
                    case '└' -> new Tile(Tile.Orientation.DOWN, Structure.random(hallway, Structure.Entrances.TURN, random));
                    case '┌' -> new Tile(Tile.Orientation.LEFT, Structure.random(hallway, Structure.Entrances.TURN, random));
                    case '─' -> new Tile(Tile.Orientation.UP, Structure.random(hallway, Structure.Entrances.STRAIGHT, random));
                    case '│' -> new Tile(Tile.Orientation.LEFT, Structure.random(hallway, Structure.Entrances.STRAIGHT, random));
                    case '.' -> new Tile(Tile.Orientation.UP, Structure.random(hallway, Structure.Entrances.DEAD_END, random));
                    case '<' -> new Tile(Tile.Orientation.RIGHT, Structure.random(hallway, Structure.Entrances.DEAD_END, random));
                    case '^' -> new Tile(Tile.Orientation.DOWN, Structure.random(hallway, Structure.Entrances.DEAD_END, random));
                    case '>' -> new Tile(Tile.Orientation.LEFT, Structure.random(hallway, Structure.Entrances.DEAD_END, random));
                    default -> null;
                };
            }
        }
        return grid;
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
            public boolean isFlipped() {
                return this.ordinal() > 3;
            }
            public int getRotation() {
                return this.ordinal() % 4;
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
                case ROOM_LOOT -> sb.append("\u001B[33m");   // yellow
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
                    case UP    -> '╗'; // ┐
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

        public static final Identifier DEFAULT_STRUCTURE = new Identifier(MOD_NAMESPACE, "invalid");
        public static final int[] STRUCTURE_COUNTS = {
         // 4, 3, s, t, 1
            1, 1, 3, 1, 2, // HALLWAY
            1, 1, 1, 1, 1, // ROOM_START
            4, 1, 1, 1, 1, // ROOM_COMBAT
            0, 0, 0, 0, 2, // ROOM_LOOT
            1, 1, 1, 1, 1, // ROOM_TRAP
            1, 1, 1, 1, 1, // ROOM_END
        };

        public final Type type;
        public final Entrances entrances;
        public final Identifier identifier;

        public Structure(Type type, Entrances entrances, Identifier identifier) {
            this.type = type;
            this.identifier = identifier;
            this.entrances = entrances;
        }

        public static Structure random(Type type, Entrances entrances, Random random) {
            final int structureCount = STRUCTURE_COUNTS[type.ordinal() * 5 + entrances.ordinal()];
            final String structureName = String.format("%s_%s_%d", type.toString().toLowerCase(), entrances.toString().toLowerCase(), random.nextInt(1, structureCount + 1));
            return new Structure(type, entrances, (structureCount == 0) ? DEFAULT_STRUCTURE : new Identifier(MOD_NAMESPACE, structureName));
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
