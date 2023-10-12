package crystalspider.nightworld.mixin;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crystalspider.nightworld.NightworldLoader;
import crystalspider.nightworld.api.NightworldPortalChecker;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.poi.PointOfInterest;

/**
 * Injects into {@link PortalForcer} to alter portal creation and location.
 */
@Mixin(PortalForcer.class)
public abstract class PortalForcerMixin {
  /**
   * Shadowed {@link PortalForcer#world}.
   */
  @Shadow
  private ServerWorld world;

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
   * Redirects the call to {@link Stream#filter(Predicate)} inside the method {@link PortalForcer#getPortalRect(BlockPos, boolean, WorldBorder)}.
   * <p>
   * Adds a new condition to the predicate to prevent teleporting from Nether Portals to Nightworld Portals and vice versa.
   * 
   * @param caller
   * @param predicate
   * @return
   */
  @Redirect(method = "getPortalRect", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 1))
  private Stream<PointOfInterest> redirectFilter(Stream<PointOfInterest> caller, Predicate<? super PointOfInterest> predicate) {
    return caller.filter(poi -> predicate.test(poi) && (world.getRegistryKey() != World.OVERWORLD || NightworldLoader.nightworldOriginThread.get() == NightworldPortalChecker.isNightworldPortal(world, poi.getPos())));
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
