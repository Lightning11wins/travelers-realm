package net.lightning.trealm;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;

public class VisionItem extends ItemData {
    public static final int NUM_FRAMES = Frame.values().length;
    public static final int NUM_GEMS = Gem.values().length;
    public static final VisionItem[] items = new VisionItem[NUM_FRAMES * NUM_GEMS];

    public final Frame frame;
    public final Gem gem;

    public VisionItem(Frame frame, Gem gem) {
        super(new Item(new FabricItemSettings()),
            new Identifier(MOD_NAMESPACE, getName(frame, gem)), null,
            frame.displayName() + ' ' + gem.displayName() + " Vision"
        );
        this.frame = frame;
        this.gem = gem;
    }

    @Override
    public void registerModel(ItemModelGenerator itemModelGenerator) {
        final Identifier layer1 = new Identifier(MOD_NAMESPACE, this.frame.texture());
        final Identifier layer0 = new Identifier(MOD_NAMESPACE, this.gem.texture(this.frame.isHexagonal));
        final TextureMap textures = new TextureMap().put(TextureKey.LAYER0, layer0).put(TextureKey.LAYER1, layer1);
        Models.GENERATED_TWO_LAYERS.upload(this.identifier.withPrefixedPath("item/"), textures, itemModelGenerator.writer);
    }

    public static String getName(Frame frame, Gem gem) {
        return String.format("%s_%s_vision", frame.toString().toLowerCase(), gem.toString().toLowerCase());
    }
    public static boolean init() {
        final Frame[] frames = Frame.values();
        final Gem[] gems = Gem.values();
        int i = 0;

        for (Frame frame : frames) {
            for (Gem gem : gems) {
                items[i++] = new VisionItem(frame, gem);
            }
        }

        return true;
    }
    public static int index(Frame frame, Gem gem) {
        return frame.ordinal() * NUM_GEMS + gem.ordinal();
    }
    public static VisionItem getVisionItem(Frame frame, Gem gem) {
        return items[index(frame, gem)];
    }

    public enum Frame {
        MONDSTADT(),
        LIYUE(true),
        INAZUMA(),
        SUMERU(),
        FONTAINE_OUSIA(),
        FONTAINE_PNEUMA(),
        NATLAN(),
        SNEZHNAYA(),
        SOCKET1(),
        SOCKET2(true),
        OUTLANDER();

        public final boolean isHexagonal;

        Frame() {
            this(false);
        }
        Frame(boolean hexagonal) {
            this.isHexagonal = hexagonal;
        }

        public String displayName() {
            return switch (this) {
                case MONDSTADT -> "Mondstadt";
                case LIYUE -> "Liyue";
                case INAZUMA -> "Inazuma";
                case SUMERU -> "Sumeru";
                case FONTAINE_OUSIA -> "Fontaine Ousia";
                case FONTAINE_PNEUMA -> "Fontaine Pneuma";
                case NATLAN -> "Natlan";
                case SNEZHNAYA -> "Snezhnaya";
                case SOCKET1 -> "Generic Circle";
                case SOCKET2 -> "Generic Square";
                case OUTLANDER -> "Outlander";
            };
        }
        public String texture() {
            return String.format("item/vision/frame/%s", this.name().toLowerCase());
        }
    }
    public enum Gem {
        ANEMO,
        GEO,
        ELECTRO,
        DENDRO,
        HYDRO,
        PYRO,
        CRYO,
        MASTERLESS;

        public String displayName() {
            return switch (this) {
                case ANEMO -> "Anemo";
                case GEO -> "Geo";
                case ELECTRO -> "Electro";
                case DENDRO -> "Dendro";
                case HYDRO -> "Hydro";
                case PYRO -> "Pyro";
                case CRYO -> "Cryo";
                case MASTERLESS -> "Masterless";
            };
        }
        public String texture() {
            return texture(false);
        }
        public String texture(boolean isHexagonal) {
            return String.format("item/vision/gem/%s%s", this.name().toLowerCase(), (isHexagonal ? '2' : '1'));
        }
    }
}
