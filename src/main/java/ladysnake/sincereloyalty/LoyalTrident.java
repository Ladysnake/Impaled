/*
 * Sincere-Loyalty
 * Copyright (C) 2020 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package ladysnake.sincereloyalty;

import ladysnake.impaled.common.init.ImpaledItems;
import ladysnake.impaled.common.item.ImpaledTridentItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.UUID;

public interface LoyalTrident {
    String MOD_NBT_KEY = SincereLoyalty.MOD_ID;
    String TRIDENT_UUID_NBT_KEY = "trident_uuid";
    String OWNER_NAME_NBT_KEY = "owner_name";
    String TRIDENT_OWNER_NBT_KEY = "trident_owner";
    String TRIDENT_SIT_NBT_KEY = MOD_NBT_KEY + ":trident_sit";
    String RETURN_SLOT_NBT_KEY = "return_slot";

    static LoyalTrident of(TridentEntity trident) {
        return ((LoyalTrident) trident);
    }

    @Nullable
    static UUID getTridentUuid(ItemStack stack) {
        NbtCompound loyaltyData = stack.getSubNbt(LoyalTrident.MOD_NBT_KEY);
        if (loyaltyData == null || !loyaltyData.containsUuid(TRIDENT_OWNER_NBT_KEY)) {
            return null;
        }
        if (!loyaltyData.containsUuid(TRIDENT_UUID_NBT_KEY)) {
            loyaltyData.putUuid(LoyalTrident.TRIDENT_UUID_NBT_KEY, UUID.randomUUID());
        }
        return loyaltyData.getUuid(TRIDENT_UUID_NBT_KEY);
    }

    static void setPreferredSlot(ItemStack tridentStack, int slot) {
        tridentStack.getOrCreateSubNbt(LoyalTrident.MOD_NBT_KEY).putInt(LoyalTrident.RETURN_SLOT_NBT_KEY, slot);
    }

    static boolean hasTrueOwner(ItemStack tridentStack) {
        if (tridentStack.isIn(SincereLoyalty.TRIDENTS) && LoyaltyBindingRecipe.isLoyalEnough(tridentStack)) {
            NbtCompound loyaltyNbt = tridentStack.getSubNbt(MOD_NBT_KEY);
            return loyaltyNbt != null && loyaltyNbt.containsUuid(TRIDENT_OWNER_NBT_KEY);
        }
        return false;
    }

    @Nullable
    static UUID getTrueOwner(ItemStack tridentStack) {
        return hasTrueOwner(tridentStack) ? Objects.requireNonNull(tridentStack.getSubNbt(MOD_NBT_KEY)).getUuid(TRIDENT_OWNER_NBT_KEY) : null;
    }

    @Nullable
    static TridentEntity spawnTridentForStack(Entity thrower, ItemStack tridentStack) {
        NbtCompound loyaltyData = tridentStack.getSubNbt(MOD_NBT_KEY);
        if (loyaltyData != null) {
            UUID ownerUuid = loyaltyData.getUuid(TRIDENT_OWNER_NBT_KEY);
            if (ownerUuid != null) {
                PlayerEntity owner = thrower.getWorld().getPlayerByUuid(ownerUuid);
                if (owner != null) {
                    TridentEntity trident;

                    // Yes it is fine to call Set<TridentItem>#contains(Item)
                    //noinspection SuspiciousMethodCalls
                    if (ImpaledItems.ALL_TRIDENTS.contains(tridentStack.getItem())) {
                        trident = ((ImpaledTridentItem) tridentStack.getItem()).createTrident(thrower.getWorld(), owner, tridentStack);
                    } else {
                        trident = new TridentEntity(thrower.getWorld(), owner, tridentStack);
                    }

                    trident.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                    trident.setVelocity(thrower.getVelocity());
                    trident.copyPositionAndRotation(thrower);
                    thrower.getWorld().spawnEntity(trident);
                    return trident;
                }
            }
        }
        return null;
    }

    UUID loyaltrident_getTridentUuid();

    void loyaltrident_sit();

    void loyaltrident_wakeUp();

    void loyaltrident_setReturnSlot(int slot);
}
