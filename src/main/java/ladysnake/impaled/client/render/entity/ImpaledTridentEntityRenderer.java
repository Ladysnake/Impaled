package ladysnake.impaled.client.render.entity;

import ladysnake.impaled.client.render.entity.model.ImpaledTridentEntityModel;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class ImpaledTridentEntityRenderer extends EntityRenderer<ImpaledTridentEntity, ProjectileEntityRenderState> {
    private final ImpaledTridentEntityModel model;
    private final Identifier texture;

    public ImpaledTridentEntityRenderer(EntityRendererFactory.Context context, Identifier texture, EntityModelLayer modelLayer) {
        super(context);
        this.model = new ImpaledTridentEntityModel(context.getPart(modelLayer));
        this.texture = texture;
    }

    @Override
    public ProjectileEntityRenderState createRenderState() {
        return new ProjectileEntityRenderState();
    }

    @Override
    public void updateRenderState(ImpaledTridentEntity entity, ProjectileEntityRenderState renderState, float tickDelta) {
        super.updateRenderState(entity, renderState, tickDelta);

        // Calculate rotation based on velocity for proper flight orientation
        float velocityX = (float) entity.getVelocity().x;
        float velocityY = (float) entity.getVelocity().y;
        float velocityZ = (float) entity.getVelocity().z;

        // Check if trident is still moving
        boolean hasVelocity = velocityX * velocityX + velocityY * velocityY + velocityZ * velocityZ > 0.001f;

        if (hasVelocity) {
            // Calculate yaw from horizontal velocity
            renderState.yaw = (float)(MathHelper.atan2(velocityX, velocityZ) * (180.0 / Math.PI));

            // Calculate pitch from vertical velocity
            float horizontalVelocity = MathHelper.sqrt(velocityX * velocityX + velocityZ * velocityZ);
            renderState.pitch = (float)(MathHelper.atan2(velocityY, horizontalVelocity) * (180.0 / Math.PI));
        } else {
            // When landed, use entity's stored rotation (from when it hit)
            renderState.yaw = entity.getYaw();
            renderState.pitch = entity.getPitch();
        }
    }

    @Override
    public void render(ProjectileEntityRenderState renderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(renderState.yaw - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(renderState.pitch + 90.0F));
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(renderState)));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(renderState, matrixStack, vertexConsumerProvider, i);
    }

    public Identifier getTexture(ProjectileEntityRenderState renderState) {
        return this.texture;
    }
}
