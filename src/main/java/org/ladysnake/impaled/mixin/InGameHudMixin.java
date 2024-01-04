package org.ladysnake.impaled.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.ladysnake.impaled.common.item.ElderTridentItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.amymialee.mialeemisc.entities.IPlayerTargeting;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "render", at = @At("TAIL"))
    private void impaled$renderCrosshair(DrawContext context, float tickDelta, CallbackInfo ci) {
        PlayerEntity player = this.client.player;
        if (!(player instanceof IPlayerTargeting targeting)) {
            return;
        }
        Entity target = targeting.mialeeMisc$getLastTarget();
        if (target == null || !target.isAlive() || target.isRemoved()) {
            return;
        }
        ItemStack mainHandStack = player.getMainHandStack();
        if (EnchantmentHelper.getRiptide(mainHandStack) > 0) {
            return;
        }
        if (!(mainHandStack.getItem() instanceof ElderTridentItem)) {
            return;
        }
        ClientPlayerInteractionManager interactionManager = this.client.interactionManager;
        if (interactionManager == null || interactionManager.getCurrentGameMode() == GameMode.SPECTATOR) {
            return;
        }
        if (this.client.world == null) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            Vec3d vec3d = new Vec3d(
                    target.getX() + (MathHelper.sin((target.age + tickDelta) * 0.75f + i * 45) * target.getWidth() * 1.2),
                    target.getBodyY(0.5f),
                    target.getZ() + (MathHelper.cos((target.age + tickDelta) * 0.75f + i * 45) * target.getWidth() * 1.2));
            this.client.world.addParticle(ParticleTypes.BUBBLE, vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.0, 0.0, 0.0);
        }
    }
}