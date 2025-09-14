package ladysnake.impaled.common;

import net.minecraft.item.ItemStack;

/**
 * Interface for accessing custom data stored in PlayerEntityRenderState via mixin
 */
public interface PlayerEntityRenderStateExtensions {
    ItemStack impaled$getMainHandStack();
    void impaled$setMainHandStack(ItemStack stack);
    ItemStack impaled$getOffHandStack();
    void impaled$setOffHandStack(ItemStack stack);
    boolean impaled$isUsingRiptide();
    void impaled$setUsingRiptide(boolean usingRiptide);
}