package org.ladysnake.impaled.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.ladysnake.impaled.common.item.MaelstromItem;
import net.minecraft.enchantment.EfficiencyEnchantment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(EfficiencyEnchantment.class)
public class EfficiencyEnchantmentMixin {

    @WrapOperation(method = "isAcceptableItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"))
    private boolean maelstromAccept(EfficiencyEnchantment instance, ItemStack stack, Operation<Boolean> original){
        return original.call(instance, stack) || stack.getItem() instanceof MaelstromItem;
    }
}
