package ladysnake.impaled.mixin;

import ladysnake.impaled.common.PlayerEntityRenderStateExtensions;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void extractCustomRiptideData(AbstractClientPlayerEntity player, PlayerEntityRenderState state, float tickDelta, CallbackInfo ci) {
        // Extract the item stacks and riptide state for use in feature renderers
        PlayerEntityRenderStateExtensions accessor = (PlayerEntityRenderStateExtensions) state;
        accessor.impaled$setMainHandStack(player.getMainHandStack().copy());
        accessor.impaled$setOffHandStack(player.getOffHandStack().copy());
        accessor.impaled$setUsingRiptide(player.isUsingRiptide());
    }
}