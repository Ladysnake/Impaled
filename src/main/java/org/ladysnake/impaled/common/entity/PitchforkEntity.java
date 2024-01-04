package org.ladysnake.impaled.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class PitchforkEntity extends ImpaledTridentEntity {
    public PitchforkEntity(EntityType<? extends PitchforkEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected float getDragInWater() {
        return 0.6F;
    }
}
