package ladysnake.impaled.common.init;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import ladysnake.impaled.common.Impaled;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.item.*;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

import java.util.function.Function;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

import java.util.Objects;
import java.util.Set;

public class ImpaledItems {
    public static final Set<ImpaledTridentItem> ALL_TRIDENTS = new ReferenceOpenHashSet<>();
    public static Item ELDER_GUARDIAN_EYE;
    public static Item ANCIENT_TRIDENT;
    public static Item PITCHFORK;
    public static Item HELLFORK;
    public static Item SOULFORK;
    public static Item ELDER_TRIDENT;
    public static Item ATLAN;
    public static Item MAELSTROM;

    public static void init() {
        ELDER_GUARDIAN_EYE = registerItem("elder_guardian_eye", Item::new, new Item.Settings().rarity(Rarity.UNCOMMON));
        ANCIENT_TRIDENT = registerItem("ancient_trident", Item::new, new Item.Settings().rarity(Rarity.UNCOMMON).fireproof());

        PITCHFORK = registerTrident("pitchfork", settings -> new PitchforkItem(settings, ImpaledEntityTypes.PITCHFORK), new Item.Settings().maxDamage(150).rarity(Rarity.COMMON), true);
        HELLFORK = registerTrident("hellfork", settings -> new HellforkItem(settings, ImpaledEntityTypes.HELLFORK), new Item.Settings().maxDamage(325).fireproof().rarity(Rarity.UNCOMMON), true);
        SOULFORK = registerTrident("soulfork", settings -> new HellforkItem(settings, ImpaledEntityTypes.SOULFORK), new Item.Settings().maxDamage(325).fireproof().rarity(Rarity.RARE), true);
        ELDER_TRIDENT = registerTrident("elder_trident", settings -> new ElderTridentItem(settings, ImpaledEntityTypes.ELDER_TRIDENT), new Item.Settings().maxDamage(250).rarity(Rarity.RARE), true);
        ATLAN = registerTrident("atlan", settings -> new AtlanItem(settings, ImpaledEntityTypes.ATLAN), new Item.Settings().maxDamage(250).rarity(Rarity.EPIC), true);
        MAELSTROM = registerItem("maelstrom", settings -> new MaelstromItem(settings), new Item.Settings().maxDamage(80).rarity(Rarity.EPIC));
    }

    public static <T extends Item> T registerItem(String name, Function<Item.Settings, T> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Impaled.MODID, name));
        T item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }

    public static <T extends ImpaledTridentItem> T registerTrident(String name, Function<Item.Settings, T> itemFactory, Item.Settings settings, boolean registerDispenserBehavior) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Impaled.MODID, name));
        T item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        ALL_TRIDENTS.add(item);
        
        if (registerDispenserBehavior) {
            DispenserBlock.registerBehavior(item, new ProjectileDispenserBehavior(item) {
                protected ProjectileEntity createProjectile(World world, Position position, ItemStack itemStack) {
                    ImpaledTridentEntity tridentEntity = Objects.requireNonNull(item.getEntityType().create(world, net.minecraft.entity.SpawnReason.DISPENSER));
                    tridentEntity.setPos(position.getX(), position.getY(), position.getZ());
                    itemStack.decrement(1);
                    return tridentEntity;
                }
            });
        }

        return item;
    }

}
