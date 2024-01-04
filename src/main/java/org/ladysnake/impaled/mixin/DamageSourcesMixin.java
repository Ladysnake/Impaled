package org.ladysnake.impaled.mixin;

import net.minecraft.entity.damage.DamageSources;
import net.minecraft.registry.DynamicRegistryManager;
import org.ladysnake.impaled.common.damage.DamageSourcesExt;
import org.ladysnake.impaled.common.damage.ImpaledDamageSources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageSources.class)
public class DamageSourcesMixin implements DamageSourcesExt {
    @Unique
    private ImpaledDamageSources impaledDamageSources;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(DynamicRegistryManager registryManager, CallbackInfo ci) {
        this.impaledDamageSources = new ImpaledDamageSources((DamageSources) (Object) this);
    }

    @SuppressWarnings("AddedMixinMembersNamePattern") // It's fine, we have a custom type
    @Override
    public ImpaledDamageSources impaledSources() {
        return this.impaledDamageSources;
    }
}
