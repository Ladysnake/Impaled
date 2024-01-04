package org.ladysnake.impaled.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.impaled.common.item.HellforkItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public World world;

    @Shadow
    @Nullable
    public abstract ItemEntity dropStack(ItemStack stack);

    @Shadow
    public abstract double getX();

    @Shadow
    public abstract double getY();

    @Shadow
    public abstract double getZ();

    @Inject(method = "doesRenderOnFire", at = @At(value = "RETURN"), cancellable = true)
    public void removePlayerFireRenderDuringHellforkRiptide(CallbackInfoReturnable<Boolean> cir) {
        if (((Object) this) instanceof PlayerEntity && ((PlayerEntity) (Object) this).isUsingRiptide() && ((((PlayerEntity) (Object) this).getMainHandStack().getItem() instanceof HellforkItem) || (((PlayerEntity) (Object) this).getOffHandStack().getItem() instanceof HellforkItem))) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "dropStack(Lnet/minecraft/item/ItemStack;F)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V"), cancellable = true)
    protected void impaled$dropStack(ItemStack stack, float yOffset, CallbackInfoReturnable<ItemEntity> cir) {
        // overridden in LivingEntityMixin
    }

    @Inject(method = "isOnFire", at = @At(value = "RETURN"), cancellable = true)
    public void isOnFire(CallbackInfoReturnable<Boolean> cir) {
        if (((Object) this) instanceof PlayerEntity && ((PlayerEntity) (Object) this).isUsingRiptide() && ((((PlayerEntity) (Object) this).getMainHandStack().getItem() instanceof HellforkItem) || (((PlayerEntity) (Object) this).getOffHandStack().getItem() instanceof HellforkItem))) {
            cir.setReturnValue(true);
        }
    }

}
