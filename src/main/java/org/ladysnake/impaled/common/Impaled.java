package org.ladysnake.impaled.common;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import org.ladysnake.impaled.common.entity.MialeeTargetingUtil;
import org.ladysnake.impaled.common.init.ImpaledEntityTypes;
import org.ladysnake.impaled.common.init.ImpaledItems;

public class Impaled implements ModInitializer {
    public static final String MODID = "impaled";

    private static final Identifier BASTION_TREASURE_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/bastion_treasure");

    public static Identifier id(String path) {
        return new Identifier(MODID, path);
    }

    @Override
    public void onInitialize() {
        ImpaledEntityTypes.init();
        ImpaledItems.init();
        MialeeTargetingUtil.init();

        // add loot to dungeons, mineshafts, jungle temples, and stronghold libraries chests loot tables
        UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
        LootCondition.Builder chanceLootCondition = RandomChanceLootCondition.builder(60);
        LootTableEvents.MODIFY.register((tableRegKey, tableBuilder, tableSource) -> {
            if (BASTION_TREASURE_CHEST_LOOT_TABLE_ID.equals(tableRegKey.getValue())) {
                LootPool.Builder lootPool = LootPool.builder()
                        .rolls(lootTableRange)
                        .conditionally(chanceLootCondition)
                        .with(ItemEntry.builder(ImpaledItems.ANCIENT_TRIDENT));

                tableBuilder.pool(lootPool);
            }
        });
    }
}
