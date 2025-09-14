package ladysnake.impaled.common.enchantment;

import ladysnake.sincereloyalty.LoyalTrident;
import ladysnake.sincereloyalty.LoyalTridentComponents;
import ladysnake.sincereloyalty.TridentRecaller;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;

public final class BetterLoyalty {
    public static boolean tryInsertTrident(ItemStack stack, PlayerEntity player) {
        LoyalTridentComponents.LoyalTridentData data = stack.get(LoyalTridentComponents.LOYAL_TRIDENT_DATA);
        if (data != null) {
            TridentRecaller caller = (TridentRecaller) player;

            if (caller.getCurrentRecallStatus() == TridentRecaller.RecallStatus.RECALLING) {
                player.getWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_TRIDENT_RETURN, player.getSoundCategory(), 0.7f, 0.5f);
            }

            caller.updateRecallStatus(TridentRecaller.RecallStatus.NONE);

            if (data.returnSlot().isPresent()) {
                int preferredSlot = data.returnSlot().get();
                // Remove the return slot by updating the component
                LoyalTridentComponents.LoyalTridentData newData = new LoyalTridentComponents.LoyalTridentData(
                    data.tridentUuid(), data.ownerName(), data.tridentOwner(), java.util.Optional.empty()
                );
                stack.set(LoyalTridentComponents.LOYAL_TRIDENT_DATA, newData);
                
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
