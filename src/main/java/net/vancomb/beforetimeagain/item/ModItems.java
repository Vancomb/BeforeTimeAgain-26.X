package net.vancomb.beforetimeagain.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vancomb.beforetimeagain.BeforeTimeAgain;
import net.vancomb.beforetimeagain.entity.custom.ModEntities;
import net.vancomb.beforetimeagain.food.ModFoodProperties;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BeforeTimeAgain.MOD_ID);

    public static final DeferredItem<Item> DODO_FOSSIL = ITEMS.registerItem("dodo_fossil",
            properties -> new Item(properties));

    public static final DeferredItem<Item> RAW_DODO_MEAT = ITEMS.registerItem("raw_dodo_meat",
            properties -> new Item(properties.food(ModFoodProperties.RAW_DODO_MEAT)));

    public static final DeferredItem<Item> COOKED_DODO_MEAT = ITEMS.registerItem("cooked_dodo_meat",
            properties -> new Item(properties.food(ModFoodProperties.COOKED_DODO_MEAT)));

    //Find out when he switches from SimpleItem to RegisterItem?
    public static final DeferredItem<Item> DODO_SPAWN_EGG = ITEMS.registerItem ("dodo_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(ModEntities.DODO.get())));

    public static final DeferredItem<Item> FOSSIL = ITEMS.registerItem("fossil",
            properties -> new Item(properties));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
