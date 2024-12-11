package org.ladysnake.impaled.compat;

import it.crystalnest.soul_fire_d.api.FireManager;
import net.minecraft.entity.Entity;

public final class SoulFired {
    private SoulFired() {}

    public static void setOnSoulFireFor(Entity entity, int seconds) {
        FireManager.setOnFire(entity, seconds, FireManager.SOUL_FIRE_TYPE);
    }
}
