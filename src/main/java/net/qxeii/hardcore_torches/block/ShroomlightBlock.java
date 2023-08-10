package net.qxeii.hardcore_torches.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.IntSupplier;
import java.util.function.ToIntFunction;

public class ShroomlightBlock extends AbstractShroomlightBlock implements BlockEntityProvider{
    public static final IntProperty LEVEL_15;
    public static final ToIntFunction<BlockState> STATE_TO_LUMINANCE;

    public ShroomlightBlock(Settings settings, IntSupplier maxFuel, boolean isLit) {
        super(settings, maxFuel, isLit);
        this.setDefaultState((BlockState) ((BlockState) this.stateManager.getDefaultState().with(LEVEL_15, 0)));
    }

    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(new Property[]{LEVEL_15});
    }

    static {
        LEVEL_15 = Properties.LEVEL_15;
        STATE_TO_LUMINANCE = (state) -> {
            return (Integer) state.get(LEVEL_15);
        };
    }

    @Override
    public void outOfFuel(World world, BlockPos pos, BlockState state, boolean playSound) {

    }
}
