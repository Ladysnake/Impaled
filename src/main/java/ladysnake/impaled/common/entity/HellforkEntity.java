package ladysnake.impaled.common.entity;

import ladysnake.impaled.common.init.ImpaledEntityTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class HellforkEntity extends ImpaledTridentEntity {
    public HellforkEntity(EntityType<? extends ImpaledTridentEntity> entityType, World world) {
        super(ImpaledEntityTypes.HELLFORK, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().setOnFireFor(100);
    }
}
