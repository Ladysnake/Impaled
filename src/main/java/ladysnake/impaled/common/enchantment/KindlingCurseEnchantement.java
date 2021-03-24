package ladysnake.impaled.common.enchantment;

import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class KindlingCurseEnchantement extends Enchantment {
    public KindlingCurseEnchantement() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    public int getMinPower(int level) {
        return 10;
    }

    public int getMaxPower(int level) {
        return 50;
    }

    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.isOf(ImpaledItems.HELLFORK);
    }

    @Override
    public boolean isCursed() {
        return true;
    }
}
