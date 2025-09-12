package ladysnake.sincereloyalty.mixin;

import ladysnake.sincereloyalty.LoyalTrident;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.ForgingSlotsManager;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    private boolean impaled$checkingRiptideCompat;
    private Enchantment impaled$currentEnchantment;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, ForgingSlotsManager forgingSlotsManager) {
        super(type, syncId, playerInventory, context, forgingSlotsManager);
    }

    // This method should be implemented by the actual AnvilScreenHandler, but we need to declare it for compilation
    protected abstract ForgingSlotsManager getForgingSlotsManager();

    @ModifyVariable(
            method = "updateResult",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;canCombine(Lnet/minecraft/enchantment/Enchantment;)Z")
            ),
            at = @At(value = "INVOKE", target = "Ljava/util/Iterator;next()Ljava/lang/Object;"),
            ordinal = 0
    )
    private Enchantment captureSecondStackEnchant(Enchantment checkedEnchantment) {
        // Check if this is riptide enchantment using registry comparison
        impaled$currentEnchantment = checkedEnchantment;
        RegistryEntry<Enchantment> riptideRef = this.player.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.RIPTIDE.getValue()).orElse(null);
        if (riptideRef != null) {
            impaled$checkingRiptideCompat = riptideRef.value().equals(checkedEnchantment);
        }
        return checkedEnchantment;
    }

    @ModifyVariable(
            method = "updateResult",
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Ljava/util/Set;iterator()Ljava/util/Iterator;", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;canCombine(Lnet/minecraft/enchantment/Enchantment;)Z")
            ),
            at = @At("STORE"),
            ordinal = 1
    )
    private Enchantment allowRiptideLoyalty(Enchantment baseEnchant) {
        // Check if this is loyalty enchantment using registry comparison
        RegistryEntry<Enchantment> loyaltyRef = this.player.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.LOYALTY.getValue()).orElse(null);
        boolean isLoyalty = loyaltyRef != null && loyaltyRef.value().equals(baseEnchant);
        
        if (isLoyalty && impaled$checkingRiptideCompat) {
            if (LoyalTrident.hasTrueOwner(this.input.getStack(0))) {
                // Return the actual riptide enchantment, not the registry key
                RegistryEntry<Enchantment> riptideRef = this.player.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.RIPTIDE.getValue()).orElse(null);
                if (riptideRef != null) {
                    return riptideRef.value();
                }
            }
        }
        return baseEnchant;
    }
}
