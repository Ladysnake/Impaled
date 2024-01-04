package org.ladysnake.impaled.client;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.resource.ResourceReloadListenerKeys;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.ladysnake.impaled.client.render.entity.model.ImpaledTridentEntityModel;

import java.util.Collection;
import java.util.Collections;

public class ImpaledTridentItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer, SimpleSynchronousResourceReloadListener {
    private final Identifier id;
    private final Identifier tridentId;
    private final Identifier texture;
    private final EntityModelLayer modelLayer;
    private ItemRenderer itemRenderer;
    private ImpaledTridentEntityModel tridentModel;
    private BakedModel inventoryTridentModel;

    public ImpaledTridentItemRenderer(Identifier tridentId, Identifier texture, EntityModelLayer modelLayer) {
        this.id = new Identifier(tridentId.getNamespace(), tridentId.getPath() + "_renderer");
        this.tridentId = tridentId;
        this.texture = texture;
        this.modelLayer = modelLayer;
    }

    @Override
    public Identifier getFabricId() {
        return this.id;
    }

    @Override
    public Collection<Identifier> getFabricDependencies() {
        return Collections.singletonList(ResourceReloadListenerKeys.MODELS);
    }

    @Override
    public void reload(ResourceManager manager) {
        MinecraftClient mc = MinecraftClient.getInstance();
        this.itemRenderer = mc.getItemRenderer();
        this.tridentModel = new ImpaledTridentEntityModel(mc.getEntityModelLoader().getModelPart(this.modelLayer));
        this.inventoryTridentModel = mc.getBakedModelManager().getModel(new ModelIdentifier(this.tridentId.getNamespace(), this.tridentId.getPath() + "_in_inventory", "inventory"));
    }

    @Override
    public void render(ItemStack stack, ModelTransformationMode renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        assert this.tridentModel != null;
        if (renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED) {
            matrices.pop(); // cancel the previous transformation and pray that we are not breaking the state
            matrices.push();
            itemRenderer.renderItem(stack, renderMode, false, matrices, vertexConsumers, light, overlay, this.inventoryTridentModel);
        } else {
            matrices.push();
            matrices.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.tridentModel.getLayer(this.texture), false, stack.hasGlint());
            this.tridentModel.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrices.pop();
        }
    }
}
