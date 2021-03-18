package ladysnake.impaled.common.entity;

import ladysnake.impaled.common.init.ImpaledEntityTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class PitchforkEntity extends ImpaledTridentEntity {
    public PitchforkEntity(EntityType<? extends ImpaledTridentEntity> entityType, World world) {
        super(ImpaledEntityTypes.PITCHFORK, world);
    }

    @Override
    protected float getDragInWater() {
        return 0.6F;
    }
}
