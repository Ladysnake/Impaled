package ladysnake.impaled.mixin;

import ladysnake.impaled.common.item.ImpaledTridentItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {
    @ModifyVariable(
            method = "onStoppedUsing",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getRiptide(Lnet/minecraft/item/ItemStack;)I"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z")
            ),
            at = @At(value = "LOAD", ordinal = 0),
            index = 7
    )
    private int foolRiptide(int riptideLevel, ItemStack stack, World world, LivingEntity user) {
        //noinspection ConstantConditions
        if ((Object) this instanceof ImpaledTridentItem impaledTrident) {
            if (riptideLevel > 0 && impaledTrident.canRiptide(user)) {
                // Negative values pass the if check, and allow us to easily restore the right value later
                return -riptideLevel;
            }
        }
        return riptideLevel;
    }

    @ModifyVariable(
            method = "onStoppedUsing",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z")
            ),
            at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;isClient:Z"),
            index = 7
    )
    private int restoreRiptide(int riptideLevel) {
        //noinspection ConstantConditions
        if ((Object) this instanceof ImpaledTridentItem) {
            if (riptideLevel < 0) {
                return -riptideLevel;
            }
        }
        return riptideLevel;
    }

    // this is only called if riptideLevel > 0
    // the above mixin also ensures !(this instanceof ImpaledTridentItem && impaledTrident.canRiptide(user))
    // so we know that we must cancel the riptide (and therefore the whole method) if an impaled trident gets here
    @Inject(
            method = "onStoppedUsing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isTouchingWaterOrRain()Z", shift = At.Shift.AFTER),
            cancellable = true
    )
    private void cancelRiptide(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        //noinspection ConstantConditions
        if ((Object) this instanceof ImpaledTridentItem) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "onStoppedUsing", at = @At("STORE"))
    private TridentEntity swapTrident(TridentEntity base, ItemStack stack, World world, LivingEntity user) {
        //noinspection ConstantConditions
        if ((Object) this instanceof ImpaledTridentItem impaledTrident) {
            return impaledTrident.createTrident(world, user, stack);
        }
        return base;
    }
}
