package org.ladysnake.impaled.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public abstract class FireTridentEntity extends ImpaledTridentEntity {
    public FireTridentEntity(EntityType<? extends FireTridentEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof LivingEntity livingEntity && livingEntity.getType() != EntityType.ENDERMAN) {
            setTargetOnFireFor(livingEntity, 1);
        }
        super.onEntityHit(entityHitResult);
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
        setTargetOnFireFor(target, 8);
    }

    @Override
    public boolean isOnFire() {
        return true;
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isSubmergedInWater() && this.getWorld().isClient() && this.random.nextInt(5) == 0) {
            this.getWorld().addParticle(ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + random.nextGaussian() / 10, this.getY() + random.nextGaussian() / 10, this.getZ() + random.nextGaussian() / 10, 0, this.random.nextFloat(), 0);
        }
    }

    protected abstract void setTargetOnFireFor(LivingEntity target, int seconds);
}
