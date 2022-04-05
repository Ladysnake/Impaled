package ladysnake.impaled.common.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import ladysnake.impaled.common.entity.ImpaledTridentEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Map;

public class PitchforkItem extends ImpaledTridentItem {
    protected static final Map<Block, BlockState> TILLED_BLOCKS;

    static {
        TILLED_BLOCKS = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT_PATH, Blocks.FARMLAND.getDefaultState(), Blocks.DIRT, Blocks.FARMLAND.getDefaultState(), Blocks.COARSE_DIRT, Blocks.DIRT.getDefaultState()));
    }

    public PitchforkItem(Settings settings, EntityType<? extends ImpaledTridentEntity> entityType) {
        super(settings, entityType);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getBlockPos();
        if (context.getSide() != Direction.DOWN && world.getBlockState(blockPos.up()).isAir()) {
            BlockState blockState = (BlockState) TILLED_BLOCKS.get(world.getBlockState(blockPos).getBlock());
            if (blockState != null) {
                PlayerEntity playerEntity = context.getPlayer();
                world.playSound(playerEntity, blockPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (!world.isClient) {
                    world.setBlockState(blockPos, blockState, 11);
                    if (playerEntity != null) {
                        context.getStack().damage(1, (LivingEntity) playerEntity, livingEntity -> livingEntity.sendToolBreakStatus(context.getPlayer().getActiveHand()));
                    }
                }

                return ActionResult.success(world.isClient);
            }
        }

        return ActionResult.PASS;
    }
}
