package net.vancomb.beforetimeagain.creativetab;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vancomb.beforetimeagain.BeforeTimeAgain;
import net.vancomb.beforetimeagain.item.ModItems;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, BeforeTimeAgain.MOD_ID);


    public static final Supplier<CreativeModeTab> BEFORE_TIME_AGAIN_TAB = CREATIVE_MODE_TABS.register("before_time_again_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.ZIRCON.get()))
                    .title(Component.translatable("creativetab.beforetimeagain.before_time_again"))
                    .displayItems((itemDisplayParameters, output) -> {
                      output.accept(ModItems.ZIRCON);
                      output.accept(ModItems.RAW_ZIRCON);
                      output.accept(ModItems.DODO_SPAWN_EGG);


                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);

    }
}
