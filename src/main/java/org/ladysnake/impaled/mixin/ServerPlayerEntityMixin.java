package org.ladysnake.impaled.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.ladysnake.impaled.common.entity.IPlayerTargeting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin implements IPlayerTargeting {
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
