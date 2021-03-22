package ladysnake.impaled.common.entity;

import ladysnake.impaled.common.init.ImpaledEntityTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ElderTridentEntity extends ImpaledTridentEntity {
    Entity closestTarget;
    boolean hasSearchedTarget;
    Box box;

    public ElderTridentEntity(EntityType<? extends ImpaledTridentEntity> entityType, World world) {
        super(ImpaledEntityTypes.ELDER_TRIDENT, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.hasSearchedTarget) {
            if (this.getOwner() != null) {
                Vec3d rotationVec = this.getOwner().getRotationVector();
                box = new Box(this.getX() - 1, this.getY() - 1, this.getZ() - 1, this.getX() + 1, this.getY() + 1, this.getZ() + 1).expand(200 * rotationVec.getX(), 200 * rotationVec.getY(), 200 * rotationVec.getZ());
                List<Entity> possibleTargets = world.getEntitiesByClass(Entity.class, box, (entity) -> entity.collides() && entity != this.getOwner() && !(entity instanceof TameableEntity && ((TameableEntity) entity).isTamed()));

                double max = 0.5;
                for (Entity possibleTarget : possibleTargets) {
                    Vec3d vecDist = possibleTarget.getPos().subtract(this.getOwner().getPos());
                    double dotProduct = vecDist.normalize().dotProduct(rotationVec);
                    if (dotProduct > max) {
                        this.closestTarget = possibleTarget;
                        max = dotProduct;
                    }
                }

                this.hasSearchedTarget = true;
            }
        } else {
            if (!this.hasDealtDamage()) {
                if (this.closestTarget != null && closestTarget.isAlive()) {
                    float i = 5f;
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
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (this.world instanceof ServerWorld && this.hasChanneling() && entityHitResult.getEntity() instanceof PlayerEntity) {
            StatusEffect statusEffect = StatusEffects.MINING_FATIGUE;
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) entityHitResult.getEntity();

            if (!(serverPlayerEntity.hasStatusEffect(statusEffect) && serverPlayerEntity.getStatusEffect(statusEffect).getAmplifier() >= 2 && serverPlayerEntity.getStatusEffect(statusEffect).getDuration() >= 1200)) {
                serverPlayerEntity.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.ELDER_GUARDIAN_EFFECT, this.isSilent() ? 0.0F : 1.0F));
                serverPlayerEntity.addStatusEffect(new StatusEffectInstance(statusEffect, 6000, 2));
            }
        }
    }

    @Override
    protected float getDragInWater() {
        return 1.0f;
    }
}
