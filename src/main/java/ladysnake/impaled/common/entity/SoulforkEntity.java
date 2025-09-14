package ladysnake.impaled.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class SoulforkEntity extends ImpaledTridentEntity {
    public SoulforkEntity(EntityType<? extends SoulforkEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
        target.setOnFireFor(8);
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
}
