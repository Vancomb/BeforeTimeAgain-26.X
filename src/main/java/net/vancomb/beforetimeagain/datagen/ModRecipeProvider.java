package net.vancomb.beforetimeagain.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.vancomb.beforetimeagain.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        SimpleCookingRecipeBuilder.generic(
                        Ingredient.of(ModItems.RAW_DODO_MEAT.get()),
                        RecipeCategory.FOOD,
                        CookingBookCategory.FOOD,
                        ModItems.COOKED_DODO_MEAT.get(),
                        0.35f,
                        200,
                        SmeltingRecipe::new
                )
                .unlockedBy(getHasName(ModItems.RAW_DODO_MEAT.get()), has(ModItems.RAW_DODO_MEAT.get()))
                .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> registries) {
            super(packOutput, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Before Time Again Recipes";
        }
    }
}