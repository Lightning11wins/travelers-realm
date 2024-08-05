package net.lightning.trealm;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;

public class ItemData implements ItemRegistrable {
    protected Item item;
    protected Identifier identifier, texture;
    protected String displayName;

    public ItemData(Item item, Identifier identifier, Identifier texture, String displayName) {
        this.item = item;
        this.identifier = identifier;
        this.texture = texture;
        this.displayName = displayName;
        TravelersRealmDataGenerator.ITEMS.add(this);
    }

    public void registerItem() {
        Registry.register(Registries.ITEM, this.identifier, this.asItem());
    }
    public void registerModel(ItemModelGenerator itemModelGenerator) {
        final Identifier texture = this.texture;
        if (texture != null) {
            final TextureMap textureMap = new TextureMap().put(TextureKey.LAYER0, texture);
            Models.GENERATED.upload(ModelIds.getItemModelId(this.asItem()), textureMap, itemModelGenerator.writer);
        }
    }
    public void registerDisplayName(FabricLanguageProvider.TranslationBuilder translationBuilder) {
        final String displayName = this.displayName;
        if (displayName != null) {
            translationBuilder.add("item." + this.identifier.toTranslationKey(), displayName);
        }
    }

    @Override
    public Item asItem() {
        return this.item;
    }

    public static class Builder {
        protected Item item;
        protected Identifier identifier, texture;
        protected String displayName;

        public Builder() {
            this(new FabricItemSettings());
        }
        public Builder(Item.Settings settings) {
            this(new Item(settings));
        }
        public Builder(Item item) {
            this.item = item;
        }

        public Builder identifier(String texture) {
            return this.identifier(new Identifier(MOD_NAMESPACE, texture));
        }
        public Builder identifier(Identifier identifier) {
            this.identifier = identifier;
            return this;
        }
        public Builder texture(String texture) {
            return this.texture(new Identifier(MOD_NAMESPACE, texture));
        }
        public Builder texture(Identifier texture) {
            this.texture = texture;
            return this;
        }
        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public ItemData build() {
            return new ItemData(this.item, this.identifier, this.texture, this.displayName);
        }
    }
}
