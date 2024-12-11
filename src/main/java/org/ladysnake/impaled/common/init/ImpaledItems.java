package org.ladysnake.impaled.common.init;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SmithingTemplateItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.ladysnake.impaled.common.Impaled;
import org.ladysnake.impaled.common.entity.ImpaledTridentEntity;
import org.ladysnake.impaled.common.item.AtlanItem;
import org.ladysnake.impaled.common.item.ElderTridentItem;
import org.ladysnake.impaled.common.item.HellforkItem;
import org.ladysnake.impaled.common.item.ImpaledTridentItem;
import org.ladysnake.impaled.common.item.MaelstromItem;
import org.ladysnake.impaled.common.item.PitchforkItem;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ImpaledItems {
    public static final Set<ImpaledTridentItem> ALL_TRIDENTS = new ReferenceOpenHashSet<>();
    public static final Item ELDER_GUARDIAN_EYE = new Item((new Item.Settings()).rarity(Rarity.UNCOMMON));
    public static final Item ANCIENT_TRIDENT = new Item((new Item.Settings()).rarity(Rarity.UNCOMMON).fireproof());
    public static final PitchforkItem PITCHFORK = new PitchforkItem((new Item.Settings()).maxDamage(150), ImpaledEntityTypes.PITCHFORK);
    public static final HellforkItem HELLFORK = new HellforkItem((new Item.Settings()).maxDamage(325).fireproof(), ImpaledEntityTypes.HELLFORK);
    public static final HellforkItem SOULFORK = new HellforkItem((new Item.Settings()).maxDamage(325).fireproof(), ImpaledEntityTypes.SOULFORK);
    public static final ElderTridentItem ELDER_TRIDENT = new ElderTridentItem((new Item.Settings()).maxDamage(250), ImpaledEntityTypes.ELDER_TRIDENT);
    public static final AtlanItem ATLAN = new AtlanItem((new Item.Settings()).maxDamage(250), ImpaledEntityTypes.ATLAN);
    public static final MaelstromItem MAELSTROM = new MaelstromItem((new Item.Settings()).maxDamage(80));
    public static final Item TRIDENT_UPGRADE_SMITHING_TEMPLATE = new SmithingTemplateItem(
            Text.translatable(
                    Util.createTranslationKey("item", Impaled.id("smithing_template.trident_upgrade.applies_to"))
            ).formatted(Formatting.BLUE),
            Text.translatable(
                    Util.createTranslationKey("item", Impaled.id("smithing_template.trident_upgrade.ingredients"))
            ).formatted(Formatting.BLUE),
            Text.translatable(
                    Util.createTranslationKey("upgrade", Impaled.id("trident_upgrade"))
            ).formatted(Formatting.GRAY),
            Text.translatable(
                    Util.createTranslationKey("item", Impaled.id("smithing_template.trident_upgrade.base_slot_description"))
            ),
            Text.translatable(
                    Util.createTranslationKey("item", Impaled.id("smithing_template.trident_upgrade.additions_slot_description"))
            ),
            List.of(new Identifier("item/empty_slot_sword")),
            List.of(new Identifier("item/empty_slot_amethyst_shard"))
    );

    public static void init() {
        registerItem(ELDER_GUARDIAN_EYE, "elder_guardian_eye");
        registerItem(ANCIENT_TRIDENT, "ancient_trident");
        registerTrident(PITCHFORK, "pitchfork", true);
        registerTrident(HELLFORK, "hellfork", true);
        registerTrident(SOULFORK, "soulfork", true);
        registerTrident(ELDER_TRIDENT, "elder_trident", true);
        registerTrident(ATLAN, "atlan", true);
        registerItem(MAELSTROM, "maelstrom");
        registerItem(TRIDENT_UPGRADE_SMITHING_TEMPLATE, "trident_upgrade_smithing_template");
    }

    public static void registerTrident(ImpaledTridentItem item, String name, boolean registerDispenserBehavior) {
        registerItem(item, name);
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
    }

    public static void registerItem(Item item, String name) {
        Registry.register(Registries.ITEM, Impaled.MODID + ":" + name, item);
    }
}
