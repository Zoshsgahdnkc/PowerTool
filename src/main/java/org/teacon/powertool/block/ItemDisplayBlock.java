package org.teacon.powertool.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.teacon.powertool.block.entity.ItemDisplayBlockEntity;
import org.teacon.powertool.block.entity.ItemSupplierBlockEntity;

import java.util.List;

public class ItemDisplayBlock extends BaseEntityBlock {

    protected static final VoxelShape DOWN_AABB = Block.box(2, 15, 2, 14, 16, 14);
    protected static final VoxelShape UP_AABB = Block.box(2, 0, 2, 14, 1, 14);
    protected static final VoxelShape SOUTH_AABB = Block.box(2, 2, 0, 14, 14, 1);
    protected static final VoxelShape WEST_AABB = Block.box(15, 2, 2, 16, 14, 14);
    protected static final VoxelShape NORTH_AABB = Block.box(2, 2, 15, 14, 14, 16);
    protected static final VoxelShape EAST_AABB = Block.box(0, 2, 2, 1, 14, 14);

    private static final DirectionProperty FACING = BlockStateProperties.FACING;

    public ItemDisplayBlock(Properties prop) {
        super(prop);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("block.powertool.item_display.tooltip").withStyle(ChatFormatting.DARK_GRAY));
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> NORTH_AABB;
            case EAST -> EAST_AABB;
            case SOUTH -> SOUTH_AABB;
            case WEST -> WEST_AABB;
            case UP -> UP_AABB;
            case DOWN -> DOWN_AABB;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        return this.defaultBlockState().setValue(FACING, direction);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ItemDisplayBlockEntity(pos, state);
    }
    @Override
    public void attack(BlockState state, Level level, BlockPos pos, Player player) {
        if (!level.isClientSide() && player.getAbilities().instabuild && level.getBlockEntity(pos) instanceof ItemDisplayBlockEntity theBE) {
            theBE.itemToDisplay = ItemStack.EMPTY;
            theBE.setChanged();
            level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
        }
    }
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.getBlockEntity(pos) instanceof ItemDisplayBlockEntity theBE) {
            if (theBE.itemToDisplay.isEmpty() && player.getAbilities().instabuild) {
                theBE.itemToDisplay = player.getItemInHand(hand).copy();
                if (!level.isClientSide) {
                    theBE.setChanged();
                    level.sendBlockUpdated(pos, state, state, Block.UPDATE_CLIENTS);
                }
            } else {
                ItemStack toGive = theBE.itemToDisplay.copy();
                toGive.setCount(player.isCrouching() ? toGive.getMaxStackSize() : 1);
                player.getInventory().add(toGive);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
