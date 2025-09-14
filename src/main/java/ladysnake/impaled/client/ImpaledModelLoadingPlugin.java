package ladysnake.impaled.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.util.Identifier;

public class ImpaledModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void initialize(Context pluginContext) {
        // Add all the inventory models to ensure they're loaded
        pluginContext.addModels(
            Identifier.of("impaled", "item/pitchfork_in_inventory"),
            Identifier.of("impaled", "item/hellfork_in_inventory"),
            Identifier.of("impaled", "item/soulfork_in_inventory"),
            Identifier.of("impaled", "item/elder_trident_in_inventory"),
            Identifier.of("impaled", "item/atlan_in_inventory")
        );
    }
}