package ladysnake.impaled.common.damage;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import ladysnake.impaled.common.Impaled;

public class HellforkHeatDamageSource {
    public static final RegistryKey<DamageType> HELLFORK_HEAT_TYPE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(Impaled.MODID, "hellfork_heat"));
    
    public static final DamageSource HELLFORK_HEAT = new DamageSource(null); // Placeholder - will be created properly when needed
}
