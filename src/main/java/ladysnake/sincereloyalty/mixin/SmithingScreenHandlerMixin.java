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
import ladysnake.sincereloyalty.SincereLoyalty;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SmithingScreenHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method = "canTakeOutput", at = @At("RETURN"), cancellable = true)
    private void canTakeResult(PlayerEntity playerEntity, boolean resultNonEmpty, CallbackInfoReturnable<Boolean> cir) {
        if (resultNonEmpty && !cir.getReturnValueZ()) {
            ItemStack item = this.input.getStack(0);
            ItemStack upgradeItem = this.input.getStack(1);
            cir.setReturnValue(SincereLoyalty.TRIDENTS.contains(item.getItem()) && SincereLoyalty.LOYALTY_CATALYSTS.contains(upgradeItem.getItem()));
        }
    }

    @ModifyArg(
        method = "updateResult",
        slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/item/ItemStack;EMPTY:Lnet/minecraft/item/ItemStack;")),
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/inventory/CraftingResultInventory;setStack(ILnet/minecraft/item/ItemStack;)V"
        )
    )
    private ItemStack updateResult(ItemStack initialResult) {
        if (initialResult.isEmpty()) {
            ItemStack item = this.input.getStack(0);
            ItemStack upgradeItem = this.input.getStack(1);
            if (SincereLoyalty.TRIDENTS.contains(item.getItem()) && SincereLoyalty.LOYALTY_CATALYSTS.contains(upgradeItem.getItem())) {
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(item);
                if (enchantments.getOrDefault(Enchantments.LOYALTY, 0) == Enchantments.LOYALTY.getMaxLevel()) {
                    ItemStack result = item.copy();
                    // we can mutate the map as it is recreated with every call to getEnchantments
                    enchantments.put(Enchantments.LOYALTY, Enchantments.LOYALTY.getMaxLevel() + 1);
                    EnchantmentHelper.set(enchantments, result);
                    NbtCompound loyaltyData = result.getOrCreateSubTag(LoyalTrident.MOD_NBT_KEY);
                    loyaltyData.putUuid(LoyalTrident.TRIDENT_OWNER_NBT_KEY, this.player.getUuid());
                    loyaltyData.putString(LoyalTrident.OWNER_NAME_NBT_KEY, this.player.getEntityName());
                    return result;
                }
            }
        }
        return initialResult;
    }
}
