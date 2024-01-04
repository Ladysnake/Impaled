package org.ladysnake.impaled.common.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.ladysnake.impaled.common.Impaled;
import org.ladysnake.impaled.common.entity.ElderTridentEntity;
import org.ladysnake.impaled.common.entity.GuardianTridentEntity;
import org.ladysnake.impaled.common.entity.HellforkEntity;
import org.ladysnake.impaled.common.entity.ImpaledTridentEntity;
import org.ladysnake.impaled.common.entity.PitchforkEntity;
import org.ladysnake.impaled.common.entity.SoulforkEntity;

public class ImpaledEntityTypes {
    public static EntityType<PitchforkEntity> PITCHFORK;
    public static EntityType<HellforkEntity> HELLFORK;
    public static EntityType<SoulforkEntity> SOULFORK;
    public static EntityType<ElderTridentEntity> ELDER_TRIDENT;
    public static EntityType<ElderTridentEntity> GUARDIAN_TRIDENT;
    public static EntityType<ImpaledTridentEntity> ATLAN;

    public static void init() {
        PITCHFORK = register("pitchfork", createEntityType(PitchforkEntity::new));
        HELLFORK = register("hellfork", createEntityType(HellforkEntity::new));
        SOULFORK = register("soulfork", createEntityType(SoulforkEntity::new));
        ELDER_TRIDENT = register("elder_trident", createDynamicEntityType(ElderTridentEntity::new));
        GUARDIAN_TRIDENT = register("guardian_trident", createDynamicEntityType(GuardianTridentEntity::new));
        ATLAN = register("atlan", createEntityType(ImpaledTridentEntity::new));
    }

    private static <T extends Entity> EntityType<T> register(String s, EntityType<T> bombEntityType) {
        return Registry.register(Registries.ENTITY_TYPE, Impaled.MODID + ":" + s, bombEntityType);
    }

    private static <T extends Entity> EntityType<T> createEntityType(EntityType.EntityFactory<T> factory) {
        return FabricEntityTypeBuilder.create(SpawnGroup.MISC, factory).dimensions(EntityDimensions.changing(0.5f, 0.5f)).trackRangeBlocks(4).trackedUpdateRate(20).build();
    }

    private static <T extends Entity> EntityType<T> createDynamicEntityType(EntityType.EntityFactory<T> factory) {
        return FabricEntityTypeBuilder.create(SpawnGroup.MISC, factory).dimensions(EntityDimensions.changing(0.5f, 0.5f)).trackRangeBlocks(4).build();
    }
}
