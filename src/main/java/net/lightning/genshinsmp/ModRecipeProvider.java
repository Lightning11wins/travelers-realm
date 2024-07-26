package net.lightning.genshinsmp;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import static net.lightning.genshinsmp.GenshinSMP.MOD_ID;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, GenshinSMP.FRAME1, 1)
            .pattern("m m")
            .pattern("   ")
            .pattern("m m")
            .input('m', Items.GOLD_INGOT)
            .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
            .offerTo(exporter, new Identifier(MOD_ID, "circular_vision_frame"));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, GenshinSMP.FRAME2, 1)
            .pattern(" m ")
            .pattern("m m")
            .pattern(" m ")
            .input('m', Items.GOLD_INGOT)
            .criterion(hasItem(Items.GOLD_INGOT), conditionsFromItem(Items.GOLD_INGOT))
            .offerTo(exporter, new Identifier(MOD_ID, "hexagonal_vision_frame"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, GenshinSMP.FRAME1, 1)
                .input(GenshinSMP.FRAME2)
                .criterion(hasItem(GenshinSMP.FRAME2), conditionsFromItem(GenshinSMP.FRAME2))
                .offerTo(exporter, new Identifier(MOD_ID, "blunt_vision_frame"));

        ShapelessRecipeJsonBuilder.create(RecipeCategory.COMBAT, GenshinSMP.FRAME2, 1)
                .input(GenshinSMP.FRAME1)
                .criterion(hasItem(GenshinSMP.FRAME1), conditionsFromItem(GenshinSMP.FRAME1))
                .offerTo(exporter, new Identifier(MOD_ID, "sharpen_vision_frame"));
    }
}
