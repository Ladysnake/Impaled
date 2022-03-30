package ladysnake.impaled.common.item;

import ladysnake.impaled.common.damage.HellforkHeatDamageSource;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import ladysnake.impaled.common.init.ImpaledItems;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HellforkItem extends ImpaledTridentItem {
    public HellforkItem(Settings settings, EntityType<? extends ImpaledTridentEntity> entityType) {
        super(settings, entityType);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.setOnFireFor(4 + attacker.getRandom().nextInt(4));
        return super.postHit(stack, target, attacker);
    }

    @Override
    protected boolean canRiptide(PlayerEntity playerEntity) {
        return playerEntity.isInLava() || playerEntity.isOnFire() || (playerEntity.getMainHandStack().getItem() == ImpaledItems.HELLFORK && playerEntity.getMainHandStack().hasNbt() && playerEntity.getMainHandStack().getNbt().getBoolean("Heated")) || (playerEntity.getOffHandStack().getItem() == ImpaledItems.HELLFORK && playerEntity.getOffHandStack().hasNbt() && playerEntity.getOffHandStack().getNbt().getBoolean("Heated"));
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);

        if (!user.isOnFire() && stack.hasNbt() && stack.getNbt().getBoolean("Heated") && !user.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
            user.damage(HellforkHeatDamageSource.HELLFORK_HEAT, 2f);
            user.playSound(SoundEvents.ENTITY_PLAYER_HURT_ON_FIRE, 1.0f, 1.0f);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        } else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !user.isInLava() && !user.isOnFire() && !(itemStack.hasNbt() && itemStack.getNbt().getBoolean("Heated"))) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.LAVA_CAULDRON && (!context.getStack().hasNbt() || !context.getStack().getNbt().getBoolean("Heated"))) {
            context.getStack().getOrCreateNbt().putBoolean("Heated", true);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
            context.getWorld().setBlockState(context.getBlockPos(), Blocks.CAULDRON.getDefaultState());
            for (int i = 0; i < 20; i++) {
                context.getWorld().addParticle(ParticleTypes.LAVA, context.getBlockPos().getX() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getY() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getZ() + .5 + context.getWorld().getRandom().nextGaussian() / 10, 0, context.getWorld().getRandom().nextFloat() / 10, 0);
            }
            return ActionResult.SUCCESS;
        } else if ((context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.WATER_CAULDRON || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.POWDER_SNOW_CAULDRON) && context.getStack().getNbt().getBoolean("Heated")) {
            context.getStack().getNbt().putBoolean("Heated", false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
            context.getWorld().setBlockState(context.getBlockPos(), Blocks.CAULDRON.getDefaultState());
            for (int i = 0; i < 50; i++) {
                context.getWorld().addParticle(ParticleTypes.SMOKE, context.getBlockPos().getX() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getY() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getZ() + .5 + context.getWorld().getRandom().nextGaussian() / 10, 0, context.getWorld().getRandom().nextFloat() / 10, 0);
            }
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);

        if (stack.getNbt().getBoolean("Heated")) {
            tooltip.add(new TranslatableText("tooltip.impaled.heated").formatted(Formatting.GOLD));
        }
    }
}
