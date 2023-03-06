package ladysnake.impaled.common.item;

import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

public class ElderTridentItem extends ImpaledTridentItem {
    public ElderTridentItem(Settings settings, EntityType<? extends ImpaledTridentEntity> entityType) {
        super(settings, entityType);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);

        int j = EnchantmentHelper.getLevel(Enchantments.RIPTIDE, stack);
        int useTime = this.getMaxUseTime(stack) - remainingUseTicks;
        if (useTime >= 10 && j > 0) {
            for (int i = 1; i <= j; i++) {
                if (!world.isClient && user instanceof PlayerEntity player) {
                    ImpaledTridentEntity trident = ImpaledEntityTypes.GUARDIAN_TRIDENT.create(world);
                    if (trident == null) {
                        continue;
                    }
                    trident.setTridentAttributes(stack);
                    trident.setOwner(user);
                    trident.setTridentStack(stack);
                    trident.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                    trident.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 0.25F, 3.5F);
                    trident.updatePosition(user.getX() + user.getRandom().nextGaussian(), user.getEyeY() + user.getRandom().nextGaussian(), user.getZ() + user.getRandom().nextGaussian());
                    trident.addVelocity(user.getRandom().nextGaussian() / 10, 0, user.getRandom().nextGaussian() / 10);
                    if (player.getAbilities().creativeMode) {
                        trident.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    }
                    world.spawnEntity(trident);
                    if (user.isSubmergedInWater()) {
                        world.playSoundFromEntity(null, trident, SoundEvents.ENTITY_GUARDIAN_AMBIENT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    } else {
                        world.playSoundFromEntity(null, trident, SoundEvents.ENTITY_GUARDIAN_AMBIENT_LAND, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }
                }
            }
        }
    }
}
