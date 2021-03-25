package ladysnake.impaled.mixin;

import ladysnake.impaled.common.item.AtlanItem;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net/minecraft/enchantment/EnchantmentTarget$11")
public class EnchantmentTargetMixin {
    @Inject(method = "isAcceptableItem", at = @At(value = "RETURN"), cancellable = true)
    public void isAcceptableItem(Item item, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && item instanceof AtlanItem) {
            cir.setReturnValue(true);
        }
    }
}
