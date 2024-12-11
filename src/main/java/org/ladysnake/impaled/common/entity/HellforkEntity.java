package org.ladysnake.impaled.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public class HellforkEntity extends FireTridentEntity {
    public HellforkEntity(EntityType<? extends HellforkEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void setTargetOnFireFor(LivingEntity target, int seconds) {
        target.setOnFireFor(seconds);
    }
}
