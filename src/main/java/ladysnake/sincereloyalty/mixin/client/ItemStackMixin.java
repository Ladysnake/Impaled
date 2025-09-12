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
package ladysnake.sincereloyalty.mixin.client;

import ladysnake.sincereloyalty.LoyalTrident;
import ladysnake.sincereloyalty.LoyalTridentComponents;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.item.Item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Nullable
    @Unique
    private static String impaled$trueOwnerName;
    @Unique
    private static boolean impaled$riptide;
    @Unique
    private static boolean impaled$isLoyalty;

    // inject into the lambda in appendEnchantments
    @Dynamic("Lambda method")
    @Inject(method = "method_17869", at = @At("RETURN"))
    private static void editTooltip(List<Text> lines, NbtCompound enchantmentNbt, Enchantment enchantment, CallbackInfo info) {
        // Check if this is loyalty enchantment using our flag
        if (impaled$isLoyalty && impaled$trueOwnerName != null) {
            if (!lines.isEmpty()) {
                if (impaled$riptide) {
                    // If there is riptide, we present as if there was only one level possible
                    // Use a generic loyalty translation since we can't access the specific one
                    lines.set(lines.size() - 1, Text.translatable("enchantment.minecraft.loyalty").formatted(Formatting.GRAY));
                }

                MutableText line = (MutableText) lines.get(lines.size() - 1);

                line.append(Text.literal(" ")).append(Text.translatable("impaled:tooltip.owned_by", impaled$trueOwnerName).formatted(Formatting.DARK_GRAY));
            }
            impaled$trueOwnerName = null;
            impaled$isLoyalty = false;
        }
    }

    // getSubNbt method is no longer available in 1.21

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendEnchantments(Ljava/util/List;Lnet/minecraft/nbt/NbtList;)V"))
    private void captureThis(PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> cir) {
        LoyalTridentComponents.LoyalTridentData loyaltyData = ((ItemStack) (Object) this).get(LoyalTridentComponents.LOYAL_TRIDENT_DATA);
        if (loyaltyData != null) {
            impaled$trueOwnerName = loyaltyData.ownerName();
            // Check if item has loyalty enchantment
            ItemStack stack = (ItemStack) (Object) this;
            RegistryEntry<Enchantment> loyaltyRef = player.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.LOYALTY.getValue()).orElse(null);
            if (loyaltyRef != null) {
                impaled$isLoyalty = EnchantmentHelper.getLevel(loyaltyRef, stack) > 0;
            }
            // Check for riptide enchantment - simplified check for now
            RegistryEntry<Enchantment> riptideRef = player.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.RIPTIDE.getValue()).orElse(null);
            if (riptideRef != null) {
                impaled$riptide = EnchantmentHelper.getLevel(riptideRef, stack) > 0;
            }
        }
    }
}
