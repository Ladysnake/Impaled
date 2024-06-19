package org.ladysnake.impaled.common.enchantment;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import org.ladysnake.sincereloyalty.SLDataComponents;
import org.ladysnake.sincereloyalty.TridentRecaller;

import java.util.Objects;

public final class BetterLoyalty {
    public static boolean tryInsertTrident(ItemStack stack, PlayerEntity player) {
            TridentRecaller caller = (TridentRecaller) player;

            if (caller.getCurrentRecallStatus() == TridentRecaller.RecallStatus.RECALLING) {
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, player.getSoundCategory(), 0.7f, 0.5f);
            }

            caller.updateRecallStatus(TridentRecaller.RecallStatus.NONE);

            if (stack.contains(SLDataComponents.RETURN_SLOT)) {
                int preferredSlot = Objects.requireNonNull(stack.get(SLDataComponents.RETURN_SLOT));
                stack.remove(SLDataComponents.RETURN_SLOT);
                if (preferredSlot == -1) {
                    if (player.getOffHandStack().isEmpty()) {
                        player.equipStack(EquipmentSlot.OFFHAND, stack.copy());
                        stack.setCount(0);
                        return true;
                    }
                } else if (player.getInventory().getStack(preferredSlot).isEmpty()) {
                    player.getInventory().insertStack(preferredSlot, stack);
                    return true;
                }
            }
        return false;
    }
}
