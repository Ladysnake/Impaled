package ladysnake.impaled.common.damage;

import net.minecraft.entity.damage.DamageSource;

public class HellforkHeatDamageSource extends DamageSource {
    public static final DamageSource HELLFORK_HEAT = ((HellforkHeatDamageSource)new HellforkHeatDamageSource("hellfork_heat").setBypassesArmor()).setUnblockable();

    protected HellforkHeatDamageSource(String name) {
        super(name);
    }
}
