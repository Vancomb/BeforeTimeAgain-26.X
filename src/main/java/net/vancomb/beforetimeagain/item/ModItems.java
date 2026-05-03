package net.vancomb.beforetimeagain.item;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vancomb.beforetimeagain.BeforeTimeAgain;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(BeforeTimeAgain.MOD_ID);

    public static final DeferredItem<Item> ZIRCON = ITEMS.registerSimpleItem("zircon",
            properties -> properties);

    public static final DeferredItem<Item> RAW_ZIRCON = ITEMS.registerSimpleItem("raw_zircon",
            properties -> properties);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
