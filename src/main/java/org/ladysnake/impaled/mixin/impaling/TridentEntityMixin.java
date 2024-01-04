package org.ladysnake.impaled.mixin.impaling;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import org.ladysnake.impaled.common.enchantment.BetterImpaling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin {
    @Shadow
    private ItemStack tridentStack;

    @ModifyVariable(
            method = "onEntityHit",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/EntityHitResult;getEntity()Lnet/minecraft/entity/Entity;"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F")
            ),
            at = @At(value = "STORE", ordinal = 0),
            ordinal = 0
    )
    private float getAttackDamage(float baseDamage, EntityHitResult result) {
        return baseDamage + BetterImpaling.getAttackDamage(this.tridentStack, result.getEntity());
    }
}
