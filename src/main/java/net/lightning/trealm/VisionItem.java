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
    public static final VisionItem[] ITEMS = new VisionItem[NUM_FRAMES * NUM_GEMS];

    public final Frame frame;
    public final Gem gem;

    public VisionItem(Frame frame, Gem gem) {
        super(new Item(new FabricItemSettings()),
            new Identifier(MOD_NAMESPACE, getName(frame, gem)), null,
            frame.displayName + ' ' + gem.displayName + " Vision"
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
                ITEMS[i++] = new VisionItem(frame, gem);
            }
        }

        return true;
    }
    public static int index(Frame frame, Gem gem) {
        return frame.ordinal() * NUM_GEMS + gem.ordinal();
    }
    public static VisionItem getVisionItem(Frame frame, Gem gem) {
        return ITEMS[index(frame, gem)];
    }

    public enum Frame {
        MONDSTADT("Mondstadt"),
        LIYUE("Liyue", true),
        INAZUMA("Inazuma"),
        SUMERU("Sumeru"),
        FONTAINE_OUSIA("Fontaine Ousia"),
        FONTAINE_PNEUMA("Fontaine Pneuma"),
        NATLAN("Natlan"),
        SNEZHNAYA("Snezhnaya"),
        SOCKET1("Generic Circle"),
        SOCKET2("Generic Square", true),
        OUTLANDER("Outlander");

        public final String displayName;
        public final boolean isHexagonal;
        public final ItemData itemData;

        Frame(String displayName) {
            this(displayName, false);
        }
        Frame(String displayName, boolean hexagonal) {
            this.displayName = displayName;
            this.isHexagonal = hexagonal;
            this.itemData = new ItemData.Builder()
                .identifier(this.toString().toLowerCase() + "_frame")
                .texture(this.texture())
                .displayName(this.displayName + " Vision Frame")
                .build();
        }

        public String texture() {
            return String.format("item/vision/frame/%s", this.name().toLowerCase());
        }
    }
    public enum Gem {
        ANEMO("Anemo"),
        GEO("Geo"),
        ELECTRO("Electro"),
        DENDRO("Dendro"),
        HYDRO("Hydro"),
        PYRO("Pyro"),
        CRYO("Cryo"),
        MASTERLESS("Masterless");

        public final String displayName;
        public final ItemData itemData;

        Gem(String displayName) {
            this.displayName = displayName;
            this.itemData = new ItemData.Builder()
                .identifier(this.toString().toLowerCase() + "_gem")
                .texture(this.texture())
                .displayName(this.displayName + " Vision Gem")
                .build();
        }

        public String texture() {
            return texture(false);
        }
        public String texture(boolean isHexagonal) {
            return String.format("item/vision/gem/%s%s", this.name().toLowerCase(), (isHexagonal ? '2' : '1'));
        }
    }
}
