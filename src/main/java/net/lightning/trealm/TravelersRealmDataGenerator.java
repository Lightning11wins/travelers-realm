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
import net.lightning.trealm.Registration.BlockRegistration;
import net.lightning.trealm.Registration.ItemRegistration;
import net.lightning.trealm.Registration.Registrable;
import net.lightning.trealm.Registration.RegistrableItem;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.server.recipe.CookingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.SmithingTransformRecipeJsonBuilder;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
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
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;

public class TravelersRealmDataGenerator implements DataGeneratorEntrypoint {
    protected static final List<ItemRegistration>
        ITEMS = new ArrayList<>(16),
        FOOD_ITEMS = new ArrayList<>(8),
        INGREDIENT_ITEMS = new ArrayList<>(8),
        VISION_ITEMS = List.of(VisionItemRegistration.init());
    protected static final List<BlockRegistration> BLOCKS = new ArrayList<>();

    public static final ItemRegistration HENSKULL = new ItemRegistration.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(3)
        .saturationModifier(0.4f)
        .snack()
        .alwaysEdible()
        .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200), 1)
        .build()
    )).identifier("henskull").texture("item/food/henskull").displayName("HenSkull").build().addTo(FOOD_ITEMS);
    public static final ItemRegistration DISH_FURINA = new ItemRegistration.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(1f)
        .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 200), 1)
        .build()
    )).identifier("dish_furina").texture("item/food/dish_furina").displayName("Pour la Justice").build().addTo(FOOD_ITEMS);
    public static final ItemRegistration DISH_NAHIDA = new ItemRegistration.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(1f)
        .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200), 1)
        .build()
    )).identifier("dish_nahida").texture("item/food/dish_nahida").displayName("Halvamazd").build().addTo(FOOD_ITEMS);
    public static final ItemRegistration DISH_AYAKA = new ItemRegistration.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(1f)
        .statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1), 1)
        .build()
    )).identifier("dish_ayaka").texture("item/food/dish_ayaka").displayName("Snow on the Hearth").build().addTo(FOOD_ITEMS);
    public static final ItemRegistration DISH_WANDERER = new ItemRegistration.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(1f)
        .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200), 1)
        .build()
    )).identifier("dish_wanderer").texture("item/food/dish_wanderer").displayName("Shimi Chazuke").build().addTo(FOOD_ITEMS);

    public static final ItemRegistration ALMOND = new ItemRegistration.Builder().identifier("almond").texture("item/food/almond").displayName("Almonds").build().addTo(INGREDIENT_ITEMS);
    public static final ItemRegistration CRAB = new ItemRegistration.Builder().identifier("crab").texture("item/food/crab").displayName("Crab").build().addTo(INGREDIENT_ITEMS);
    public static final ItemRegistration SHRIMP = new ItemRegistration.Builder().identifier("shrimp").texture("item/food/shrimp").displayName("Shrimp").build().addTo(INGREDIENT_ITEMS);

    public static final ItemRegistration ELEMENTAL_INGOT = new ItemRegistration.Builder().identifier("elemental_ingot").texture("item/elemental_ingot").displayName("Elemental Ingot").build().addTo(INGREDIENT_ITEMS);
    public static final ItemRegistration ELEMENTAL_NUGGET = new ItemRegistration.Builder().identifier("elemental_nugget").texture("item/elemental_nugget").displayName("Elemental Nugget").build().addTo(INGREDIENT_ITEMS);
    public static final ItemRegistration RAW_ELEMENTAL_ORE = new ItemRegistration.Builder().identifier("raw_elemental_ore").texture("item/raw_elemental_ore").displayName("Raw Elemental Ore").build().addTo(INGREDIENT_ITEMS);

    public static final BlockRegistration DEEPSLATE_ELEMENTAL_ORE = new BlockRegistration.Builder(Blocks.DEEPSLATE_DIAMOND_ORE).identifier("deepslate_elemental_ore").texture("block/elemental_ore").displayName("Elemental Ore").build();
    public static final BlockRegistration ELEMENTAL_BLOCK = new BlockRegistration.Builder(Blocks.DIAMOND_BLOCK).identifier("elemental_block").texture("block/elemental_block").displayName("Elemental Block").build();
    public static final BlockRegistration RAW_ELEMENTAL_BLOCK = new BlockRegistration.Builder(Blocks.RAW_IRON_BLOCK).identifier("raw_elemental_block").texture("block/raw_elemental_block").displayName("Block of Raw Elemental Ore").build();

    @Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		final FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
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
            for (final BlockRegistration block : TravelersRealmDataGenerator.BLOCKS) {
                block.registerModel(blockStateModelGenerator);
            }
        }

        @Override
        public void generateItemModels(ItemModelGenerator itemModelGenerator) {
            for (final RegistrableItem item : TravelersRealmDataGenerator.ITEMS) {
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
            final Block elementalOre = DEEPSLATE_ELEMENTAL_ORE.block;
            this.getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(elementalOre);
            this.getOrCreateTagBuilder(BlockTags.NEEDS_DIAMOND_TOOL).add(elementalOre);
        }
    }

    public static class ModLangProvider extends FabricLanguageProvider {
        public ModLangProvider(FabricDataOutput data) {
            super(data);
        }

        @Override
        public void generateTranslations(TranslationBuilder translationBuilder) {
            for (final Registrable block : TravelersRealmDataGenerator.BLOCKS) {
                block.registerDisplayName(translationBuilder);
            }
            for (final Registrable item : TravelersRealmDataGenerator.ITEMS) {
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
            final Block elementalOre = DEEPSLATE_ELEMENTAL_ORE.block;
            this.addDrop(elementalOre, this.copperLikeOreDrops(elementalOre, RAW_ELEMENTAL_ORE.asItem()));
        }

        public LootTable.Builder copperLikeOreDrops(Block drop, Item item) {
            final LeafEntry.Builder<?> standardDrop = ItemEntry.builder(item)
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
            Registration baseItem,
            Registration compactItem
        ) {
            final String baseItemName = baseItem.identifier.getPath(), compactItemName = compactItem.identifier.getPath();
            final String compactRecipe = String.format("compact_%s_to_%s", baseItemName, compactItemName);
            final String uncompactRecipe = String.format("uncompact_%s_to_%s", compactItemName, baseItemName);
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

        final static int FURNACE_ONLY = 1;
        final static int BLAST_FURNACE_ONLY = 2;
        final static int SMOKER_ONLY = 4;
        final static int FURNACE = FURNACE_ONLY;
        final static int BLAST_FURNACE = BLAST_FURNACE_ONLY | FURNACE_ONLY;
        final static int SMOKER = SMOKER_ONLY | FURNACE_ONLY;

        /**
         * @param time measured in ticks
         * @param furnaceFlags use one of the flags listed above this comment.
         */
        public void offerCookingRecipes(
            RecipeExporter exporter,
            ItemConvertible input,
            RecipeCategory category,
            ItemConvertible output,
            float experience,
            int time,
            int furnaceFlags
        ) {
            final String name = getItemPath(input),
                criterion1Name = hasItem(input),
                criterion2Name = hasItem(output);
            final AdvancementCriterion<InventoryChangedCriterion.Conditions>
                criterion1Trigger = conditionsFromItem(input),
                criterion2Trigger = conditionsFromItem(output);
            final Ingredient ingredient = Ingredient.ofItems(input);
            if ((furnaceFlags & FURNACE_ONLY) != 0) {
                CookingRecipeJsonBuilder.createSmelting(ingredient, category, output, experience, time)
                    .criterion(criterion1Name, criterion1Trigger)
                    .criterion(criterion2Name, criterion2Trigger)
                    .offerTo(exporter, new Identifier(MOD_NAMESPACE, name + "_smelting"));
            }
            if ((furnaceFlags & BLAST_FURNACE_ONLY) != 0) {
                CookingRecipeJsonBuilder.createBlasting(ingredient, category, output, experience, time / 2)
                    .criterion(criterion1Name, criterion1Trigger)
                    .criterion(criterion2Name, criterion2Trigger)
                    .offerTo(exporter, new Identifier(MOD_NAMESPACE, name + "_blasting"));
            }
            if ((furnaceFlags & SMOKER_ONLY) != 0) {
                CookingRecipeJsonBuilder.createSmoking(ingredient, category, output, experience, time / 2)
                    .criterion(criterion1Name, criterion1Trigger)
                    .criterion(criterion2Name, criterion2Trigger)
                    .offerTo(exporter, new Identifier(MOD_NAMESPACE, name + "_smoking"));
            }
        }

        @Override
        public void generate(RecipeExporter exporter) {
            final Item blankFrame = VisionItemRegistration.Frame.SOCKET1.itemRegistration.item;
            final RecipeCategory combat = RecipeCategory.COMBAT, misc = RecipeCategory.MISC;
            for (final VisionItemRegistration.Frame frame : VisionItemRegistration.Frame.values()) {
                final Item result = frame.itemRegistration.item;
                if (blankFrame == result) continue;
                offerStonecuttingRecipe(exporter, combat, blankFrame, result);
                offerStonecuttingRecipe(exporter, combat, result, blankFrame);
            }

            ShapedRecipeJsonBuilder.create(combat, blankFrame, 1)
                .pattern("iii")
                .pattern("i i")
                .pattern("iii")
                .input('i', ELEMENTAL_INGOT)
                .criterion(hasItem(ELEMENTAL_INGOT), conditionsFromItem(ELEMENTAL_INGOT))
                .offerTo(exporter, new Identifier(MOD_NAMESPACE, "vision_frame"));

            this.offerReversibleCompactingRecipes(exporter, misc, ELEMENTAL_NUGGET, ELEMENTAL_INGOT);
            this.offerReversibleCompactingRecipes(exporter, misc, ELEMENTAL_INGOT, ELEMENTAL_BLOCK);
            this.offerReversibleCompactingRecipes(exporter, misc, RAW_ELEMENTAL_ORE, RAW_ELEMENTAL_BLOCK);

            this.offerCookingRecipes(exporter, DEEPSLATE_ELEMENTAL_ORE, misc, RAW_ELEMENTAL_ORE, 1.0F, 200, BLAST_FURNACE);
            this.offerCookingRecipes(exporter, RAW_ELEMENTAL_ORE, misc, ELEMENTAL_INGOT, 2.0F, 200, BLAST_FURNACE);
            this.offerCookingRecipes(exporter, RAW_ELEMENTAL_BLOCK, misc, ELEMENTAL_BLOCK, 18.0F, 1600, BLAST_FURNACE);

            for (final VisionItemRegistration vision : VisionItemRegistration.VISION_ITEM_REGISTRATIONS) {
                final ItemRegistration frame = vision.frame.itemRegistration, gem = vision.gem.itemRegistration;
                SmithingTransformRecipeJsonBuilder.create(
                        Ingredient.ofItems(),
                        Ingredient.ofItems(frame),
                        Ingredient.ofItems(gem),
                        combat,
                        vision.asItem()
                    )
                    .criterion(hasItem(frame), conditionsFromItem(frame))
                    .criterion(hasItem(gem), conditionsFromItem(gem))
                    .criterion(hasItem(vision), conditionsFromItem(vision))
                    .offerTo(exporter, getItemPath(vision) + "_embedding");
            }
        }
    }
}
