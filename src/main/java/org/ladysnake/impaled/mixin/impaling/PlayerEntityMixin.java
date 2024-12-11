package org.ladysnake.impaled.mixin.impaling;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.ladysnake.impaled.common.enchantment.BetterImpaling;
import org.ladysnake.impaled.common.item.HellforkItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyVariable(method = "attack", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityGroup;)F"), ordinal = 1)
    private float getAttackDamage(float baseDamage, Entity target) {
        return baseDamage + BetterImpaling.getAttackDamage(this.getMainHandStack(), target);
    }

    @ModifyConstant(method = "attack", constant = @Constant(expandZeroConditions = Constant.Condition.GREATER_THAN_ZERO), slice = @Slice(from = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getFireAspect(Lnet/minecraft/entity/LivingEntity;)I", ordinal = 0)))
    private int checkFireDamage(int constant) {
        return this.getMainHandStack().getItem() instanceof HellforkItem ? -1 : constant;
    }
}
