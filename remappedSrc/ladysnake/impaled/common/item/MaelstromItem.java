package ladysnake.impaled.common.item;

import ladysnake.sincereloyalty.SincereLoyalty;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

import java.util.function.Predicate;

public class MaelstromItem extends RangedWeaponItem implements Vanishable {
    public MaelstromItem(Item.Settings settings) {
        super(settings);
    }

    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity) {
            ((PlayerEntity) user).getItemCooldownManager().set(this, 20 - (3 * EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack)));
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(itemStack);
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack maelstromStack, int remainingUseTicks) {
        super.usageTick(world, user, maelstromStack, remainingUseTicks);
        if (remainingUseTicks % (20 - (3 * EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, maelstromStack))) == 0 && world instanceof ServerWorld) {
            if (user instanceof PlayerEntity) {
                Inventory inventory = ((PlayerEntity) user).getInventory();
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stackToThrow = ((PlayerEntity) user).getInventory().getStack(i);
                    if (!stackToThrow.isEmpty() && EnchantmentHelper.getRiptide(stackToThrow) == 0 && stackToThrow.isIn(SincereLoyalty.TRIDENTS)) {
                        TridentEntity trident = null;
                        PlayerEntity playerEntity = (PlayerEntity) user;
                        stackToThrow.damage(1, (LivingEntity) playerEntity, livingEntity -> livingEntity.sendToolBreakStatus(user.getActiveHand()));
                        maelstromStack.damage(1, (LivingEntity) playerEntity, livingEntity -> livingEntity.sendToolBreakStatus(user.getActiveHand()));

                        if (stackToThrow.getItem() instanceof ImpaledTridentItem) {
                            trident = ((ImpaledTridentItem) stackToThrow.getItem()).createTrident(world, user, stackToThrow);
                        } else if (stackToThrow.getItem() instanceof TridentItem) {
                            trident = new TridentEntity(world, user, stackToThrow);
                            trident.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, 2.5F, 1.0F);
                        }

                        if (trident != null) {
                            if (playerEntity.getAbilities().creativeMode) {
                                trident.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                            }

                            world.spawnEntity(trident);
                            world.playSoundFromEntity(null, playerEntity, SoundEvents.ITEM_TRIDENT_RETURN, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            if (!playerEntity.getAbilities().creativeMode) {
                                playerEntity.getInventory().removeOne(stackToThrow);
                            }

                            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                            break;
                        }
                    }
                }
            }
        }
    }

    public Predicate<ItemStack> getProjectiles() {
        return itemStack -> itemStack.isIn(SincereLoyalty.TRIDENTS);
    }

    public int getRange() {
        return 15;
    }
}
