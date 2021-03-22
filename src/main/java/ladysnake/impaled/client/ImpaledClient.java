package ladysnake.impaled.client;

import ladysnake.impaled.client.render.entity.ImpaledTridentEntityRenderer;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.impaled.common.item.ImpaledTridentItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ImpaledClient implements ClientModInitializer {
    public static final Identifier HELLFORK_RIPTIDE_TEXTURE = new Identifier(Impaled.MODID, "textures/entity/hellfork_riptide.png");

    @Override
    public void onInitializeClient() {
        for (ImpaledTridentItem item : ImpaledItems.ALL_TRIDENTS) {
            Identifier tridentId = Registry.ITEM.getId(item);
            Identifier texture = new Identifier(tridentId.getNamespace(), "textures/entity/" + tridentId.getPath() + ".png");
            ImpaledTridentItemRenderer tridentItemRenderer = new ImpaledTridentItemRenderer(tridentId, texture);
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(tridentItemRenderer);
            BuiltinItemRendererRegistry.INSTANCE.register(item, tridentItemRenderer);
            EntityRendererRegistry.INSTANCE.register(item.getEntityType(), ctx -> new ImpaledTridentEntityRenderer(ctx, texture));
            FabricModelPredicateProviderRegistry.register(item, new Identifier("throwing"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
            ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(new ModelIdentifier(tridentId + "_in_inventory", "inventory")));
        }
    }
}
