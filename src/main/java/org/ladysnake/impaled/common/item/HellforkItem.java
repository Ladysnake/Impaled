package org.ladysnake.impaled.common.item;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.ladysnake.impaled.compat.SoulFired;
import org.ladysnake.impaled.common.entity.ImpaledTridentEntity;
import org.ladysnake.impaled.common.init.ImpaledItems;

public class HellforkItem extends ImpaledTridentItem {
    public HellforkItem(Settings settings, EntityType<? extends ImpaledTridentEntity> entityType) {
        super(settings, entityType);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        int seconds = 4 + attacker.getRandom().nextInt(4);
        if (this == ImpaledItems.SOULFORK && FabricLoader.getInstance().isModLoaded("soul_fire_d")) {
            SoulFired.setOnSoulFireFor(target, seconds);
        } else {
            target.setOnFireFor(seconds);
        }
        return super.postHit(stack, target, attacker);
    }

    @Override
    protected boolean canRiptide(PlayerEntity playerEntity) {
        return playerEntity.isInLava() || playerEntity.isOnFire() || playerEntity.getMainHandStack().getItem() == ImpaledItems.SOULFORK || playerEntity.getOffHandStack().getItem() == ImpaledItems.SOULFORK;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        super.onStoppedUsing(stack, world, user, remainingUseTicks);

        if (user instanceof PlayerEntity player && user.isUsingRiptide() && stack.getItem() == ImpaledItems.SOULFORK) {
            if (player.experienceLevel <= 0) {
                user.damage(world.getDamageSources().impaledSources().hellforkHeat(), 2f);
                user.playSound(SoundEvents.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
            } else {
                player.addExperienceLevels(-1);
            }
            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.SOUL, user.getX(), user.getY(), user.getZ(), 20, user.getRandom().nextFloat(), user.getRandom().nextGaussian(), user.getRandom().nextFloat(), user.getRandom().nextFloat() / 10f);
            }
            user.playSound(SoundEvents.PARTICLE_SOUL_ESCAPE, 5.0f, 1.0f);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return TypedActionResult.fail(itemStack);
        } else if (EnchantmentHelper.getRiptide(itemStack) > 0 && !user.isInLava() && !user.isOnFire() && !(itemStack.hasNbt() && itemStack.getItem() == ImpaledItems.SOULFORK)) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if ((context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_CAMPFIRE || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_LANTERN || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_TORCH || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_WALL_TORCH) && context.getStack().getItem() == ImpaledItems.HELLFORK) {
            ItemStack soulfork = new ItemStack(ImpaledItems.SOULFORK, context.getStack().getCount());
            soulfork.setNbt(context.getStack().getNbt());
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);

            context.getPlayer().setStackInHand(context.getHand(), soulfork);

            BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
            BlockState replacedBlockState = blockState;
            if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_CAMPFIRE) {
                replacedBlockState = Blocks.CAMPFIRE.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_LANTERN) {
                replacedBlockState = Blocks.LANTERN.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_TORCH) {
                replacedBlockState = Blocks.TORCH.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.SOUL_WALL_TORCH) {
                replacedBlockState = Blocks.WALL_TORCH.getDefaultState();
            }
            for (Property property : context.getWorld().getBlockState(context.getBlockPos()).getProperties()) {
                if (replacedBlockState.getProperties().contains(property)) {
                    replacedBlockState = replacedBlockState.with(property, blockState.get(property));
                }
            }
            context.getWorld().setBlockState(context.getBlockPos(), replacedBlockState);

            for (int i = 0; i < 20; i++) {
                context.getWorld().addParticle(ParticleTypes.SOUL, context.getBlockPos().getX() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getY() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getZ() + .5 + context.getWorld().getRandom().nextGaussian() / 10, 0, context.getWorld().getRandom().nextFloat() / 10, 0);
            }
            return ActionResult.SUCCESS;
        } else if ((context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.CAMPFIRE || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.LANTERN || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.TORCH || context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.WALL_TORCH) && context.getStack().getItem() == ImpaledItems.SOULFORK) {
            ItemStack hellfork = new ItemStack(ImpaledItems.HELLFORK, context.getStack().getCount());
            hellfork.setNbt(context.getStack().getNbt());
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1.0f, 1.0f, false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.PARTICLE_SOUL_ESCAPE, SoundCategory.PLAYERS, 1.0f, 0.8f, false);
            context.getWorld().playSound(context.getPlayer().getX(), context.getPlayer().getY(), context.getPlayer().getZ(), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.PLAYERS, 1.0f, 0.8f, false);

            context.getPlayer().setStackInHand(context.getHand(), hellfork);

            BlockState blockState = context.getWorld().getBlockState(context.getBlockPos());
            BlockState replacedBlockState = blockState;
            if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.CAMPFIRE) {
                replacedBlockState = Blocks.SOUL_CAMPFIRE.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.LANTERN) {
                replacedBlockState = Blocks.SOUL_LANTERN.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.TORCH) {
                replacedBlockState = Blocks.SOUL_TORCH.getDefaultState();
            } else if (context.getWorld().getBlockState(context.getBlockPos()).getBlock() == Blocks.WALL_TORCH) {
                replacedBlockState = Blocks.SOUL_WALL_TORCH.getDefaultState();
            }
            for (Property property : context.getWorld().getBlockState(context.getBlockPos()).getProperties()) {
                if (replacedBlockState.getProperties().contains(property)) {
                    replacedBlockState = replacedBlockState.with(property, blockState.get(property));
                }
            }
            context.getWorld().setBlockState(context.getBlockPos(), replacedBlockState);

            for (int i = 0; i < 20; i++) {
                context.getWorld().addParticle(ParticleTypes.SOUL_FIRE_FLAME, context.getBlockPos().getX() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getY() + .5 + context.getWorld().getRandom().nextGaussian() / 10, context.getBlockPos().getZ() + .5 + context.getWorld().getRandom().nextGaussian() / 10, 0, context.getWorld().getRandom().nextFloat() / 10, 0);
            }
            return ActionResult.SUCCESS;
        }

        return super.useOnBlock(context);
    }
}
