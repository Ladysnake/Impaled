package ladysnake.impaled.mixin.impaling;

import ladysnake.impaled.common.enchantment.BetterImpaling;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin {

    @ModifyVariable(method = "onEntityHit", at = @At(value = "STORE", ordinal = 0))
    private float enhanceTridentDamage(float baseDamage, EntityHitResult result) {
        TridentEntity trident = (TridentEntity)(Object)this;
        return baseDamage + BetterImpaling.getAttackDamage(trident.getWeaponStack(), result.getEntity(), trident.getWorld().getRegistryManager());
    }
}
