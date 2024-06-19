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

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.ladysnake.sincereloyalty.LoyalTrident;
import org.ladysnake.sincereloyalty.SLDataComponents;
import org.ladysnake.sincereloyalty.storage.LoyalTridentStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;
import java.util.UUID;

@Mixin(Item.class)
public abstract class ItemMixin {
    @Inject(method = "inventoryTick", at = @At("RETURN"))
    private void updateTridentInInventory(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (entity.age % 10 == 0 && !entity.getWorld().isClient && entity instanceof PlayerEntity) {
            // Update from nbt
            if (stack.contains(DataComponentTypes.CUSTOM_DATA)) {
                NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
                if (nbtComponent.contains(LoyalTrident.MOD_NBT_KEY)) {
                    NbtCompound slData = nbtComponent.getNbt().getCompound(LoyalTrident.MOD_NBT_KEY);
                    if (slData.contains(LoyalTrident.TRIDENT_UUID_NBT_KEY)) {
                        stack.set(SLDataComponents.TRIDENT_UUID, slData.getUuid(LoyalTrident.TRIDENT_UUID_NBT_KEY));
                    }
                    if (slData.contains(LoyalTrident.OWNER_NAME_NBT_KEY)) {
                        stack.set(SLDataComponents.OWNER_NAME, slData.getString(LoyalTrident.OWNER_NAME_NBT_KEY));
                    }
                    if (slData.contains(LoyalTrident.TRIDENT_OWNER_NBT_KEY)) {
                        stack.set(SLDataComponents.TRIDENT_OWNER, Objects.requireNonNullElse(slData.getUuid(LoyalTrident.TRIDENT_OWNER_NBT_KEY), UUID.randomUUID()));
                    }
                    if (slData.contains(LoyalTrident.TRIDENT_SIT_NBT_KEY)) {
                        stack.set(SLDataComponents.TRIDENT_SIT, slData.getBoolean(LoyalTrident.TRIDENT_SIT_NBT_KEY));
                    }
                    if (slData.contains(LoyalTrident.RETURN_SLOT_NBT_KEY)) {
                        stack.set(SLDataComponents.RETURN_SLOT, slData.getInt(LoyalTrident.RETURN_SLOT_NBT_KEY));
                    }
                }
                nbtComponent.getNbt().remove(LoyalTrident.MOD_NBT_KEY);
                stack.set(DataComponentTypes.CUSTOM_DATA, nbtComponent);
            }

            UUID trueOwner = LoyalTrident.getTrueOwner(stack);
            if (Objects.equals(trueOwner, entity.getUuid())) {
                if (!Objects.equals(entity.getName().getString(), stack.get(SLDataComponents.OWNER_NAME))) {
                    stack.set(SLDataComponents.OWNER_NAME, entity.getName().getString());
                }
            } else if (trueOwner != null) {
                LoyalTridentStorage.get((ServerWorld) world).memorizeTrident(trueOwner, LoyalTrident.getTridentUuid(stack), (PlayerEntity) entity);
            }
        }
    }
}
