package ladysnake.impaled.client;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import ladysnake.impaled.client.render.entity.ImpaledTridentEntityRenderer;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.impaled.common.item.ImpaledTridentItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;

public class ImpaledClient implements ClientModInitializer {
    public static final Map<Item, Identifier> tridentEntityTextures = new Reference2ObjectOpenHashMap<>();

    @Override
    public void onInitializeClient() {
        for (ImpaledTridentItem item : ImpaledItems.ALL_TRIDENTS) {
            Identifier texture = new Identifier(Impaled.MODID, "textures/entity/" + Registry.ITEM.getId(item).getPath() + ".png");
            tridentEntityTextures.put(item, texture);
            EntityRendererRegistry.INSTANCE.register(item.getEntityType(), ctx -> new ImpaledTridentEntityRenderer(ctx, texture));
            FabricModelPredicateProviderRegistry.register(item, new Identifier("throwing"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0F : 0.0F);
        }
    }
}
