package ladysnake.impaled.common.item;

import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class HellforkItem extends ImpaledTridentItem {
    public HellforkItem(Settings settings, EntityType<ImpaledTridentEntity> entityType) {
        super(settings, entityType);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setOnFireFor(4 + attacker.getRandom().nextInt(4));
        return super.postHit(stack, target, attacker);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity) user;
            int i = this.getMaxUseTime(stack) - remainingUseTicks;
            if (i >= 10) {
                int j = EnchantmentHelper.getRiptide(stack);
                if (j <= 0 || playerEntity.isInLava() || playerEntity.isOnFire()) {
                    if (!world.isClient) {
                        stack.damage(1, (LivingEntity) playerEntity, livingEntity -> livingEntity.sendToolBreakStatus(user.getActiveHand()));
                        if (j == 0) {
                            ImpaledTridentEntity impaledTridentEntity = this.type.create(world);
                            impaledTridentEntity.setTridentAttributes(world, playerEntity, stack);
                            impaledTridentEntity.setOwner(playerEntity);
                            impaledTridentEntity.setTridentStack(stack);
                            impaledTridentEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, 2.5F + (float) j * 0.5F, 1.0F);
                            impaledTridentEntity.updatePosition(user.getX(), user.getEyeY() - 0.10000000149011612D, user.getZ());
                            if (playerEntity.getAbilities().creativeMode) {
                                impaledTridentEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                            }

                            world.spawnEntity(impaledTridentEntity);
                            world.playSoundFromEntity((PlayerEntity) null, impaledTridentEntity, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            if (!playerEntity.getAbilities().creativeMode) {
                                playerEntity.getInventory().removeOne(stack);
                            }
                        }
                    }

                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                    if (j > 0) {
                        float f = playerEntity.yaw;
                        float g = playerEntity.pitch;
                        float h = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                        float k = -MathHelper.sin(g * 0.017453292F);
                        float l = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(g * 0.017453292F);
                        float m = MathHelper.sqrt(h * h + k * k + l * l);
                        float n = 3.0F * ((1.0F + (float) j) / 4.0F);
                        h *= n / m;
                        k *= n / m;
                        l *= n / m;
                        playerEntity.addVelocity((double) h, (double) k, (double) l);
                        playerEntity.setRiptideTicks(20);
                        if (playerEntity.isOnGround()) {
                            float o = 1.1999999F;
                            playerEntity.move(MovementType.SELF, new Vec3d(0.0D, 1.1999999284744263D, 0.0D));
                        }

                        SoundEvent soundEvent3;
                        if (j >= 3) {
                            soundEvent3 = SoundEvents.ITEM_TRIDENT_RIPTIDE_3;
                        } else if (j == 2) {
                            soundEvent3 = SoundEvents.ITEM_TRIDENT_RIPTIDE_2;
                        } else {
                            soundEvent3 = SoundEvents.ITEM_TRIDENT_RIPTIDE_1;
                        }

                        world.playSoundFromEntity((PlayerEntity) null, playerEntity, soundEvent3, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        } else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !user.isInLava() && !user.isOnFire()) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (entity instanceof LivingEntity) {
            if (((LivingEntity) entity).getMainHandStack().isOf(ImpaledItems.HELLFORK) && EnchantmentHelper.getLevel(Impaled.KINDLING_CURSE, ((LivingEntity) entity).getMainHandStack()) > 0 || ((LivingEntity) entity).getOffHandStack().isOf(ImpaledItems.HELLFORK) && EnchantmentHelper.getLevel(Impaled.KINDLING_CURSE, ((LivingEntity) entity).getOffHandStack()) > 0) {
                if (EnchantmentHelper.getLevel(Impaled.KINDLING_CURSE, stack) > 0) {
                    entity.setOnFireFor(1);
                }
            }
        }
    }
}
