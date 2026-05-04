package net.vancomb.beforetimeagain.event;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.vancomb.beforetimeagain.BeforeTimeAgain;
import net.vancomb.beforetimeagain.entity.ModEntities;
import net.vancomb.beforetimeagain.entity.custom.DodoEntity;



@EventBusSubscriber(modid = BeforeTimeAgain.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.DODO.get(), DodoEntity.createDodoAttributes().build());
    }
}
