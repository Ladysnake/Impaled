package ladysnake.impaled.common.init;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.item.AtlanItem;
import ladysnake.impaled.common.item.ElderTridentItem;
import ladysnake.impaled.common.item.HellforkItem;
import ladysnake.impaled.common.item.ImpaledTridentItem;
import ladysnake.impaled.common.item.MaelstromItem;
import ladysnake.impaled.common.item.PitchforkItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Position;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Set;

public class ImpaledItems {
    public static Item PITCHFORK;
    public static Item HELLFORK;
    public static Item ELDER_TRIDENT;
    public static Item ATLAN;
    public static Item MAELSTROM;
    public static final Set<ImpaledTridentItem> ALL_TRIDENTS = new ReferenceOpenHashSet<>();

    public static void init() {
        PITCHFORK = registerTrident(new PitchforkItem((new Item.Settings()).maxDamage(250).group(ItemGroup.COMBAT), ImpaledEntityTypes.PITCHFORK), "pitchfork", true);
        HELLFORK = registerTrident(new HellforkItem((new Item.Settings()).maxDamage(325).fireproof().group(ItemGroup.COMBAT), ImpaledEntityTypes.HELLFORK), "hellfork", true);
        ELDER_TRIDENT = registerTrident(new ElderTridentItem((new Item.Settings()).maxDamage(250).group(ItemGroup.COMBAT), ImpaledEntityTypes.ELDER_TRIDENT), "elder_trident", true);
        ATLAN = registerTrident(new AtlanItem((new Item.Settings()).maxDamage(250).group(ItemGroup.COMBAT), ImpaledEntityTypes.ATLAN), "atlan", true);
        MAELSTROM = registerItem(new MaelstromItem((new Item.Settings()).maxDamage(80).group(ItemGroup.COMBAT)), "maelstrom");
    }

    public static ImpaledTridentItem registerTrident(ImpaledTridentItem item, String name, boolean registerDispenserBehavior) {
        Registry.register(Registry.ITEM, Impaled.MODID + ":" + name, item);
        ALL_TRIDENTS.add(item);
        if (registerDispenserBehavior) {
            DispenserBlock.registerBehavior(item, new ProjectileDispenserBehavior() {
                @Override
                protected ProjectileEntity createProjectile(World world, Position position, ItemStack itemStack) {
                    ImpaledTridentEntity tridentEntity = Objects.requireNonNull(item.getEntityType().create(world));
                    tridentEntity.setPos(position.getX(), position.getY(), position.getZ());
                    itemStack.decrement(1);
                    return tridentEntity;
                }
            });
        }

        return item;
    }

    public static Item registerItem(Item item, String name) {
        Registry.register(Registry.ITEM, Impaled.MODID + ":" + name, item);
        return item;
    }
}
