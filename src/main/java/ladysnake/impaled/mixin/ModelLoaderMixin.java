package ladysnake.impaled.mixin;

import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {
    @Shadow protected abstract void addModel(ModelIdentifier modelId);

    @Inject(method = "<init>", at = @At(value = "CONSTANT", args = "stringValue=minecraft:trident_in_hand#inventory"))
    private void loadSpecialTridentModels(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i, CallbackInfo ci) {
        for (Item item : ImpaledItems.ALL_TRIDENTS) {
            this.addModel(new ModelIdentifier(Registry.ITEM.getId(item) + "_in_hand#inventory"));
        }
    }
}
