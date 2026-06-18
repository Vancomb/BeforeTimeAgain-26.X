package net.vancomb.beforetimeagain.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.vancomb.beforetimeagain.block.ModBlocks;
import net.vancomb.beforetimeagain.item.ModItems;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        add(ModBlocks.ANCIENT_SAND.get(),
                block -> createOreDrop(block, ModItems.DODO_FOSSIL.get()));
    }
}
