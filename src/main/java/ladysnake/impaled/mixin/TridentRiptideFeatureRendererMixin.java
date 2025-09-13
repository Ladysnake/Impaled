package ladysnake.impaled.mixin;

import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.sincereloyalty.SincereLoyalty;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static ladysnake.impaled.client.ImpaledClient.HELLFORK_RIPTIDE_TEXTURE;
import static ladysnake.impaled.client.ImpaledClient.SOULFORK_RIPTIDE_TEXTURE;

@Mixin(TridentRiptideFeatureRenderer.class)
public abstract class TridentRiptideFeatureRendererMixin {
    @ModifyVariable(method = "render", at = @At("STORE"))
    private VertexConsumer swapHotRiptide(VertexConsumer orig, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, PlayerEntityRenderState state, float animationProgress, float headYaw) {
        // In 1.21.3, we work with render states instead of entities directly
        // For now, disable the custom riptide textures as we need to refactor
        // how we access item stacks from the render state
        // TODO: Find a way to access player's held items from PlayerEntityRenderState
        return orig;
    }
}