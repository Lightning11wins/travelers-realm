package net.lightning.genshinsmp;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider.TranslationBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.ToggleableFeature;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static net.lightning.genshinsmp.GenshinSMP.MOD_ID;

public class ModItem extends Item implements ToggleableFeature, ItemConvertible, FabricItem {
    public static final List<ModItem> ITEMS = new ArrayList<>();

    Identifier name, texture;
    String displayName;

    public ModItem() {
        this(new FabricItemSettings());
    }
    public ModItem(Item.Settings settings) {
        super(settings);
        ITEMS.add(this);
    }

    public ModItem setName(String name) {
        return this.setName(new Identifier(MOD_ID, name));
    }
    public ModItem setName(Identifier name) {
        Registry.register(Registries.ITEM, name, this);
        this.name = name;
        return this;
    }
    public ModItem setTexture(String texture) {
        return this.setTexture(new Identifier(MOD_ID, texture));
    }
    public ModItem setTexture(Identifier texture) {
        this.texture = texture;
        return this;
    }
    public ModItem setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public void registerModel(ItemModelGenerator itemModelGenerator) {
        final Identifier texture = this.texture;
        if (texture != null) {
            final TextureMap textureMap = new TextureMap().put(TextureKey.LAYER0, texture);
            Models.GENERATED.upload(ModelIds.getItemModelId(this), textureMap, itemModelGenerator.writer);
        }
    }
    public void registerDisplayName(TranslationBuilder translationBuilder) {
        final String displayName = this.displayName;
        if (displayName != null) {
            translationBuilder.add("item." + this.name.toTranslationKey(), this.displayName);
        }
    }

    public static void registerAllModels(ItemModelGenerator itemModelGenerator) {
        for (ModItem item : ModItem.ITEMS) {
            item.registerModel(itemModelGenerator);
        }
    }
    public static void registerAllDisplayNames(TranslationBuilder translationBuilder) {
        for (ModItem item : ModItem.ITEMS) {
            item.registerDisplayName(translationBuilder);
        }
    }
    public static void initializeItems() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register((e) -> {
            for (ModItem item : ModItem.ITEMS) {
                e.add(item);
            }
        });
    }
}
