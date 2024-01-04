package org.ladysnake.impaled.common.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.ladysnake.impaled.common.Impaled;

public final class ImpaledDamageSources {
    public static final RegistryKey<DamageType> HELLFORK_HEAT = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Impaled.MODID, "hellfork_heat"));

    private final DamageSource hellforkHeat;

    public ImpaledDamageSources(DamageSources damageSources) {
        this.hellforkHeat = damageSources.create(HELLFORK_HEAT);
    }

    public DamageSource hellforkHeat() {
        return this.hellforkHeat;
    }
}
