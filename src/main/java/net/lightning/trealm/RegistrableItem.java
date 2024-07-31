package net.lightning.trealm;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.ItemConvertible;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;

public interface RegistrableItem extends ItemConvertible {
    RegistryData getRegistrationData();

    default void registerItem() {
        Registry.register(Registries.ITEM, this.getRegistrationData().identifier, this.asItem());
    }
    default void registerModel(ItemModelGenerator itemModelGenerator) {
        final Identifier texture = this.getRegistrationData().texture;
        if (texture != null) {
            final TextureMap textureMap = new TextureMap().put(TextureKey.LAYER0, texture);
            Models.GENERATED.upload(ModelIds.getItemModelId(this.asItem()), textureMap, itemModelGenerator.writer);
        }
    }
    default void registerDisplayName(FabricLanguageProvider.TranslationBuilder translationBuilder) {
        final RegistryData registryData = this.getRegistrationData();
        final String displayName = registryData.displayName;
        if (displayName != null) {
            translationBuilder.add("item." + registryData.identifier.toTranslationKey(), displayName);
        }
    }

    record RegistryData(Identifier identifier, Identifier texture, String displayName) {
        public static class Builder {
            protected Identifier identifier, texture;
            protected String displayName;

            public RegistryData.Builder identifier(String texture) {
                return this.identifier(new Identifier(MOD_NAMESPACE, texture));
            }

            public RegistryData.Builder identifier(Identifier identifier) {
                this.identifier = identifier;
                return this;
            }

            public RegistryData.Builder texture(String texture) {
                return this.texture(new Identifier(MOD_NAMESPACE, texture));
            }

            public RegistryData.Builder texture(Identifier texture) {
                this.texture = texture;
                return this;
            }

            public RegistryData.Builder displayName(String displayName) {
                this.displayName = displayName;
                return this;
            }

            public RegistryData build() {
                return new RegistryData(this.identifier, this.texture, this.displayName);
            }
        }
    }
}
