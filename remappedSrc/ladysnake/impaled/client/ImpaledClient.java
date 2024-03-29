package ladysnake.impaled.client;

import ladysnake.impaled.client.render.entity.ImpaledTridentEntityRenderer;
import ladysnake.impaled.client.render.entity.model.ImpaledTridentEntityModel;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.impaled.common.item.ImpaledTridentItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ImpaledClient implements ClientModInitializer {
    public static final Identifier HELLFORK_RIPTIDE_TEXTURE = new Identifier(Impaled.MODID, "textures/entity/hellfork_riptide.png");
    public static final Identifier SOULFORK_RIPTIDE_TEXTURE = new Identifier(Impaled.MODID, "textures/entity/soulfork_riptide.png");
    public static final EntityModelLayer ATLAN = new EntityModelLayer(new Identifier(Impaled.MODID, "atlan"), "main");

    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(ATLAN, ImpaledTridentEntityModel::getAtlanTexturedModelData);

        for (ImpaledTridentItem item : ImpaledItems.ALL_TRIDENTS) {
            Identifier tridentId = Registry.ITEM.getId(item);
            Identifier texture = new Identifier(tridentId.getNamespace(), "textures/entity/" + tridentId.getPath() + ".png");

            EntityModelLayer modelLayer = item == ImpaledItems.ATLAN ? ATLAN : EntityModelLayers.TRIDENT;
            ImpaledTridentItemRenderer tridentItemRenderer = new ImpaledTridentItemRenderer(tridentId, texture, modelLayer);
            ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(tridentItemRenderer);
            BuiltinItemRendererRegistry.INSTANCE.register(item, tridentItemRenderer);
            EntityRendererRegistry.register(item.getEntityType(), ctx -> new ImpaledTridentEntityRenderer(ctx, texture, modelLayer));

            FabricModelPredicateProviderRegistry.register(item, new Identifier("throwing"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
            ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> out.accept(new ModelIdentifier(tridentId + "_in_inventory", "inventory")));
        }

        EntityRendererRegistry.register(ImpaledEntityTypes.GUARDIAN_TRIDENT, ctx -> new ImpaledTridentEntityRenderer(ctx, new Identifier(Impaled.MODID, "textures/entity/guardian_trident.png"), EntityModelLayers.TRIDENT));
    }
}
