package net.vancomb.beforetimeagain.food;

import net.minecraft.world.food.FoodProperties;

public class ModFoodProperties {

    public static final FoodProperties RAW_DODO_MEAT =
            new FoodProperties.Builder()
                    .nutrition(3)
                    .saturationModifier(0.25f)
                    .build();

    public static final FoodProperties COOKED_DODO_MEAT =
            new FoodProperties.Builder()
                    .nutrition(8)
                    .saturationModifier(0.8f)
                    .build();
}