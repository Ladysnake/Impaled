package ladysnake.impaled.common.item;

import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ImpaledTridentItem extends TridentItem {
    private final EntityType<? extends ImpaledTridentEntity> type;

    public ImpaledTridentItem(Settings settings, EntityType<? extends ImpaledTridentEntity> entityType) {
        super(settings);
        this.type = entityType;
    }

    public EntityType<? extends ImpaledTridentEntity> getEntityType() {
        return type;
    }

    public boolean canRiptide(LivingEntity playerEntity) {
        return playerEntity.isTouchingWaterOrRain();
    }

    public @NotNull ImpaledTridentEntity createTrident(World world, LivingEntity user, ItemStack stack) {
        ImpaledTridentEntity impaledTridentEntity = Objects.requireNonNull(this.type.create(world));
        impaledTridentEntity.setTridentAttributes(world, user, stack);
        impaledTridentEntity.setOwner(user);
        impaledTridentEntity.setTridentStack(stack);
        impaledTridentEntity.updatePosition(user.getX(), user.getEyeY() - 0.1, user.getZ());
        return impaledTridentEntity;
    }

    @Override
    public boolean damage(DamageSource source) {
        return super.damage(source);
    }
}
