package ladysnake.impaled.common.entity;

import ladysnake.impaled.mixin.TridentEntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import java.lang.reflect.Field;

public class ImpaledTridentEntity extends TridentEntity {
    public ImpaledTridentEntity(EntityType<? extends ImpaledTridentEntity> entityType, World world) {
        super(entityType, world);
    }

    public ImpaledTridentEntity(EntityType<? extends ImpaledTridentEntity> entityType, World world, LivingEntity owner, ItemStack stack) {
        super(entityType, world);
        this.setOwner(owner);

        // Set position like vanilla TridentEntity
        this.setPosition(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());

        // Set rotation to match thrower's orientation - THIS IS KEY FOR PROPER PHYSICS
        this.setRotation(owner.getYaw(), owner.getPitch());

        // Set the trident attributes after basic initialization
        this.setTridentAttributes(stack);
    }

    public void setTridentAttributes(ItemStack stack) {
        this.setTridentStack(stack.copy());
        // Get loyalty level using the new enchantment system
        byte loyaltyLevel = 0;
        if (!this.getWorld().isClient()) {
            var loyaltyEnchantment = this.getWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getEntry(Enchantments.LOYALTY.getValue());
            if (loyaltyEnchantment.isPresent()) {
                loyaltyLevel = (byte) EnchantmentHelper.getLevel(loyaltyEnchantment.get(), stack);
            }
        }
        this.dataTracker.set(TridentEntityAccessor.impaled$getLoyalty(), loyaltyLevel);
        this.dataTracker.set(TridentEntityAccessor.impaled$getEnchanted(), stack.hasGlint());
    }

    protected float getDragInWater() {
        return 0.99f;
    }

    public void setTridentStack(ItemStack tridentStack) {
        // Use reflection to set the ItemStack field since the field name changed in 1.21.3
        try {
            Field[] fields = TridentEntity.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType() == ItemStack.class) {
                    field.setAccessible(true);
                    field.set(this, tridentStack);
                    break;
                }
            }
        } catch (Exception e) {
            // If reflection fails, just store enchantments in DataTracker
            System.err.println("Could not set trident ItemStack via reflection: " + e.getMessage());
        }
    }

    protected void setDealtDamage() {
        ((TridentEntityAccessor) this).impaled$setDealtDamage(true);
    }

    protected boolean hasDealtDamage() {
        return ((TridentEntityAccessor) this).impaled$hasDealtDamage();
    }
}
