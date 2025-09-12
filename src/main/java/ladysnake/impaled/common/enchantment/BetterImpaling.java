package ladysnake.impaled.common.enchantment;

import ladysnake.impaled.common.item.HellforkItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;

public final class BetterImpaling {
    public static float getAttackDamage(ItemStack stack, Entity target, RegistryWrapper.WrapperLookup registryLookup) {
        // Get impaling enchantment from registry
        var impalingEnchantment = registryLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOptional(Enchantments.IMPALING);
        int impalingLevel = 0;
        if (impalingEnchantment.isPresent()) {
            impalingLevel = EnchantmentHelper.getLevel(impalingEnchantment.get(), stack);
        }

        if (impalingLevel > 0) {
            if (stack.getItem() instanceof HellforkItem) {
                if (isFireImmune(target)) {
                    return impalingLevel * 2F;
                }
            } else if (target.isWet()) {
                return impalingLevel * 1.5F;
            }
        }

        return 0;
    }

    // Backward compatibility method - simplified version
    public static float getAttackDamage(ItemStack stack, Entity target) {
        // TODO: Implement proper enchantment checking when registry access is available
        // For now, return 0 to prevent compilation errors
        return 0;
    }

    private static boolean isFireImmune(Entity target) {
        if (target.isFireImmune()) return true;
        if (!(target instanceof LivingEntity)) return false;
        return ((LivingEntity) target).hasStatusEffect(StatusEffects.FIRE_RESISTANCE);
    }
}
