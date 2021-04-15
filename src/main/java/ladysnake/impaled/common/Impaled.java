package ladysnake.impaled.common;

import ladysnake.impaled.common.enchantment.KindlingCurseEnchantement;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class Impaled implements ModInitializer {
    public static final String MODID = "impaled";

    public static Enchantment KINDLING_CURSE;

    private static final Identifier BASTION_TREASURE_CHEST_LOOT_TABLE_ID = new Identifier("minecraft", "chests/bastion_treasure");

    @Override
    public void onInitialize() {
        ImpaledEntityTypes.init();
        ImpaledItems.init();

        KINDLING_CURSE = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier(MODID, "kindling_curse"),
                new KindlingCurseEnchantement()
        );

        // add loot to dungeons, mineshafts, jungle temples, and stronghold libraries chests loot tables
        UniformLootNumberProvider lootTableRange = UniformLootNumberProvider.create(1, 1);
        LootCondition chanceLootCondition = RandomChanceLootCondition.builder(60).build();
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if (BASTION_TREASURE_CHEST_LOOT_TABLE_ID.equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .rolls(lootTableRange)
                        .withCondition(chanceLootCondition)
                        .withEntry(ItemEntry.builder(ImpaledItems.ANCIENT_TRIDENT).build());

                supplier.withPool(poolBuilder.build());
            }
        });

    }
}
