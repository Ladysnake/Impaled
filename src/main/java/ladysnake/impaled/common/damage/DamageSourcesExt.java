package ladysnake.impaled.common.damage;

import net.minecraft.entity.damage.DamageSources;

/**
 * Allows access to {@link ImpaledDamageSources} from {@link DamageSources}
 *
 * <p>Interface injected into {@link DamageSources}
 */
public interface DamageSourcesExt {
    default ImpaledDamageSources impaledSources() {
        throw new IllegalStateException("Not transformed");
    }
}
