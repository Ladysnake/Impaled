package ladysnake.impaled.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class HellforkEntity extends ImpaledTridentEntity {
    public HellforkEntity(EntityType<? extends HellforkEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().setOnFireFor(4 + this.world.getRandom().nextInt(4));
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

        if (this.isSubmergedInWater() && this.world.isClient() && this.random.nextInt(5) == 0) {
            this.world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, this.getX()+random.nextGaussian()/10, this.getY()+random.nextGaussian()/10, this.getZ()+random.nextGaussian()/10, 0, this.random.nextFloat(), 0);
        }
    }
}
