package net.lightning.trealm;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.lightning.trealm.AdeptusStoveBlock.AdeptalStoveScreenHandler;
import net.lightning.trealm.AdeptusStoveBlock.AdeptusStoveBlock;
import net.lightning.trealm.AdeptusStoveBlock.AdeptusStoveBlockEntity;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;

public class TravelersRealmDataGenerator implements DataGeneratorEntrypoint {
    public static final List<ItemRegistrable> ITEMS = new ArrayList<>();
    private static final boolean VISION_INIT = VisionItem.init();

    public static final ItemData HENSKULL = new ItemData.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(3)
        .saturationModifier(1.2f)
        .snack()
        .alwaysEdible()
        .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200), 1)
        .build()
    )).identifier("henskull").texture("item/food/henskull").displayName("HenSkull").build();
    public static final ItemData DISH_FURINA = new ItemData.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(6)
        .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 200), 1)
        .build()
    )).identifier("dish_furina").texture("item/food/dish_furina").displayName("Pour la Justice").build();
    public static final ItemData DISH_NAHIDA = new ItemData.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(6)
        .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200), 1)
        .build()
    )).identifier("dish_nahida").texture("item/food/dish_nahida").displayName("Halvamazd").build();
    public static final ItemData DISH_AYAKA = new ItemData.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(6)
        .statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1), 1)
        .build()
    )).identifier("dish_ayaka").texture("item/food/dish_ayaka").displayName("Snow on the Hearth").build();
    public static final ItemData DISH_WANDERER = new ItemData.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(6)
        .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200), 1)
        .build()
    )).identifier("dish_wanderer").texture("item/food/dish_wanderer").displayName("Shimi Chazuke").build();

    public static final ItemData ALMOND = new ItemData.Builder().identifier("almond").texture("item/food/almond").displayName("Almonds").build();
    public static final ItemData CRAB = new ItemData.Builder().identifier("crab").texture("item/food/crab").displayName("Crab").build();
    public static final ItemData SHRIMP = new ItemData.Builder().identifier("shrimp").texture("item/food/shrimp").displayName("Shrimp").build();
    public static final ItemData FLOUR = new ItemData.Builder().identifier("flour").texture("item/food/flour").displayName("Flour").build();

    public static final ItemData ELEMENTAL_INGOT = new ItemData.Builder().identifier("elemental_ingot").texture("item/elemental_ingot").displayName("Elemental Ingot").build();
    public static final ItemData ELEMENTAL_NUGGET = new ItemData.Builder().identifier("elemental_nugget").texture("item/elemental_nugget").displayName("Elemental Nugget").build();
    public static final ItemData ELEMENTAL_ORE_BLOCK = new ItemData.Builder().identifier("elemental_ore_block").texture("item/elemental_block").displayName("Block of Elemental Ingots").build();
    public static final ItemData RAW_ELEMENTAL_ORE = new ItemData.Builder().identifier("raw_elemental_ore").texture("item/raw_elemental_ore").displayName("Raw Elemental Ore").build();
    public static final ItemData RAW_ELEMENTAL_ORE_BLOCK = new ItemData.Builder().identifier("raw_elemental_ore_block").texture("item/raw_elemental_ore_block").displayName("Raw Elemental Ore Block").build();

    public static final Block ELEMENTAL_ORE = registerBlock("elemental_ore", new Block(Blocks.DEEPSLATE_DIAMOND_ORE.getSettings()));

    public static final Block ADEPTUS_STOVE_BLOCK = registerBlock("adeptus_stove", new AdeptusStoveBlock(Blocks.CAULDRON.getSettings()));


    public static final BlockEntityType<AdeptusStoveBlockEntity> ADEPTUS_STOVE_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(MOD_NAMESPACE, "adeptal_cooking_be"),
                    FabricBlockEntityTypeBuilder.create(AdeptusStoveBlockEntity::new,
                            ADEPTUS_STOVE_BLOCK).build());

    public static final ScreenHandlerType<AdeptalStoveScreenHandler> ADEPTAL_STOVE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(MOD_NAMESPACE, "adeptal_cooking"),
                    new ExtendedScreenHandlerType<>(AdeptalStoveScreenHandler::new));

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registries.ITEM, new Identifier(MOD_NAMESPACE, name),
            new BlockItem(block, new FabricItemSettings()));
        return Registry.register(Registries.BLOCK, new Identifier(MOD_NAMESPACE, name), block);
    }

    @Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModBlockTagProvider::new);
        pack.addProvider(ModLangProvider::new);
        pack.addProvider(ModLootTableProvider::new);
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModWorldGenerator::new);
	}

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ModWorldGenerator.ModConfiguredFeatures::boostrap);
        registryBuilder.addRegistry(RegistryKeys.PLACED_FEATURE, ModWorldGenerator.ModPlacedFeatures::boostrap);
    }


    public static class ModModelProvider extends FabricModelProvider {
        public ModModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            blockStateModelGenerator.registerSimpleCubeAll(ELEMENTAL_ORE);
            blockStateModelGenerator.registerSimpleState(ADEPTUS_STOVE_BLOCK);
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            for (ItemRegistrable item : TravelersRealmDataGenerator.ITEMS) {
                item.registerModel(itemModelGenerator);
            }
        }
    }

    public class ModBlockTagProvider extends FabricTagProvider.BlockTagProvider {
        public ModBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup arg) {
            final Block elementalOre = ELEMENTAL_ORE;
            getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(elementalOre);
            getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(elementalOre);
            final Block adeptusStove = ADEPTUS_STOVE_BLOCK;
            getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(adeptusStove);
            getOrCreateTagBuilder(BlockTags.NEEDS_STONE_TOOL).add(adeptusStove);
        }
    }

    public static class ModLangProvider extends FabricLanguageProvider {
        public ModLangProvider(FabricDataOutput data) {
            super(data);
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            for (ItemRegistrable item : TravelersRealmDataGenerator.ITEMS) {
                item.registerDisplayName(translationBuilder);
            }
        }
    }

    public class ModLootTableProvider extends FabricBlockLootTableProvider {
        protected ModLootTableProvider(FabricDataOutput dataOutput) {
            super(dataOutput);
        }

        @Override
        public void generate() {
            final Block elementalOre = ELEMENTAL_ORE;
            addDrop(elementalOre, copperLikeOreDrops(elementalOre, RAW_ELEMENTAL_ORE.asItem()));
            final Block adeptusStove = ADEPTUS_STOVE_BLOCK;
            addDrop(adeptusStove, drops(adeptusStove, ADEPTUS_STOVE_BLOCK.asItem()));
        }

        public LootTable.Builder copperLikeOreDrops(Block drop, Item item) {
            LeafEntry.Builder<?> standardDrop = ItemEntry.builder(item)
                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 1.0F)))
                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE));
            return dropsWithSilkTouch(drop, this.applyExplosionDecay(drop, standardDrop));
        }
    }

    public static class ModRecipeProvider extends FabricRecipeProvider {
        public ModRecipeProvider(FabricDataOutput output) {
            super(output);
        }

        public void offerReversibleCompactingRecipes(
            RecipeExporter exporter,
            RecipeCategory category,
            ItemData baseItem,
            ItemData compactItem
        ) {
            final String baseItemDisplayName = baseItem.identifier.getPath(), compactItemDisplayName = compactItem.identifier.getPath();
            final String compactRecipe = String.format("compact_%s_to_%s", baseItemDisplayName, compactItemDisplayName);
            final String uncompactRecipe = String.format("uncompact_%s_to_%s", compactItemDisplayName, baseItemDisplayName);
            offerReversibleCompactingRecipes(
                exporter,
                category,
                baseItem,
                category,
                compactItem,
                compactRecipe,
                null,
                uncompactRecipe,
                null
            );
        }

        public void offerBlastingRecipe(
            RecipeExporter exporter,
            ItemConvertible input,
            RecipeCategory category,
            ItemConvertible output,
            String name,
            float experience,
            int blastTime,
            int smeltTime
        ) {
            final String hasName = "has_" + name;
            final Ingredient ingredient = Ingredient.ofItems(input);
            final AdvancementCriterion<InventoryChangedCriterion.Conditions> unlockCriterion = conditionsFromItem(input);
            CookingRecipeJsonBuilder.createBlasting(ingredient, category, output, experience, blastTime)
                .criterion(hasName, unlockCriterion)
                .offerTo(exporter, new Identifier(MOD_NAMESPACE, name + "_blasting"));
            CookingRecipeJsonBuilder.createSmelting(ingredient, category, output, experience, smeltTime)
                .criterion(hasName, unlockCriterion)
                .offerTo(exporter, new Identifier(MOD_NAMESPACE, name + "_smelting"));
        }

        @Override
        public void generate(RecipeExporter exporter) {
            final Item blank_frame = VisionItem.Frame.SOCKET1.itemData.item;
            final RecipeCategory combat = RecipeCategory.COMBAT, misc = RecipeCategory.MISC;
            for (VisionItem.Frame frame : VisionItem.Frame.values()) {
                final Item result = frame.itemData.item;
                if (blank_frame == result) continue;
                offerStonecuttingRecipe(exporter, combat, blank_frame, result);
                offerStonecuttingRecipe(exporter, combat, result, blank_frame);
            }

            ShapedRecipeJsonBuilder.create(combat, blank_frame, 1)
                    .pattern("iii")
                    .pattern("i i")
                    .pattern("iii")
                    .input('i', ELEMENTAL_INGOT)
                    .criterion(hasItem(ELEMENTAL_INGOT), conditionsFromItem(ELEMENTAL_INGOT))
                    .offerTo(exporter, new Identifier(MOD_NAMESPACE, "vision_frame"));

            offerReversibleCompactingRecipes(exporter, misc, ELEMENTAL_NUGGET, ELEMENTAL_INGOT);
            offerReversibleCompactingRecipes(exporter, misc, ELEMENTAL_INGOT, ELEMENTAL_ORE_BLOCK);
            offerReversibleCompactingRecipes(exporter, misc, RAW_ELEMENTAL_ORE, RAW_ELEMENTAL_ORE_BLOCK);

            offerBlastingRecipe(exporter, ELEMENTAL_ORE, misc, RAW_ELEMENTAL_ORE, "elemental_ore", 1.0F, 100, 200);
            offerBlastingRecipe(exporter, RAW_ELEMENTAL_ORE, misc, ELEMENTAL_INGOT, "elemental_ingot", 2.0F, 100, 200);

            for (VisionItem vision : VisionItem.ITEMS) {
                final ItemData frame = vision.frame.itemData, gem = vision.gem.itemData;
                SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(),
                        Ingredient.ofItems(frame),
                        Ingredient.ofItems(gem),
                        combat,
                        vision.asItem()
                    )
                    .criterion("has_" + frame.identifier.getPath(), conditionsFromItem(frame))
                    .criterion("has_" + gem.identifier.getPath(), conditionsFromItem(gem))
                    .offerTo(exporter, getItemPath(vision) + "_embedding");
            }
        }
    }
}
