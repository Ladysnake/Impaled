package org.ladysnake.impaled.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.ladysnake.impaled.client.render.entity.model.ImpaledTridentEntityModel;
import org.ladysnake.impaled.common.entity.ImpaledTridentEntity;
import org.ladysnake.impaled.compat.EnchancementCompat;

@Environment(EnvType.CLIENT)
public class ImpaledTridentEntityRenderer extends EntityRenderer<ImpaledTridentEntity> {
    private final ImpaledTridentEntityModel model;
    private final Identifier texture;

    public ImpaledTridentEntityRenderer(EntityRendererFactory.Context context, Identifier texture, EntityModelLayer modelLayer) {
        super(context);
        this.model = new ImpaledTridentEntityModel(context.getPart(modelLayer));
        this.texture = texture;
    }

    public void render(ImpaledTridentEntity trident, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (EnchancementCompat.tryRenderLeechTrident(
                trident,
                matrices,
                vertexConsumers,
                model,
                getTexture(trident),
                light,
                () -> super.render(trident, yaw, tickDelta, matrices, vertexConsumers, light)
        )) return;

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp(tickDelta, trident.prevYaw, trident.getYaw()) - 90.0F));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp(tickDelta, trident.prevPitch, trident.getPitch()) + 90.0F));
        VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.model.getLayer(this.getTexture(trident)), false, trident.isEnchanted());
        this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();
        super.render(trident, yaw, tickDelta, matrices, vertexConsumers, light);
    }

    public Identifier getTexture(ImpaledTridentEntity impaledTridentEntity) {
        return this.texture;
    }
}
