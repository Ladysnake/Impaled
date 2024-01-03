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
package ladysnake.impaled.compat;

import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import moriyashiine.enchancement.client.util.EnchancementClientUtil;
import moriyashiine.enchancement.common.ModConfig;
import moriyashiine.enchancement.common.component.entity.LeechComponent;
import moriyashiine.enchancement.common.component.entity.WarpComponent;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public final class EnchancementCompat {
    public static final boolean enabled = FabricLoader.getInstance().isModLoaded("enchancement");

    public static void tryEnableEnchantments(ImpaledTridentEntity trident, LivingEntity user, ItemStack stack) {
        if (enabled) {
            LeechComponent.maybeSet(user, stack, trident);
            WarpComponent.maybeSet(user, stack, trident);
        }
    }

    public static boolean tryRenderLeechTrident(TridentEntity trident, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Model model, Identifier texture, int light, Runnable runnable) {
        if (enabled) {
            CallbackInfo ci = new CallbackInfo("render", true); // funni mixin-based API
            EnchancementClientUtil.renderLeechTrident(trident, matrices, vertexConsumers, model, texture, light, runnable, ci);
            return ci.isCancelled();
        }
        return false;
    }

    public static boolean areTridentsLoyal() {
        if (enabled) {
            return ModConfig.allTridentsHaveLoyalty && !EnchancementUtil.isEnchantmentAllowed(Enchantments.LOYALTY);
        }
        return false;
    }
}
