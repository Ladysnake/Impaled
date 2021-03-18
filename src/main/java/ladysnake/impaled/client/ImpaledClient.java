package ladysnake.impaled.client;

import ladysnake.impaled.client.render.entity.ImpaledTridentEntityRenderer;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.minecraft.util.Identifier;

public class ImpaledClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(ImpaledEntityTypes.PITCHFORK, ctx -> new ImpaledTridentEntityRenderer(ctx, new Identifier(Impaled.MODID, "textures/entity/pitchfork.png")));
        EntityRendererRegistry.INSTANCE.register(ImpaledEntityTypes.HELLFORK, ctx -> new ImpaledTridentEntityRenderer(ctx, new Identifier(Impaled.MODID, "textures/entity/hellfork.png")));
        EntityRendererRegistry.INSTANCE.register(ImpaledEntityTypes.ELDER_TRIDENT, ctx -> new ImpaledTridentEntityRenderer(ctx, new Identifier(Impaled.MODID, "textures/entity/elder_trident.png")));
    }
}
