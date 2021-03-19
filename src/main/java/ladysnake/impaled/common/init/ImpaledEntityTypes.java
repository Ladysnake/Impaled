package ladysnake.impaled.common.init;

import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.entity.ElderTridentEntity;
import ladysnake.impaled.common.entity.HellforkEntity;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.entity.PitchforkEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.registry.Registry;

public class ImpaledEntityTypes {
    public static EntityType<ImpaledTridentEntity> PITCHFORK;
    public static EntityType<ImpaledTridentEntity> HELLFORK;
    public static EntityType<ImpaledTridentEntity> ELDER_TRIDENT;

    public static void init() {
        PITCHFORK = register("pitchfork", createEntityType(PitchforkEntity::new));
        HELLFORK = register("hellfork", createEntityType(HellforkEntity::new));
        ELDER_TRIDENT = register("elder_trident", createEntityType(ElderTridentEntity::new));
    }

    private static <T extends Entity> EntityType<T> register(String s, EntityType<T> bombEntityType) {
        return Registry.register(Registry.ENTITY_TYPE, Impaled.MODID + ":" + s, bombEntityType);
    }

    private static <T extends Entity> EntityType<T> createEntityType(EntityType.EntityFactory<T> factory) {
        return FabricEntityTypeBuilder.create(SpawnGroup.MISC, factory).dimensions(EntityDimensions.changing(0.5f, 0.5f)).trackRangeBlocks(4).trackedUpdateRate(20).build();
    }
}