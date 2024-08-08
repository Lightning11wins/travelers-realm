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
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.BlockItem;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.lightning.trealm.TravelersRealm.MOD_NAMESPACE;

public class TravelersRealmDataGenerator implements DataGeneratorEntrypoint {
    public static final List<ItemRegistrable> ITEMS = new ArrayList<>();
    private static final boolean VISION_INIT = VisionItem.init();

    public static final ItemRegistrable HENSKULL = new ItemData.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(3)
        .saturationModifier(1.2f)
        .snack()
        .alwaysEdible()
        .statusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 200), 1)
        .build()
    )).identifier("henskull").texture("item/food/henskull").displayName("HenSkull").build();
    public static final ItemRegistrable DISH_FURINA = new ItemData.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(6)
        .statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 200), 1)
        .build()
    )).identifier("dish_furina").texture("item/food/dish_furina").displayName("Pour la Justice").build();
    public static final ItemRegistrable DISH_NAHIDA = new ItemData.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(6)
        .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200), 1)
        .build()
    )).identifier("dish_nahida").texture("item/food/dish_nahida").displayName("Halvamazd").build();
    public static final ItemRegistrable DISH_AYAKA = new ItemData.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(6)
        .statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1), 1)
        .build()
    )).identifier("dish_ayaka").texture("item/food/dish_ayaka").displayName("Snow on the Hearth").build();
    public static final ItemRegistrable DISH_WANDERER = new ItemData.Builder(new FabricItemSettings().food(new FoodComponent.Builder()
        .hunger(6)
        .saturationModifier(6)
        .statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 200), 1)
        .build()
    )).identifier("dish_wanderer").texture("item/food/dish_wanderer").displayName("Shimi Chazuke").build();

    public static final ItemRegistrable ALMOND = new ItemData.Builder().identifier("almond").texture("item/food/almond").displayName("Almonds").build();
    public static final ItemRegistrable CRAB = new ItemData.Builder().identifier("crab").texture("item/food/crab").displayName("Crab").build();
    public static final ItemRegistrable SHRIMP = new ItemData.Builder().identifier("shrimp").texture("item/food/shrimp").displayName("Shrimp").build();

    public static final Block ELEMENTAL_ORE = registerBlock("elemental_ore",
        new Block(Blocks.DEEPSLATE_DIAMOND_ORE.getSettings()));

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
	}


    public static class ModModelProvider extends FabricModelProvider {
        public ModModelProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
            blockStateModelGenerator.registerSimpleCubeAll(ELEMENTAL_ORE);
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
            addDrop(elementalOre, copperLikeOreDrops(elementalOre, HENSKULL.asItem()));
        }

        public LootTable.Builder copperLikeOreDrops(Block drop, Item item) {
            LeafEntry.Builder<?> standardDrop = ItemEntry.builder(item)
                .apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(1.0F, 3.0F)))
                .apply(ApplyBonusLootFunction.oreDrops(Enchantments.FORTUNE));
            return dropsWithSilkTouch(drop, this.applyExplosionDecay(drop, standardDrop));
        }
    }

    public static class ModRecipeProvider extends FabricRecipeProvider {
        public ModRecipeProvider(FabricDataOutput output) {
            super(output);
        }

        @Override
        public void generate(RecipeExporter exporter) {
            final Item blank_frame = VisionItem.Frame.SOCKET1.itemData.item;
            final RecipeCategory combat = RecipeCategory.COMBAT;
            for (VisionItem.Frame frame : VisionItem.Frame.values()) {
                final Item result = frame.itemData.item;
                if (blank_frame == result) continue;
                offerStonecuttingRecipe(exporter, combat, blank_frame, result);
                offerStonecuttingRecipe(exporter, combat, result, blank_frame);
            }

            ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, blank_frame, 1)
                .pattern("m m")
                .pattern("   ")
                .pattern("m m")
                .input('m', Items.GOLD_INGOT)
                .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
                .offerTo(exporter, new Identifier(MOD_NAMESPACE, "vision_frame"));

//            TODO - Lightning: Add smithing recipes in loop.
//            SmithingTransformRecipeJsonBuilder.create(
//                    Ingredient.ofItems(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.ofItems(input), Ingredient.ofItems(Items.NETHERITE_INGOT), category, result
//                )
//                .criterion("has_netherite_ingot", conditionsFromItem(Items.NETHERITE_INGOT))
//                .offerTo(exporter, getItemPath(result) + "_smithing");
        }
    }
}
