package ladysnake.impaled.common.init;

import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.item.ImpaledTridentItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Position;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class ImpaledItems {
    public static Item PITCHFORK;
    public static Item HELLFORK;
    public static Item ELDER_TRIDENT;

    public static void init() {
        PITCHFORK = registerItem(new ImpaledTridentItem((new Item.Settings()).maxDamage(250).group(ItemGroup.COMBAT), ImpaledEntityTypes.PITCHFORK), "pitchfork", true);
        HELLFORK = registerItem(new ImpaledTridentItem((new Item.Settings()).maxDamage(250).group(ItemGroup.COMBAT), ImpaledEntityTypes.HELLFORK), "hellfork", true);
        ELDER_TRIDENT = registerItem(new ImpaledTridentItem((new Item.Settings()).maxDamage(250).group(ItemGroup.COMBAT), ImpaledEntityTypes.ELDER_TRIDENT), "elder_trident", true);
    }

    public static Item registerItem(Item item, String name, boolean registerDispenserBehavior) {
        Registry.register(Registry.ITEM, Impaled.MODID + ":" + name, item);
        if (registerDispenserBehavior) {
            DispenserBlock.registerBehavior(item, new ProjectileDispenserBehavior() {
                @Override
                protected ProjectileEntity createProjectile(World world, Position position, ItemStack itemStack) {
                    TridentEntity tridentEntity = EntityType.TRIDENT.create(world);
                    tridentEntity.setPos(position.getX(), position.getY(), position.getZ());
                    itemStack.decrement(1);
                    return tridentEntity;
                }
            });
        }

        return item;
    }

}
