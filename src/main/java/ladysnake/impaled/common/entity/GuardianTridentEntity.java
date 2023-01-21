package ladysnake.impaled.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuardianTridentEntity extends ElderTridentEntity {
    private int timeSinceTracking = 40;

    public GuardianTridentEntity(EntityType<? extends ElderTridentEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Consumer<ItemStack> getStackFetcher() {
        Entity owner = this.getOwner();
        if (owner != null) {
            return stack -> {
                if (owner.isAlive()) {
                    if (!(owner instanceof PlayerEntity) || !((PlayerEntity) owner).getInventory().insertStack(stack)) {
                        owner.dropStack(stack);
                    }
                } else {
                    this.dropStack(stack);
                }
            };
        }
        return super.getStackFetcher();
    }

    @Override
    public void tick() {
        if (this.hasDealtDamage()) {
            if (this.isSubmergedInWater()) {
                this.playSound(SoundEvents.ENTITY_GUARDIAN_DEATH, 1.0f, 1.0f);
            } else {
                this.playSound(SoundEvents.ENTITY_GUARDIAN_DEATH_LAND, 1.0f, 1.0f);
            }

            for (int i = 0; i < 20; i++) {
                this.world.addParticle(ParticleTypes.BUBBLE_POP, this.getX() + this.random.nextGaussian() / 10, this.getY() + this.random.nextGaussian() / 10, this.getZ() + this.random.nextGaussian() / 10, this.random.nextGaussian() / 10, Math.abs(this.random.nextGaussian() / 10), this.random.nextGaussian() / 10);
            }

            this.setNoGravity(false);
            this.remove(RemovalReason.DISCARDED);
        }

        if (this.timeSinceTracking != -1) {
            this.timeSinceTracking++;
        }

        if (timeSinceTracking >= 40) {
            Vec3d rotationVec = this.getVelocity().normalize();
            Box box = new Box(this.getX() - 1, this.getY() - 1, this.getZ() - 1, this.getX() + 1, this.getY() + 1, this.getZ() + 1).expand(96 * rotationVec.getX(), 96 * rotationVec.getY(), 96 * rotationVec.getZ());
            List<LivingEntity> possibleTargets = world.getEntitiesByClass(LivingEntity.class, box, (entity) -> entity.canHit() && entity != this.getOwner() && !(entity instanceof TameableEntity && ((TameableEntity) entity).isTamed()));
            List<LivingEntity> validTargets = new ArrayList<>();

            double max = 0.3;
            for (LivingEntity possibleTarget : possibleTargets) {
                Vec3d vecDist = possibleTarget.getPos();
                if (this.getOwner() != null) {
                    vecDist = vecDist.subtract(this.getOwner().getPos());
                } else {
                    vecDist = vecDist.subtract(this.getPos());
                }
                double dotProduct = vecDist.normalize().dotProduct(rotationVec);
                if (dotProduct > max) {
                    validTargets.add(possibleTarget);
                }
            }

            if (!validTargets.isEmpty()) {
                this.tridentTarget = validTargets.get(this.random.nextInt(validTargets.size()));
            }

            if (this.tridentTarget != null) {
                this.timeSinceTracking = -1;
            } else {
                this.timeSinceTracking = 0;
            }
        } else {
            super.tick();
        }
        this.setNoGravity(this.tridentTarget != null && tridentTarget.isAlive());
    }

    @Override
    protected void setDealtDamage() {
        this.setNoGravity(false);
        this.tridentTarget = null;
        super.setDealtDamage();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (!this.isOwner(entityHitResult.getEntity())) {
            if (!(entityHitResult.getEntity() instanceof PlayerEntity)) {
                entityHitResult.getEntity().timeUntilRegen = 0;
            }
            super.onEntityHit(entityHitResult);
        }
    }

    @Override
    public boolean collidesWith(Entity other) {
        return super.collidesWith(other) && !this.isOwner(other);
    }

    @Override
    public boolean isEnchanted() {
        return false;
    }
}
