package ladysnake.impaled.common;

import ladysnake.impaled.common.enchantment.KindlingCurseEnchantement;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Impaled implements ModInitializer {
    public static final String MODID = "impaled";

    public static Enchantment KINDLING_CURSE;

    @Override
    public void onInitialize() {
        ImpaledEntityTypes.init();
        ImpaledItems.init();

        KINDLING_CURSE = Registry.register(
                Registry.ENCHANTMENT,
                new Identifier(MODID, "kindling_curse"),
                new KindlingCurseEnchantement()
        );
    }
}
