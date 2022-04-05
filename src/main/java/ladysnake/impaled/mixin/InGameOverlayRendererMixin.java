package ladysnake.impaled.mixin;

import ladysnake.impaled.common.item.HellforkItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
public class InGameOverlayRendererMixin {
    @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
    private static void renderFireOverlay(MinecraftClient client, MatrixStack matrices, CallbackInfo ci) {
        if (MinecraftClient.getInstance().player.isUsingRiptide() && (MinecraftClient.getInstance().player.getMainHandStack().getItem() instanceof HellforkItem || MinecraftClient.getInstance().player.getOffHandStack().getItem() instanceof HellforkItem)) {
            ci.cancel();
        }
    }
}
