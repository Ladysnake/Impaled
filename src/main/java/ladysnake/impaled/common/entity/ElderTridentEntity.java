package ladysnake.impaled.common.entity;

import ladysnake.impaled.common.init.ImpaledEntityTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class ElderTridentEntity extends ImpaledTridentEntity {
    public ElderTridentEntity(EntityType<? extends ImpaledTridentEntity> entityType, World world) {
        super(ImpaledEntityTypes.ELDER_TRIDENT, world);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.hasDealtDamage()) {
        }
    }

    @Override
    protected float getDragInWater() {
        return 1.01f;
    }
}
