package net.vancomb.beforetimeagain;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.vancomb.beforetimeagain.datagen.*;

import java.util.Collections;
import java.util.List;


@EventBusSubscriber(modid = BeforeTimeAgain.MOD_ID)
public class BeforeTimeAgainDataGenerators {
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        var lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new ModModelProvider(packOutput));

        generator.addProvider(true, new LootTableProvider(packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(ModEntityLootTableProvider::new, LootContextParamSets.ENTITY)), lookupProvider));

        generator.addProvider(true, new ModRecipeProvider.Runner(packOutput, lookupProvider));
        generator.addProvider(true, new ModItemTagProvider(packOutput, lookupProvider));

        generator.addProvider(true, new ModSoundsProvider(packOutput));

    }
}
