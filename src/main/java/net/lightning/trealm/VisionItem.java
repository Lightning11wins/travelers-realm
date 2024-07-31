package net.lightning.trealm;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.util.Identifier;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;

public class VisionItem extends ModItem implements RegistrableItem {
    public final Frame frame;
    public final Gem gem;

    public VisionItem(Settings settings) {
        this(settings, Frame.OUTLANDER, Gem.MASTERLESS);
    }
    public VisionItem(Settings settings, Frame frame, Gem gem) {
        super(settings, new RegistryData.Builder()
            .identifier(getName(frame, gem))
            .displayName(frame.displayName() + ' ' + gem.displayName() + " Vision")
            .build());
        this.frame = frame;
        this.gem = gem;
    }

    @Override
    public void registerModel(ItemModelGenerator itemModelGenerator) {
        final Identifier layer1 = new Identifier(MOD_NAMESPACE, this.frame.texture());
        final Identifier layer0 = new Identifier(MOD_NAMESPACE, this.gem.texture(this.frame.isHexagonal));
        final TextureMap textures = new TextureMap().put(TextureKey.LAYER0, layer0).put(TextureKey.LAYER1, layer1);
        Models.GENERATED_TWO_LAYERS.upload(ModelIds.getItemModelId(this), textures, itemModelGenerator.writer);
    }

    @Override
    public String toString() {
        return this.registryData.displayName();
    }

    public static String getName(Frame frame, Gem gem) {
        return String.format("%s_%s_vision", frame.toString().toLowerCase(), gem.toString().toLowerCase());
    }
    public static VisionItem[] constructAll() {
        final Frame[] frames = Frame.values();
        final Gem[] gems = Gem.values();
        final VisionItem[] items = new VisionItem[frames.length * gems.length];
        int i = 0;

        for (Frame frame : frames) {
            for (Gem gem : gems) {
                items[i++] = new VisionItem(new FabricItemSettings(), frame, gem);
            }
        }

        return items;
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
