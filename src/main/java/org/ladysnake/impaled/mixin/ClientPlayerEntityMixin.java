package org.ladysnake.impaled.mixin;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.impaled.common.entity.MialeeTargetingUtil;
import org.ladysnake.impaled.common.entity.IPlayerTargeting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements IPlayerTargeting {
    @Unique @Nullable LivingEntity lastTarget;
    @Unique int targetDecayTime;

    @Override
    public LivingEntity mialeeMisc$getLastTarget() {
        return this.lastTarget;
    }

    @Override
    public void mialeeMisc$setLastTarget(LivingEntity target) {
        this.lastTarget = target;
        this.targetDecayTime = 60;
        if (target != null) {
            ClientPlayNetworking.send(
                    new MialeeTargetingUtil.MialeeTargetPayload(target.getId())
            );
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void mialeeMisc$decayTarget(CallbackInfo ci) {
        if (this.targetDecayTime > 0) {
            this.targetDecayTime--;
            if (this.targetDecayTime == 0) {
                this.mialeeMisc$setLastTarget(null);
            }
        }
    }
}
