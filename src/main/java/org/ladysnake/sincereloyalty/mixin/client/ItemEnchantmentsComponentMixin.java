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
package org.ladysnake.sincereloyalty.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.item.TooltipType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.impaled.compat.EnchancementCompat;
import org.ladysnake.sincereloyalty.ExtendedEnchantmentsComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialib.util.interfaces.MText;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemEnchantmentsComponent.class)
public abstract class ItemEnchantmentsComponentMixin implements ExtendedEnchantmentsComponent {

    @Nullable
    @Unique
    private static String impaled$trueOwnerName;
    @Unique
    private static boolean impaled$riptide;

    @Shadow @Final boolean showInTooltip;

    @WrapOperation(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getName(I)Lnet/minecraft/text/Text;", ordinal = 1))
    private Text editTooltip(Enchantment enchantment, int level, Operation<Text> original) {
        if (enchantment == Enchantments.LOYALTY && impaled$trueOwnerName != null) {
            Text enchantText = original.call(enchantment, level);
            if (impaled$riptide) {
                // If there is riptide, we present as if there was only one level possible
                enchantText = Text.translatable(enchantment.getTranslationKey()).formatted(Formatting.GRAY);
            }

            enchantText = MText.repack(List.of(
                    enchantText,
                    Text.literal(" "),
                    Text.translatable("impaled:tooltip.owned_by", impaled$trueOwnerName).formatted(Formatting.DARK_GRAY)
            ));

            impaled$trueOwnerName = null;
            return enchantText;
        }
        return original.call(enchantment, level);
    }

    @Inject(method = "appendTooltip", at = @At("RETURN"))
    private void appendEnchancementLoyalty(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type, CallbackInfo ci) {
        if (this.showInTooltip && EnchancementCompat.areTridentsLoyal() && impaled$trueOwnerName != null) {
            tooltip.accept(Text.translatable("impaled:tooltip.owned_by_full", impaled$trueOwnerName).formatted(Formatting.GOLD));
            impaled$trueOwnerName = null;
        }
    }

    public boolean impaled$hasRiptide() { return impaled$riptide; }

    public void impaled$setRiptide(boolean riptide) { impaled$riptide = riptide; }

    public String impaled$trueOwnerName() { return impaled$trueOwnerName; }

    public void impaled$setTrueOwner(String owner) { impaled$trueOwnerName = owner; }
}
