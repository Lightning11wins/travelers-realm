package net.lightning.trealm;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;
import static net.lightning.trealm.RegistrableItem.RegistryData;

public class TravelersRealmDataGenerator implements DataGeneratorEntrypoint {
    public static final List<RegistrableItem> ITEMS = new ArrayList<>();

    public static final ModItem HENSKULL = new ModItem(new FabricItemSettings().food(new FoodComponent.Builder()
            .hunger(3)
            .saturationModifier(1.2f)
            .snack()
            .alwaysEdible()
            .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200), 1)
            .build()
    ), new RegistryData.Builder().identifier("henskull").texture("item/henskull").displayName("HenSkull").build());
    public static final ModItem FRAME1 = new ModItem(new RegistryData.Builder()
            .identifier("frame1")
            .texture("item/vision/frame/socket1")
            .displayName("Hexagonal Frame")
            .build());
    public static final ModItem FRAME2 = new ModItem(new RegistryData.Builder()
            .identifier("frame2")
            .texture("item/vision/frame/socket2")
            .displayName("Circular Frame")
            .build());
    public static final VisionItem[] VISION_ITEMS = VisionItem.constructAll();

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModLangProvider::new);
        pack.addProvider(ModRecipeProvider::new);
	}

    public static class ModLangProvider extends FabricLanguageProvider {
        public ModLangProvider(FabricDataOutput data) {
            super(data);
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            for (RegistrableItem item : TravelersRealmDataGenerator.ITEMS) {
                item.registerDisplayName(translationBuilder);
            }
        }
    }

    public static class ModModelProvider extends FabricModelProvider {
        public ModModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            for (RegistrableItem item : TravelersRealmDataGenerator.ITEMS) {
                item.registerModel(itemModelGenerator);
            }
        }
    }

    public static class ModRecipeProvider extends FabricRecipeProvider {
        public ModRecipeProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate(RecipeExporter exporter) {
            ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TravelersRealmDataGenerator.FRAME1, 1)
                .pattern("m m")
                .pattern("   ")
                .pattern("m m")
                .input('m', Items.GOLD_INGOT)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter, new Identifier(MOD_NAMESPACE, "circular_vision_frame"));

            ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, TravelersRealmDataGenerator.FRAME2, 1)
                .pattern(" m ")
                .pattern("m m")
                .pattern(" m ")
                .input('m', Items.GOLD_INGOT)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter, new Identifier(MOD_NAMESPACE, "hexagonal_vision_frame"));

            ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, TravelersRealmDataGenerator.FRAME1, 1)
                    .input(TravelersRealmDataGenerator.FRAME2)
                    .criterion(hasItem(TravelersRealmDataGenerator.FRAME2), conditionsFromItem(TravelersRealmDataGenerator.FRAME2))
                    .offerTo(exporter, new Identifier(MOD_NAMESPACE, "blunt_vision_frame"));

            ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, TravelersRealmDataGenerator.FRAME2, 1)
                    .input(TravelersRealmDataGenerator.FRAME1)
                    .criterion(hasItem(TravelersRealmDataGenerator.FRAME1), conditionsFromItem(TravelersRealmDataGenerator.FRAME1))
                    .offerTo(exporter, new Identifier(MOD_NAMESPACE, "sharpen_vision_frame"));
        }
    }
}
