package net.qxeii.hardcore_torches.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.qxeii.hardcore_torches.Mod;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

public class GlowstoneItem extends BlockItem implements FabricItem {

    int maxFuel;
    boolean isLit;
    public GlowstoneItem(Block block, Settings settings, int maxFuel, boolean isLit) {
        super(block, settings);
        this.maxFuel = maxFuel;
        this.isLit = isLit;
    }
    public static ItemStack stateStack(ItemStack inputStack, boolean isLit) {
        ItemStack outputStack = ItemStack.EMPTY;

        if (inputStack.getItem() instanceof BlockItem && inputStack.getItem() instanceof GlowstoneItem) {
            GlowstoneItem newItem = (GlowstoneItem) Mod.GLOWSTONE.asItem();

            outputStack = new ItemStack(newItem, inputStack.getCount());

            if (inputStack.getNbt() != null) {
                outputStack.setNbt(inputStack.getNbt().copy());
            }
        }

        return outputStack;
    }
    public static ItemStack addFuel(ItemStack stack, World world, int amount) {

        if (stack.getItem() instanceof GlowstoneItem && !world.isClient) {
            GlowstoneItem item = (GlowstoneItem) stack.getItem();

            NbtCompound nbt = stack.getNbt();
            int fuel = item.isLit ? item.maxFuel : 0;

            if (nbt != null) {
                fuel = nbt.getInt("Fuel");
            } else {
                nbt = new NbtCompound();
            }

            fuel += amount;

            // If burn out
            if (fuel > Mod.config.defaultGlowstoneFuel) {
                fuel = Mod.config.defaultGlowstoneFuel;
            }

            nbt.putInt("Fuel", fuel);
            stack.setNbt(nbt);
        }

        return stack;
    }
    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        NbtCompound oldNbt = null;
        NbtCompound newNbt = null;

        if (oldStack.getNbt() != null) {
            oldNbt = oldStack.getNbt().copy();
            oldNbt.remove("Fuel");
        }

        if (newStack.getNbt() != null) {
            newNbt = newStack.getNbt().copy();
            newNbt.remove("Fuel");
        }

        if (oldNbt == null && newNbt != null) return true;
        if (oldNbt != null && newNbt == null) return true;
        if (oldNbt == null && newNbt == null) return false;

        return oldNbt.equals(null);
    }
    public static int getFuel(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt != null) {
            return nbt.getInt("Fuel");
        } else {
            return Mod.config.defaultGlowstoneFuel;
        }
    }
}
