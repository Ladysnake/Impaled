package org.ladysnake.sincereloyalty.mixin.client;

import net.minecraft.client.item.TooltipType;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.ladysnake.sincereloyalty.SLDataComponents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {

    @Shadow @Final ComponentMapImpl components;

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/DataComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/client/item/TooltipType;)V", ordinal = 1))
    private void captureThis(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        if (this.contains(SLDataComponents.OWNER_NAME) && this.contains(DataComponentTypes.ENCHANTMENTS)) {
            ItemEnchantmentsComponent enchants = Objects.requireNonNull(this.get(DataComponentTypes.ENCHANTMENTS));
            enchants.impaled$setTrueOwner(this.get(SLDataComponents.OWNER_NAME));
            enchants.impaled$setRiptide(EnchantmentHelper.getRiptide((ItemStack) (Object) this) > 0);
            // No IntelliJ, we are not returning anything.
            //noinspection UnreachableCode
            this.components.set(DataComponentTypes.ENCHANTMENTS, enchants);
        }
    }
}
