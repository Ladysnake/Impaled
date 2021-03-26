package ladysnake.impaled.common.item;

import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.init.ImpaledEntityTypes;
import ladysnake.sincereloyalty.LoyalTrident;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Objects;

public class ElderTridentItem extends ImpaledTridentItem {
    public ElderTridentItem(Settings settings, EntityType<? extends ImpaledTridentEntity> entityType) {
        super(settings, entityType);
    }

    @Override
    protected boolean canRiptide(PlayerEntity playerEntity) {
        return true;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int j = EnchantmentHelper.getLevel(Enchantments.RIPTIDE, stack);
        if (j > 0) {
            for (int i = 1; i <= j; i++) {
                if (!world.isClient && user instanceof PlayerEntity) {
                    stack.damage(1, user, livingEntity -> livingEntity.sendToolBreakStatus(user.getActiveHand()));
                    ImpaledTridentEntity trident = ImpaledEntityTypes.GUARDIAN_TRIDENT.create(world);
                    trident.setTridentAttributes(world, user, stack);
                    trident.setOwner(user);
                    trident.setTridentStack(stack);
                    trident.setProperties(user, user.pitch, user.yaw, 0.0F, 25F, 10F);
                    trident.updatePosition(user.getX(), user.getEyeY() - 0.1, user.getZ());

                    if (((PlayerEntity) user).getAbilities().creativeMode) {
                        trident.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                    }

                    world.spawnEntity(trident);
                    world.playSoundFromEntity(null, trident, SoundEvents.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    if (!((PlayerEntity) user).getAbilities().creativeMode) {
                        ((PlayerEntity) user).getInventory().removeOne(stack);
                    }
                }
            }
        } else {
            super.onStoppedUsing(stack, world, user, remainingUseTicks);
        }
    }
}
