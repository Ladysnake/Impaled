package ladysnake.impaled.common.item;

import ladysnake.sincereloyalty.SincereLoyalty;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Predicate;
import java.util.List;

public class MaelstromItem extends RangedWeaponItem {
    public MaelstromItem(Item.Settings settings) {
        super(settings);
    }

    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity && world instanceof ServerWorld serverWorld) {
            var efficiencyRef = serverWorld.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.EFFICIENCY.getValue()).orElse(null);
            int efficiencyLevel = efficiencyRef != null ? EnchantmentHelper.getLevel(efficiencyRef, stack) : 0;
            ((PlayerEntity) user).getItemCooldownManager().set(Registries.ITEM.getId(this), 20 - (3 * efficiencyLevel));
        }
        return true;
    }

    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, LivingEntity target) {
        // MaelstromItem doesn't shoot projectiles in the traditional sense, so this is left empty
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        user.setCurrentHand(hand);
        return ActionResult.CONSUME;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack maelstromStack, int remainingUseTicks) {
        super.usageTick(world, user, maelstromStack, remainingUseTicks);
        int efficiencyLevel = 0;
        if (world instanceof ServerWorld serverWorld) {
            var efficiencyRef = serverWorld.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.EFFICIENCY.getValue()).orElse(null);
            efficiencyLevel = efficiencyRef != null ? EnchantmentHelper.getLevel(efficiencyRef, maelstromStack) : 0;
        }
        if (remainingUseTicks % (20 - (3 * efficiencyLevel)) == 0 && world instanceof ServerWorld serverWorld) {
            if (user instanceof PlayerEntity) {
                Inventory inventory = ((PlayerEntity) user).getInventory();
                for (int i = 0; i < inventory.size(); i++) {
                    ItemStack stackToThrow = ((PlayerEntity) user).getInventory().getStack(i);
                    var riptideRef = serverWorld.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.RIPTIDE.getValue()).orElse(null);
                    int riptideLevel = riptideRef != null ? EnchantmentHelper.getLevel(riptideRef, stackToThrow) : 0;
                    if (!stackToThrow.isEmpty() && riptideLevel == 0 && stackToThrow.isIn(SincereLoyalty.TRIDENTS)) {
                        TridentEntity trident = null;
                        PlayerEntity playerEntity = (PlayerEntity) user;
                        stackToThrow.damage(1, (LivingEntity) playerEntity, user.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
                        maelstromStack.damage(1, (LivingEntity) playerEntity, user.getActiveHand() == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);

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

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);

        // Add description tooltip using the item's translation key + ".tooltip"
        String tooltipKey = this.getTranslationKey() + ".tooltip";
        tooltip.add(Text.translatable(tooltipKey).formatted(Formatting.GRAY));
    }
}
