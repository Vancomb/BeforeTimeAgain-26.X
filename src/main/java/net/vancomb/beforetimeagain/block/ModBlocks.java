package net.vancomb.beforetimeagain.block;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.vancomb.beforetimeagain.BeforeTimeAgain;
import net.vancomb.beforetimeagain.item.ModItems;

import java.util.function.Function;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(BeforeTimeAgain.MOD_ID);


    public static final DeferredBlock<Block> ANCIENT_SAND = registerBlock("ancient_sand",
            properties -> new BrushableBlock(
                    Blocks.AIR,
                    SoundEvents.BRUSH_SAND,
                    SoundEvents.BRUSH_SAND_COMPLETED,
                    properties
                            .strength(4f)
                            .requiresCorrectToolForDrops()
                            .sound(SoundType.SAND)));


    public static final DeferredBlock<Block> DODO_EGG = registerBlock("dodo_egg",
            properties -> new Block(properties
                    .strength(0.5f)
                    .sound(SoundType.STONE)
                    .noOcclusion()));



    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
