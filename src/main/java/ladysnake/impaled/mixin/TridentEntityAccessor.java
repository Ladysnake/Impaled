package ladysnake.impaled.mixin;

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

    // TODO: TridentEntity no longer has direct ItemStack field in 1.21.3
    // Need to use getItemStack() and setItemStack() methods instead
    // @Accessor("item") 
    // ItemStack impaled$getTridentStack();

    // @Accessor("item")
    // void impaled$setTridentStack(ItemStack stack);

    @Accessor("dealtDamage")
    boolean impaled$hasDealtDamage();

    @Accessor("dealtDamage")
    void impaled$setDealtDamage(boolean dealtDamage);
}
