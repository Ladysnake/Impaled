package ladysnake.impaled.mixin;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import ladysnake.impaled.client.ImpaledClient;
import ladysnake.impaled.client.render.entity.model.ImpaledTridentEntityModel;
import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(BuiltinModelItemRenderer.class)
public abstract class BuiltinModelItemRendererMixin {
    @Shadow @Final private EntityModelLoader entityModelLoader;
    private final Map<Item, ImpaledTridentEntityModel> impaled$tridentModels = new Reference2ReferenceOpenHashMap<>();

    @Inject(method = "apply", at = @At("RETURN"))
    private void getModdedTridentModels(ResourceManager manager, CallbackInfo ci) {
        this.impaled$tridentModels.clear();
        for (Item trident : ImpaledItems.ALL_TRIDENTS) {
            this.impaled$tridentModels.put(trident, new ImpaledTridentEntityModel(this.entityModelLoader.getModelPart(EntityModelLayers.TRIDENT)));
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void renderModdedTridents(ItemStack stack, ModelTransformation.Mode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CallbackInfo ci) {
        ImpaledTridentEntityModel model = this.impaled$tridentModels.get(stack.getItem());
        if (model != null) {
            matrices.push();
            matrices.scale(1.0F, -1.0F, -1.0F);
            VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, model.getLayer(ImpaledClient.tridentEntityTextures.get(stack.getItem())), false, stack.hasGlint());
            model.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
            matrices.pop();
        }
    }
}
