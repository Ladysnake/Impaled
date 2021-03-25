package ladysnake.impaled.common.item;

import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class HellforkItem extends ImpaledTridentItem {
    public HellforkItem(Settings settings, EntityType<? extends ImpaledTridentEntity> entityType) {
        super(settings, entityType);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setOnFireFor(4 + attacker.getRandom().nextInt(4));
        return super.postHit(stack, target, attacker);
    }

    @Override
    protected boolean canRiptide(PlayerEntity playerEntity) {
        return playerEntity.isInLava() || playerEntity.isOnFire();
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
