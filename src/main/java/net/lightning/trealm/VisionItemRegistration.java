package net.lightning.trealm;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.lightning.trealm.Registration.ItemRegistration;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;

public class VisionItemRegistration extends ItemRegistration {
    public static final int NUM_FRAMES = Frame.values().length;
    public static final int NUM_GEMS = Gem.values().length;
    protected static final VisionItemRegistration[] VISION_ITEM_REGISTRATIONS = new VisionItemRegistration[NUM_FRAMES * NUM_GEMS];

    public final Frame frame;
    public final Gem gem;

    public VisionItemRegistration(Frame frame, Gem gem) {
        super(
            new Item(new FabricItemSettings()),
            new Identifier(MOD_NAMESPACE, getName(frame, gem)),
            null,
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
    public static VisionItemRegistration[] init() {
        final Frame[] frames = Frame.values();
        final Gem[] gems = Gem.values();
        int i = 0;

        for (final Frame frame : frames) {
            for (final Gem gem : gems) {
                VISION_ITEM_REGISTRATIONS[i++] = new VisionItemRegistration(frame, gem);
            }
        }

        return VISION_ITEM_REGISTRATIONS;
    }
    public static VisionItemRegistration getVisionItem(Frame frame, Gem gem) {
        return VISION_ITEM_REGISTRATIONS[frame.ordinal() * NUM_GEMS + gem.ordinal()];
    }

    public enum Frame implements ItemConvertible {
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
        public final ItemRegistration itemRegistration;

        Frame(String displayName) {
            this(displayName, false);
        }
        Frame(String displayName, boolean hexagonal) {
            this.displayName = displayName;
            this.isHexagonal = hexagonal;
            this.itemRegistration = new ItemRegistration.Builder()
                .identifier(this.toString().toLowerCase() + "_frame")
                .texture(this.texture())
                .displayName(this.displayName + " Vision Frame")
                .build();
        }

        public String texture() {
            return "item/vision/frame/" + this.name().toLowerCase();
        }

        @Override
        public Item asItem() {
            return this.itemRegistration.asItem();
        }
    }
    public enum Gem implements ItemConvertible {
        ANEMO("Anemo"),
        GEO("Geo"),
        ELECTRO("Electro"),
        DENDRO("Dendro"),
        HYDRO("Hydro"),
        PYRO("Pyro"),
        CRYO("Cryo"),
        MASTERLESS("Masterless");

        public final String displayName;
        public final ItemRegistration itemRegistration;

        Gem(String displayName) {
            this.displayName = displayName;
            this.itemRegistration = new Registration.ItemRegistration.Builder()
                .identifier(this.toString().toLowerCase() + "_gem")
                .texture(this.texture())
                .displayName(this.displayName + " Vision Gem")
                .build();
        }

        public String texture() {
            return this.texture(false);
        }
        public String texture(boolean isHexagonal) {
            return "item/vision/gem/" + this.name().toLowerCase() + (isHexagonal ? '2' : '1');
        }

        @Override
        public Item asItem() {
            return this.itemRegistration.asItem();
        }
    }
}
