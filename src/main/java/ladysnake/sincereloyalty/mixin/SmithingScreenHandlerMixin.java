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
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.ForgingSlotsManager;
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
    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    // This method should be implemented by the actual SmithingScreenHandler, but we need to declare it for compilation
    protected abstract ForgingSlotsManager getForgingSlotsManager();

    @Inject(method = "canTakeOutput", at = @At("RETURN"), cancellable = true)
    private void canTakeResult(PlayerEntity playerEntity, boolean resultNonEmpty, CallbackInfoReturnable<Boolean> cir) {
        if (resultNonEmpty && !cir.getReturnValueZ()) {
            ItemStack item = this.input.getStack(0);
            ItemStack upgradeItem = this.input.getStack(1);
            cir.setReturnValue(item.isIn(SincereLoyalty.TRIDENTS) && upgradeItem.isIn(SincereLoyalty.LOYALTY_CATALYSTS));
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
            if (item.isIn(SincereLoyalty.TRIDENTS) && upgradeItem.isIn(SincereLoyalty.LOYALTY_CATALYSTS)) {
                // Get current loyalty level
                // Access registry through context world instead of player.getServerWorld()
                if (this.player.getWorld() instanceof ServerWorld serverWorld) {
                    // Get the registry entry using the key's ID
                    final RegistryEntry<Enchantment> loyaltyEnchantment = serverWorld.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.LOYALTY.getValue()).orElse(null);
                    if (loyaltyEnchantment != null) {
                        int currentLevel = EnchantmentHelper.getLevel(loyaltyEnchantment, item);
                        // Check if at max level (3)
                        if (currentLevel == 3) {
                            ItemStack result = item.copy();
                            // Apply level 4 loyalty
                            EnchantmentHelper.apply(result, builder -> {
                                builder.set(loyaltyEnchantment, 4);
                            });
                            // Set trident owner using component system
                            LoyalTrident.setTridentOwner(result, this.player.getUuid(), this.player.getName().getString());
                            return result;
                        }
                    }
                }
            }
        }
        return initialResult;
    }
}
