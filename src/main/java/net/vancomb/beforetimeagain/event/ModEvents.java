package net.vancomb.beforetimeagain.event;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BlockEntityTypeAddBlocksEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.vancomb.beforetimeagain.BeforeTimeAgain;
import net.vancomb.beforetimeagain.block.ModBlocks;
import net.vancomb.beforetimeagain.entity.custom.ModEntities;
import net.vancomb.beforetimeagain.entity.custom.DodoEntity;



@EventBusSubscriber(modid = BeforeTimeAgain.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DODO.get(), DodoEntity.createDodoAttributes().build());
    }

    @SubscribeEvent
    public static void registerBrushableBlocks(BlockEntityTypeAddBlocksEvent event) {
        event.modify(BlockEntityType.BRUSHABLE_BLOCK, ModBlocks.ANCIENT_SAND.get());
    }
}
