package ladysnake.impaled.mixin;

import ladysnake.impaled.common.PlayerEntityRenderStateExtensions;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateAccessor implements PlayerEntityRenderStateExtensions {
    @Unique
    private ItemStack impaled$mainHandStack = ItemStack.EMPTY;

    @Unique
    private ItemStack impaled$offHandStack = ItemStack.EMPTY;

    @Unique
    private boolean impaled$isUsingRiptide = false;

    public ItemStack impaled$getMainHandStack() {
        return this.impaled$mainHandStack;
    }

    public void impaled$setMainHandStack(ItemStack stack) {
        this.impaled$mainHandStack = stack;
    }

    public ItemStack impaled$getOffHandStack() {
        return this.impaled$offHandStack;
    }

    public void impaled$setOffHandStack(ItemStack stack) {
        this.impaled$offHandStack = stack;
    }

    public boolean impaled$isUsingRiptide() {
        return this.impaled$isUsingRiptide;
    }

    public void impaled$setUsingRiptide(boolean usingRiptide) {
        this.impaled$isUsingRiptide = usingRiptide;
    }
}