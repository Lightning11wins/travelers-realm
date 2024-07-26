package net.lightning.genshinsmp;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class ModLangProvider extends FabricLanguageProvider {
    public ModLangProvider(FabricDataOutput data) {
        super(data);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        ModItem.registerAllDisplayNames(translationBuilder);
    }
}
