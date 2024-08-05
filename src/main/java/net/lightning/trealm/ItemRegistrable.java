package net.lightning.trealm;

import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.item.ItemConvertible;

public interface ItemRegistrable extends ItemConvertible {
    void registerItem();
    void registerModel(ItemModelGenerator itemModelGenerator);
    void registerDisplayName(FabricLanguageProvider.TranslationBuilder translationBuilder);
}
