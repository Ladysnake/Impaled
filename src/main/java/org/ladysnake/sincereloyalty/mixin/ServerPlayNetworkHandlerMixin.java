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
package org.ladysnake.sincereloyalty.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.ladysnake.sincereloyalty.SLDataComponents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @ModifyVariable(method = "onCreativeInventoryAction", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private ItemStack removeTridentUuid(ItemStack copiedStack) {
        if (copiedStack.contains(SLDataComponents.TRIDENT_UUID)) {
            copiedStack.remove(SLDataComponents.TRIDENT_UUID);  // prevent stupid copies of the exact same trident
        }
        return copiedStack;
    }
}
