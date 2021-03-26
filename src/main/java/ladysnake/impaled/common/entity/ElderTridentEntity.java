package ladysnake.impaled.common.entity;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class ElderTridentEntity extends ImpaledTridentEntity {
    protected Entity closestTarget;
    protected boolean hasSearchedTarget;
    private final List<ItemStack> fetchedStacks = new ArrayList<>();

    public ElderTridentEntity(EntityType<? extends ElderTridentEntity> entityType, World world) {
        super(entityType, world);
    }

    public List<ItemStack> getFetchedStacks() {
        return fetchedStacks;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.inGround) {
            this.setDealtDamage(true);
        }

        if (!this.hasSearchedTarget) {
            if (this.getOwner() != null) {
                Vec3d rotationVec = this.getOwner().getRotationVector();
                Box box = new Box(this.getX() - 1, this.getY() - 1, this.getZ() - 1, this.getX() + 1, this.getY() + 1, this.getZ() + 1).expand(96 * rotationVec.getX(), 96 * rotationVec.getY(), 96 * rotationVec.getZ());
                List<Entity> possibleTargets = world.getEntitiesByClass(Entity.class, box, (entity) -> entity.collides() && entity != this.getOwner() && !(entity instanceof TameableEntity && ((TameableEntity) entity).isTamed()) && !(entity instanceof TridentEntity));

                double max = 0.3;
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
                    this.setPos(this.getX(), this.getY() + vec3d.y * 0.015D * (double)i, this.getZ());
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
    public void onPlayerCollision(PlayerEntity player) {
        super.onPlayerCollision(player);
        Entity entity = this.getOwner();
        if (entity == null || entity.getUuid() == player.getUuid()) {
            this.fetchedStacks.forEach(stack -> {
                if (!player.getInventory().insertStack(stack)) {
                    this.dropStack(stack);
                }
            });
        }
    }

    @Override
    protected float getDragInWater() {
        return 1.0f;
    }

    @Override
    public void readCustomDataFromNbt(CompoundTag tag) {
        super.readCustomDataFromNbt(tag);
        if (tag.contains("fetched_items", NbtType.LIST)) {
            ListTag fetchedItems = tag.getList("fetched_items", NbtType.COMPOUND);
            for (int i = 0; i < fetchedItems.size(); i++) {
                CompoundTag fetchedItem = fetchedItems.getCompound(i);
                this.fetchedStacks.add(ItemStack.fromNbt(fetchedItem));
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(CompoundTag tag) {
        super.writeCustomDataToNbt(tag);
        ListTag listTag = new ListTag();
        for (ItemStack fetchedStack : this.fetchedStacks) {
            listTag.add(fetchedStack.writeNbt(new CompoundTag()));
        }
        tag.put("fetched_stacks", listTag);
    }
}
