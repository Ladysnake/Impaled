package ladysnake.impaled.common.entity;

import ladysnake.impaled.common.init.ImpaledEntityTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ElderTridentEntity extends ImpaledTridentEntity {
    LivingEntity closestTarget;
    boolean hasSearchedTarget;

    public ElderTridentEntity(EntityType<? extends ImpaledTridentEntity> entityType, World world) {
        super(ImpaledEntityTypes.ELDER_TRIDENT, world);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoClip(true);

        if (!this.hasSearchedTarget) {
            Vec3d rotationVec = this.getRotationVector();
            Box box = new Box(this.getX()-1, this.getY()-1, this.getZ()-1, this.getX()+1, this.getY()+1, this.getZ()+1).expand(200*rotationVec.getX(), 200*rotationVec.getY(), 200*rotationVec.getZ());
            List<LivingEntity> possibleTargets = world.getEntitiesByClass(LivingEntity.class, box, (livingEntity1) -> livingEntity1.isAlive() && livingEntity1 != this.getOwner());
            if (!possibleTargets.isEmpty()) {
                closestTarget = Collections.min(possibleTargets, Comparator.comparing(livingEntity -> {
                    Vec3d vecDist = livingEntity.getPos().subtract(this.getOwner().getPos());
                    return vecDist.normalize().dotProduct(rotationVec);
                }));
            }
            this.hasSearchedTarget = true;
        } else {
            if (!this.hasDealtDamage()) {
                if (this.closestTarget != null && closestTarget.isAlive()) {
                    float i = 3f;
                    Vec3d vec3d = new Vec3d(closestTarget.getX() - this.getX(), closestTarget.getEyeY() - this.getY(), closestTarget.getZ() - this.getZ());
                    if (this.world.isClient) {
                        this.lastRenderY = this.getY();
                    }

                    double d = 0.05D * (double) i;
                    this.setVelocity(this.getVelocity().multiply(0.95D).add(vec3d.normalize().multiply(d)));
                }
            }
        }
    }

    @Override
    protected float getDragInWater() {
        return 1.0f;
    }
}
