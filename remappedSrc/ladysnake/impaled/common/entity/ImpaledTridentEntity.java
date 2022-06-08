package ladysnake.impaled.common.entity;

import ladysnake.impaled.mixin.TridentEntityAccessor;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ImpaledTridentEntity extends TridentEntity {

    public ImpaledTridentEntity(EntityType<? extends ImpaledTridentEntity> entityType, World world) {
        super(entityType, world);
    }

    public void setTridentAttributes(World world, LivingEntity owner, ItemStack stack) {
        this.setTridentStack(stack.copy());
        this.dataTracker.set(TridentEntityAccessor.impaled$getLoyalty(), (byte) EnchantmentHelper.getLoyalty(stack));
        this.dataTracker.set(TridentEntityAccessor.impaled$getEnchanted(), stack.hasGlint());
    }

    protected float getDragInWater() {
        return 0.99f;
    }

    public ItemStack getTridentStack() {
        return ((TridentEntityAccessor) this).impaled$getTridentStack();
    }

    public void setTridentStack(ItemStack tridentStack) {
        ((TridentEntityAccessor) this).impaled$setTridentStack(tridentStack);
    }

    protected void setDealtDamage(boolean dealtDamage) {
        ((TridentEntityAccessor) this).impaled$setDealtDamage(dealtDamage);
    }

    protected boolean hasDealtDamage() {
        return ((TridentEntityAccessor) this).impaled$hasDealtDamage();
    }
}
