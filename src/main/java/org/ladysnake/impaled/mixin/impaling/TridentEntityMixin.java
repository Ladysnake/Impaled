package org.ladysnake.impaled.mixin.impaling;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.ladysnake.impaled.common.enchantment.BetterImpaling;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {

    protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyConstant(
            method = "onEntityHit",
            constant = @Constant(ordinal = 0)
    )
    private float getAttackDamage(float baseDamage, EntityHitResult result) {
        return baseDamage + BetterImpaling.getAttackDamage(this.getItemStack(), result.getEntity());
    }
}
