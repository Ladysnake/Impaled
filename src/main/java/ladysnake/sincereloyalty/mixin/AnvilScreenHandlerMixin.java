package ladysnake.sincereloyalty.mixin;

import ladysnake.sincereloyalty.LoyalTrident;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    private boolean impaled$checkingRiptideCompat;

    public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

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
        impaled$checkingRiptideCompat = checkedEnchantment == Enchantments.RIPTIDE;
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
        if (baseEnchant == Enchantments.LOYALTY && impaled$checkingRiptideCompat) {
            if (LoyalTrident.hasTrueOwner(this.input.getStack(0))) {
                // If enchantment1 == enchantment2, they are automatically considered compatible
                return Enchantments.RIPTIDE;
            }
        }
        return baseEnchant;
    }
}
