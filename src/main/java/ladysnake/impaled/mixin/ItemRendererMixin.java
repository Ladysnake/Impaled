package ladysnake.impaled.mixin;

import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    private static final ThreadLocal<Item> IMPALED$RENDERING_TRIDENT = new ThreadLocal<>();
    @Shadow
    @Final
    private ItemModels models;

    @Inject(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal = 0)
    )
    private void captureItem(ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model, CallbackInfo ci) {
        // yes it is fine to check if a Set<XItem> contains an Item
        //noinspection SuspiciousMethodCalls
        if (ImpaledItems.ALL_TRIDENTS.contains(stack.getItem())) {
            IMPALED$RENDERING_TRIDENT.set(stack.getItem());
        }
    }

    @ModifyVariable(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;TRIDENT:Lnet/minecraft/item/Item;", ordinal = 0)
    )
    public BakedModel renderItem(BakedModel model, ItemStack stack) {
        Item trident = IMPALED$RENDERING_TRIDENT.get();
        if (trident != null) {
            return this.models.getModelManager().getModel(new ModelIdentifier(Registry.ITEM.getId(trident), "inventory"));
        }
        return model;
    }

    @ModifyArg(
            method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z", ordinal = 2)
    )
    public Item renderItem(Item item) {
        assert item == Items.TRIDENT;

        Item trident = IMPALED$RENDERING_TRIDENT.get();
        if (trident != null) {
            IMPALED$RENDERING_TRIDENT.set(null);
            return trident;
        }

        return item;
    }

    @ModifyVariable(method = "getHeldItemModel", at = @At("STORE"), ordinal = 0)
    private BakedModel swapModel(BakedModel original, ItemStack stack) {
        //noinspection SuspiciousMethodCalls
        if (ImpaledItems.ALL_TRIDENTS.contains(stack.getItem())) {
            return this.models.getModelManager().getModel(new ModelIdentifier(Registry.ITEM.getId(stack.getItem()) + "_in_hand#inventory"));
        }
        return original;
    }
}
