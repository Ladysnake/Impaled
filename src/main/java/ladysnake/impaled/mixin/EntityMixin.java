package ladysnake.impaled.mixin;

import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "doesRenderOnFire", at = @At(value = "RETURN"), cancellable = true)
    public void removePlayerFireRenderDuringHellforkRiptide(CallbackInfoReturnable<Boolean> cir) {
        if (((Object)this) instanceof PlayerEntity && ((PlayerEntity)(Object)this).isUsingRiptide() && ((((PlayerEntity)(Object)this).getMainHandStack().getItem() == ImpaledItems.HELLFORK) || (((PlayerEntity)(Object)this).getOffHandStack().getItem() == ImpaledItems.HELLFORK))) {
            cir.setReturnValue(false);
        }
    }
}
