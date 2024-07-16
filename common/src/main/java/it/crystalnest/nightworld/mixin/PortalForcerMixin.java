package it.crystalnest.nightworld.mixin;

import it.crystalnest.nightworld.Constants;
import it.crystalnest.nightworld.api.NightworldPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalForcer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Injects into {@link PortalForcer} to alter portal creation and location.
 */
@Mixin(PortalForcer.class)
public abstract class PortalForcerMixin {
  /**
   * Shadowed {@link PortalForcer#level}.
   */
  @Final
  @Shadow
  private ServerLevel level;

  /**
   * Returns the correct {@link BlockState} to create a portal.
   *
   * @param world destination {@link ServerLevel world}.
   * @param state block state.
   * @return the correct {@link BlockState} to create a portal.
   */
  @Unique
  private BlockState getCorrectBlockState(ServerLevel world, BlockState state) {
    return world.dimension() == Constants.NIGHTWORLD && state.is(Blocks.OBSIDIAN) ? Blocks.CRYING_OBSIDIAN.defaultBlockState() : state;
  }

  /**
   * Redirects the call to {@link ServerLevel#setBlockAndUpdate(BlockPos, BlockState)} inside the method {@link PortalForcer#createPortal(BlockPos, Axis)}.<br />
   * Calls the same redirected method, but with the correct {@link BlockState}.
   * 
   * @param instance {@link ServerLevel} owning the redirected method.
   * @param pos block position.
   * @param state block state.
   * @return whether the {@link BlockState} has been set in the {@link ServerLevel}.
   */
  @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
  private boolean redirectSetBlockStateNoFlags$createPortal(ServerLevel instance, BlockPos pos, BlockState state) {
    return instance.setBlockAndUpdate(pos, getCorrectBlockState(instance, state));
  }

  /**
   * Redirects the call to {@link ServerLevel#setBlock(BlockPos, BlockState, int)} inside the method {@link PortalForcer#createPortal(BlockPos, Axis)}.<br />
   * Calls the same redirected method, but with the correct {@link BlockState}.
   *
   * @param instance {@link ServerLevel} owning the redirected method.
   * @param pos block position.
   * @param state block state.
   * @param flags update flags.
   * @return whether the {@link BlockState} has been set in the {@link ServerLevel}.
   */
  @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0))
  private boolean redirectSetBlockStateWithFlags$createPortal(ServerLevel instance, BlockPos pos, BlockState state, int flags) {
    return instance.setBlock(pos, getCorrectBlockState(instance, state), flags);
  }

  /**
   * Redirects the call to {@link Stream#filter(Predicate)} inside the method {@link PortalForcer#findClosestPortalPosition(BlockPos, boolean, WorldBorder)}.<br />
   * Adds a new condition to the predicate to prevent teleporting from Nether Portals to Nightworld Portals and vice versa.
   * 
   * @param instance stream of {@link BlockPos}s owning the redirected method.
   * @param predicate whether the portal is within bounds.
   * @return filtered stream of {@link BlockPos}s that represent matching portals.
   */
  @Redirect(method = "findClosestPortalPosition", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 1))
  private Stream<BlockPos> redirectFilter(Stream<BlockPos> instance, Predicate<? super BlockPos> predicate) {
    return instance.filter(pos -> predicate.test(pos) && (level.dimension() != Level.OVERWORLD || (Constants.NIGHTWORLD_ORIGIN_THREAD.get() == NightworldPortalChecker.isNightworldPortal(level, pos))));
  }
}
