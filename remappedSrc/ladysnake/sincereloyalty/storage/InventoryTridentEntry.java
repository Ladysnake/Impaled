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
package ladysnake.sincereloyalty.storage;

import ladysnake.sincereloyalty.LoyalTrident;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class InventoryTridentEntry extends TridentEntry {
    private final UUID playerUuid;

    InventoryTridentEntry(ServerWorld world, UUID tridentUuid, UUID playerUuid) {
        super(world, tridentUuid);
        this.playerUuid = playerUuid;
    }

    InventoryTridentEntry(ServerWorld world, NbtCompound tag) {
        super(world, tag);
        this.playerUuid = tag.getUuid("player_uuid");
    }

    @Override
    public NbtCompound toNbt(NbtCompound nbt) {
        super.toNbt(nbt);
        nbt.putUuid("player_uuid", this.playerUuid);
        return nbt;
    }

    @Override
    public void preloadTrident() {
        // NO-OP players should load themselves
        // TODO maybe load fake players ?
    }

    @Override
    public TridentEntity findTrident() {
        PlayerEntity player = this.world.getPlayerByUuid(this.playerUuid);
        if (player != null) {
            for (int slot = 0; slot < player.getInventory().size(); slot++) {
                ItemStack stack = player.getInventory().getStack(slot);
                NbtCompound loyaltyData = stack.getSubNbt(LoyalTrident.MOD_NBT_KEY);
                if (loyaltyData != null && loyaltyData.containsUuid(LoyalTrident.TRIDENT_UUID_NBT_KEY)) {
                    if (loyaltyData.getUuid(LoyalTrident.TRIDENT_UUID_NBT_KEY).equals(this.tridentUuid)) {
                        TridentEntity tridentEntity = LoyalTrident.spawnTridentForStack(player, stack);
                        if (tridentEntity != null) {
                            player.getInventory().removeStack(slot);
                            return tridentEntity;
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean isHolder(PlayerEntity holder) {
        return this.playerUuid.equals(holder.getUuid());
    }
}
