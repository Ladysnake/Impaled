package org.ladysnake.impaled.mixin.impaling;

import net.minecraft.enchantment.ImpalingEnchantment;
import net.minecraft.entity.EntityGroup;
import org.ladysnake.impaled.common.enchantment.BetterImpaling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ImpalingEnchantment.class)
public abstract class ImpalingEnchantmentMixin {
    /**
     * @reason we are canceling the vanilla effect and replacing it with our own in {@link BetterImpaling}
     * @author Pyrofab
     */
    @Overwrite
    public float getAttackDamage(int level, EntityGroup group) {
        return 0.0F;
    }
}
