package net.qxeii.hardcore_torches.block;

import java.util.function.IntSupplier;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.qxeii.hardcore_torches.util.ETorchState;

public class WallTorchBlock extends AbstractTorchBlock {

	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

	public WallTorchBlock(Settings settings, ParticleEffect particle, ETorchState type, IntSupplier maxFuel) {
		super(settings, particle, type, maxFuel);
		setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
	}

	// region Overridden methods for TorchBlock since I can't extend 2 classes
	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return Blocks.WALL_TORCH.getOutlineShape(state, world, pos, context);
	}

	public static VoxelShape getBoundingShape(BlockState state) {
		return WallTorchBlock.getBoundingShape(state);
	}

	@Override
	public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
		return Blocks.WALL_TORCH.canPlaceAt(state, world, pos);
	}

	@Override
	@Nullable
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		BlockState torchState = Blocks.WALL_TORCH.getPlacementState(ctx);

		if (torchState != null) {
			BlockState state = this.getDefaultState();
			Direction d = torchState.get(FACING);
			return state.with(FACING, d);
		}

		return null;
	}

	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
			WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		return Blocks.WALL_TORCH.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	public BlockState rotate(BlockState state, BlockRotation rotation) {
		return Blocks.WALL_TORCH.rotate(state, rotation);
	}

	@Override
	public BlockState mirror(BlockState state, BlockMirror mirror) {
		return Blocks.WALL_TORCH.mirror(state, mirror);
	}
	// endregion

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
		stateManager.add(FACING);
	}

	// region IHardcoreTorch
	@Override
	public void smother(World world, BlockPos pos, BlockState state, boolean playSound) {
		super.smother(world, pos, state, playSound);

		WallTorchBlock newTorch;
		newTorch = group.getWallTorch(ETorchState.SMOLDERING);

		world.setBlockState(pos, newTorch.getDefaultState().with(HorizontalFacingBlock.FACING, state.get(FACING)));
	}

	@Override
	public void burnOut(World world, BlockPos pos, BlockState state, boolean playSound) {
		super.burnOut(world, pos, state, playSound);

		WallTorchBlock newTorch;
		newTorch = group.getWallTorch(ETorchState.BURNT);

		world.setBlockState(pos, newTorch.getDefaultState().with(HorizontalFacingBlock.FACING, state.get(FACING)));
	}

	@Override
	public void light(World world, BlockPos pos, BlockState state) {
		super.light(world, pos, state);

		WallTorchBlock newTorch;
		newTorch = group.getWallTorch(ETorchState.LIT);

		world.setBlockState(pos, newTorch.getDefaultState().with(HorizontalFacingBlock.FACING, state.get(FACING)));
	}

	@Override
	public boolean isWall() {
		return true;
	}
	// endregion
}
