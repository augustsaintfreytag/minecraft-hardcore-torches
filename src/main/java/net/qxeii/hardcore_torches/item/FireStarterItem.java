package net.qxeii.hardcore_torches.item;

import net.qxeii.hardcore_torches.Mod;
import net.qxeii.hardcore_torches.block.AbstractHardcoreTorchBlock;
import net.qxeii.hardcore_torches.block.AbstractLanternBlock;
import net.qxeii.hardcore_torches.util.ETorchState;
import net.fabricmc.fabric.impl.client.particle.ParticleFactoryRegistryImpl;
import net.fabricmc.fabric.mixin.client.particle.ParticleManagerAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundEntry;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralTextContent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class FireStarterItem extends Item {
    private static final int USE_DURATION = 72000;

    public FireStarterItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() == Blocks.CAMPFIRE) {
            if (state.get(Properties.LIT)) {
                world.setBlockState(pos, state.with(Properties.LIT, true));
            }
        }

        return super.useOnBlock(context);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(MutableText.of(new LiteralTextContent("Has a chance to fail")).formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (world.isClient) return;
        //if (!(user instanceof PlayerEntity)) return;

        BlockHitResult hit = world.raycast(new RaycastContext(user.getEyePos(), user.getEyePos().add(user.getRotationVecClient().multiply(4)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, user));
        BlockPos pos = hit.getBlockPos();
        ServerWorld serverWorld = (ServerWorld) world;
        Block block = world.getBlockState(pos).getBlock();
        boolean attempt = false;
        boolean success;

        // Random chance to fail
        Random random = new Random();
        double num = random.nextDouble();
        success = num < Mod.config.starterSuccessChance;

        // Attempt to light
        boolean simulateFlintAndSteel = false;
        if (remainingUseTicks <= USE_DURATION - 15 && user instanceof PlayerEntity) {

            if (block instanceof CampfireBlock && Mod.config.starterLightCampfires) {
                attempt = true;
                if (success) simulateFlintAndSteel = true;
            } else if (block instanceof AbstractHardcoreTorchBlock && Mod.config.starterLightTorches) {
                if (((AbstractHardcoreTorchBlock) block).burnState != ETorchState.LIT) {
                    attempt = true;
                    if (success) ((AbstractHardcoreTorchBlock) block).light(world, pos, world.getBlockState(pos));
                }
            } else if (block instanceof AbstractLanternBlock && Mod.config.starterLightLanterns) {
                if (((AbstractLanternBlock) block).canLight(world, pos)) {
                    attempt = true;
                    if (success) ((AbstractLanternBlock) block).light(world, pos, world.getBlockState(pos));
                }
            } else if (Mod.config.starterStartFires) {
                attempt = true;
                if (success) simulateFlintAndSteel = true;
            }

            if (simulateFlintAndSteel)
                Items.FLINT_AND_STEEL.useOnBlock(new ItemUsageContext((PlayerEntity) user, user.getActiveHand(), hit));
        }
        if (attempt){
            stack.increment(-1);
        if (simulateFlintAndSteel) {
            world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_EXTINGUISH, SoundCategory.BLOCKS, 2, 1);
            world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_AMBIENT, SoundCategory.BLOCKS, 2, 2);
            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1f, 1f);
            world.playSound(null, pos, SoundEvents.BLOCK_CANDLE_AMBIENT, SoundCategory.BLOCKS, 2f, 2f);
            serverWorld.spawnParticles(ParticleTypes.SMOKE, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 0.25f, 0.25f, 0.25f, 0.001f);
            serverWorld.spawnParticles(ParticleTypes.SMALL_FLAME, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 4, 0.25f, 0.25f, 0.25f, 0.001f);
            serverWorld.spawnParticles(ParticleTypes.LAVA, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 2, 0.25f, 0.25f, 0.25f, 0.001f);
            serverWorld.spawnParticles(ParticleTypes.FLAME, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 4, 0.25f, 0.25f, 0.25f, 0.001f);
        } else {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.4f, 2f);
                serverWorld.spawnParticles(ParticleTypes.SMOKE, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, 10, 0.25f, 0.25f, 0.25f, 0.001f);
        }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return USE_DURATION;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        world.playSound(null, user.getBlockPos(), SoundEvents.ITEM_CROSSBOW_LOADING_START, SoundCategory.BLOCKS, 1f, 0f);

        return super.use(world, user, hand);
    }
}