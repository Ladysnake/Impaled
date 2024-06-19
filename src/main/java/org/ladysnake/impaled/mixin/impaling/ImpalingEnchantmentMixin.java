package org.ladysnake.impaled.mixin.impaling;

import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;

@Mixin(Enchantments.class)
public class ImpalingEnchantmentMixin {
    @ModifyArg(method = "<clinit>", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/enchantment/DamageEnchantment;<init>(Lnet/minecraft/enchantment/Enchantment$Properties;Ljava/util/Optional;)V",
            ordinal = 3
    ), index = 1)
    private static Optional<TagKey<EntityType<?>>> createImpaling(Optional<TagKey<EntityType<?>>> applicableEntities) {
        return Optional.empty();
    }
}
