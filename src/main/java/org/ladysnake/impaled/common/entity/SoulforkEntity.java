package org.ladysnake.impaled.common.entity;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.ladysnake.impaled.compat.SoulFired;

public class SoulforkEntity extends FireTridentEntity {
    public SoulforkEntity(EntityType<? extends SoulforkEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void setTargetOnFireFor(LivingEntity target, int seconds) {
        if (FabricLoader.getInstance().isModLoaded("soul_fire_d")) {
            SoulFired.setOnSoulFireFor(target, seconds);
        } else {
            target.setOnFireFor(seconds);
        }
    }
}
