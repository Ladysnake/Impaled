package ladysnake.impaled.common;

import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;

public class Impaled implements ModInitializer {
    public static final String MODID = "impaled";

    private static final Identifier BASTION_TREASURE_CHEST_LOOT_TABLE_ID = Identifier.of("minecraft", "chests/bastion_treasure");

    @Override
    public void onInitialize() {
        ImpaledEntityTypes.init();
        ImpaledItems.init();

        // Commenting out loot table modification for now due to API changes
        // TODO: Re-implement loot table modification with 1.21.3 API
        /*
        UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
        LootCondition chanceLootCondition = RandomChanceLootCondition.builder(60).build();
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            Identifier id = key.getValue();
            if (BASTION_TREASURE_CHEST_LOOT_TABLE_ID.equals(id)) {
                LootPool lootPool = LootPool.builder()
                        .rolls(lootTableRange)
                        .conditionally(chanceLootCondition)
                        .with(ItemEntry.builder(ImpaledItems.ANCIENT_TRIDENT).build()).build();

                tableBuilder.pool(lootPool);
            }
        });
        */
    }
}
