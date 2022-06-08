package ladysnake.impaled.common.enchantment;

import ladysnake.sincereloyalty.LoyalTrident;
import ladysnake.sincereloyalty.TridentRecaller;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;

public final class BetterLoyalty {
    public static boolean tryInsertTrident(ItemStack stack, PlayerEntity player) {
        NbtCompound tag = stack.getSubNbt(LoyalTrident.MOD_NBT_KEY);
        if (tag != null) {
            TridentRecaller caller = (TridentRecaller) player;

            if (caller.getCurrentRecallStatus() == TridentRecaller.RecallStatus.RECALLING) {
                player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, player.getSoundCategory(), 0.7f, 0.5f);
            }

            caller.updateRecallStatus(TridentRecaller.RecallStatus.NONE);

            if (tag.contains(LoyalTrident.RETURN_SLOT_NBT_KEY)) {
                int preferredSlot = tag.getInt(LoyalTrident.RETURN_SLOT_NBT_KEY);
                tag.remove(LoyalTrident.RETURN_SLOT_NBT_KEY);
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
        }
        return false;
    }
}
