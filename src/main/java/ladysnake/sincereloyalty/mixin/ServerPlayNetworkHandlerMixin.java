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
package ladysnake.sincereloyalty.mixin;

import ladysnake.sincereloyalty.LoyalTrident;
import ladysnake.sincereloyalty.LoyalTridentComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @ModifyVariable(method = "onCreativeInventoryAction", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/network/packet/c2s/play/CreativeInventoryActionC2SPacket;getItemStack()Lnet/minecraft/item/ItemStack;"))
    private ItemStack removeTridentUuid(ItemStack copiedStack) {
        LoyalTridentComponents.LoyalTridentData data = copiedStack.get(LoyalTridentComponents.LOYAL_TRIDENT_DATA);
        if (data != null) {
            // prevent stupid copies of the exact same trident - create new UUID
            LoyalTridentComponents.LoyalTridentData newData = new LoyalTridentComponents.LoyalTridentData(
                java.util.UUID.randomUUID(), data.ownerName(), data.tridentOwner(), data.returnSlot()
            );
            copiedStack.set(LoyalTridentComponents.LOYAL_TRIDENT_DATA, newData);
        }
        return copiedStack;
    }
}
