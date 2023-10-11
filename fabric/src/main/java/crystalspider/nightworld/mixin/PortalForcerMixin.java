package crystalspider.nightworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crystalspider.nightworld.NightworldLoader;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.PortalForcer;

/**
 * Injects into {@link PortalForcer} to alter portal creation and location.
 */
@Mixin(PortalForcer.class)
public abstract class PortalForcerMixin {
  /**
   * Redirects the call to {@link ServerWorld#setBlockState(BlockPos, BlockState)} inside the method {@link PortalForcer#createPortal(BlockPos, Axis)}.
   * <p>
   * Calls the same redirected method, but with the correct {@link BlockState}.
   * 
   * @param caller
   * @param pos
   * @param state
   * @return whether the {@link BlockState} has been set in the {@link ServerWorld}.
   */
  @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
  private boolean redirectSetBlockStateNoFlags$createPortal(ServerWorld caller, BlockPos pos, BlockState state) {
    return caller.setBlockState(pos, getCorrectBlockState(caller, state));
  }

  /**
   * Redirects the call to {@link ServerWorld#setBlockState(BlockPos, BlockState, int)} inside the method {@link PortalForcer#createPortal(BlockPos, Axis)}.
   * <p>
   * Calls the same redirected method, but with the correct {@link BlockState}.
   * 
   * @param caller
   * @param pos
   * @param state
   * @param flags
   * @return whether the {@link BlockState} has been set in the {@link ServerWorld}.
   */
  @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 0))
  private boolean redirectSetBlockStateWithFlags$createPortal(ServerWorld caller, BlockPos pos, BlockState state, int flags) {
    return caller.setBlockState(pos, getCorrectBlockState(caller, state), flags);
  }

  /**
   * Returns the correct {@link BlockState} to create a portal.
   * 
   * @param world destination {@link ServerWorld world}.
   * @param state
   * @return the correct {@link BlockState} to create a portal.
   */
  private BlockState getCorrectBlockState(ServerWorld world, BlockState state) {
    return (world.getRegistryKey() == NightworldLoader.NIGHTWORLD || NightworldLoader.nightworldOriginThread.get()) && state.isOf(Blocks.OBSIDIAN) ? Blocks.CRYING_OBSIDIAN.getDefaultState() : state;
  }
}
