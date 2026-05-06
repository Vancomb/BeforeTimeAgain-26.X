package net.vancomb.beforetimeagain.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vancomb.beforetimeagain.BeforeTimeAgain;
import net.vancomb.beforetimeagain.entity.ModEntities;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BeforeTimeAgain.MOD_ID);

    public static final DeferredItem<Item> ZIRCON = ITEMS.registerSimpleItem("zircon",
            properties -> properties);

    public static final DeferredItem<Item> RAW_ZIRCON = ITEMS.registerSimpleItem("raw_zircon",
            properties -> properties);

    //Find out when he switches from SimpleItem to RegisterItem?
    public static final DeferredItem<Item> DODO_SPAWN_EGG = ITEMS.registerItem ("dodo_spawn_egg",
            properties -> new SpawnEggItem(properties.spawnEgg(ModEntities.DODO.get())));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
