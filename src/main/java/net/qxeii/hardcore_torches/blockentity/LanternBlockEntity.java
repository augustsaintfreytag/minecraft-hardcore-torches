package net.qxeii.hardcore_torches.blockentity;

import net.qxeii.hardcore_torches.Mod;
import net.qxeii.hardcore_torches.block.AbstractLanternBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LanternBlockEntity extends FuelBlockEntity {

    public LanternBlockEntity(BlockPos pos, BlockState state) {
        super(Mod.LANTERN_BLOCK_ENTITY, pos, state);
        fuel = 0;
    }

    public static void tick(World world, BlockPos pos, BlockState state, LanternBlockEntity be) {
        if (!world.isClient) {
            // Burn out
            if (world.getBlockState(pos).getBlock() instanceof AbstractLanternBlock) {
                if (be.fuel >= 0 && ((AbstractLanternBlock) world.getBlockState(pos).getBlock()).isLit) {
                    be.changeFuel(-1);
                }

                be.markDirty();
            }
        }
    }
}
