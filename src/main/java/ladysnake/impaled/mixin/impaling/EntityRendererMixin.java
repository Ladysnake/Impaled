package ladysnake.impaled.mixin.impaling;

import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {
    @Inject(method = "getBlockLight", at = @At("HEAD"), cancellable = true)
    protected void getBlockLight(T entity, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (entity instanceof LivingEntity livingEntity && livingEntity.isUsingRiptide() && (livingEntity.getMainHandStack().getItem() == ImpaledItems.HELLFORK || livingEntity.getOffHandStack().getItem() == ImpaledItems.HELLFORK)) {
            cir.setReturnValue(15);
        }
    }
}
