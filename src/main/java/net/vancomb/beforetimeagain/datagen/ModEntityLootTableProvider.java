package net.vancomb.beforetimeagain.datagen;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.vancomb.beforetimeagain.entity.ModEntities;
import net.vancomb.beforetimeagain.item.ModItems;

import java.util.stream.Stream;

public class ModEntityLootTableProvider extends EntityLootSubProvider {
    public ModEntityLootTableProvider(HolderLookup.Provider registries) {
        super(FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    public void generate() {
        add(ModEntities.DODO.get(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1f))
                                .add(LootItem.lootTableItem(ModItems.RAW_DODO_MEAT)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 1)))
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))
                                .when(LootItemKilledByPlayerCondition.killedByPlayer()))
                        .withPool(LootPool.lootPool()
                                .setRolls(ConstantValue.exactly(1f))
                                .add(LootItem.lootTableItem(net.minecraft.world.item.Items.FEATHER)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0, 2)))
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(this.registries, UniformGenerator.between(0, 1))))));
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return ModEntities.ENTITY_TYPES.getEntries().stream().map(Holder::value);


    }
}
