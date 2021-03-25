package ladysnake.impaled.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class HellforkEntity extends ImpaledTridentEntity {
    public HellforkEntity(EntityType<? extends HellforkEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        entityHitResult.getEntity().setOnFireFor(4 + this.world.getRandom().nextInt(4));
    }

    @Override
    public boolean isOnFire() {
        return true;
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }
}
