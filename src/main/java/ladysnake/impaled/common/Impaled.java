package ladysnake.impaled.common;

import ladysnake.impaled.common.enchantment.HotrodEnchantement;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.impl.registry.sync.FabricRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.RiptideEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Impaled implements ModInitializer {
    public static final String MODID = "impaled";

    public static Enchantment HOTROD;

    @Override
    public void onInitialize() {
        ImpaledEntityTypes.init();
        ImpaledItems.init();

        HOTROD = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier(MODID, "hotrod"),
                new HotrodEnchantement()
        );
    }
}
