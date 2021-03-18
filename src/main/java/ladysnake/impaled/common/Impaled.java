package ladysnake.impaled.common;

import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.impaled.common.init.ImpaledItems;
import net.fabricmc.api.ModInitializer;

public class Impaled implements ModInitializer {
    public static final String MODID = "impaled";

    @Override
    public void onInitialize() {
        ImpaledEntityTypes.init();
        ImpaledItems.init();
    }
}
