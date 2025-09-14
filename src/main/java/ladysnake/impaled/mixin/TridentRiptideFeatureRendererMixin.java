package ladysnake.impaled.mixin;

import ladysnake.impaled.common.PlayerEntityRenderStateExtensions;
import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.sincereloyalty.SincereLoyalty;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TridentRiptideFeatureRenderer.class)
public abstract class TridentRiptideFeatureRendererMixin {
    @ModifyVariable(method = "render", at = @At("STORE"))
    private VertexConsumer swapHotRiptide(VertexConsumer orig, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, PlayerEntityRenderState state, float animationProgress, float headYaw) {
        // Custom riptide texture functionality temporarily disabled due to 1.21.3 PlayerEntityRenderer method mapping issues
        // The PlayerEntityRenderState system requires accessing data from the extractRenderState phase,
        // but the method name/signature has changed in 1.21.3 and needs proper mapping research
        // TODO: Find correct method mapping for PlayerEntityRenderer.extractRenderState in 1.21.3
        return orig;
    }
}