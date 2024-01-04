package org.ladysnake.impaled.mixin;

import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TridentEntity.class)
public interface TridentEntityAccessor {
    @Accessor("LOYALTY")
    static TrackedData<Byte> impaled$getLoyalty() {
        return null;
    }

    @Accessor("ENCHANTED")
    static TrackedData<Boolean> impaled$getEnchanted() {
        return null;
    }

    @Accessor("tridentStack")
    ItemStack impaled$getTridentStack();

    @Accessor("tridentStack")
    void impaled$setTridentStack(ItemStack stack);

    @Accessor("dealtDamage")
    boolean impaled$hasDealtDamage();

    @Accessor("dealtDamage")
    void impaled$setDealtDamage(boolean dealtDamage);
}
