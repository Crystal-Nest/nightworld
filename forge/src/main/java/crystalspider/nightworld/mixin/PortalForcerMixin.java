package crystalspider.nightworld.mixin;

import java.util.function.Predicate;
import java.util.stream.Stream;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crystalspider.nightworld.NightworldLoader;
import crystalspider.nightworld.api.NightworldPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalForcer;

/**
 * Injects into {@link PortalForcer} to alter portal creation and location.
 */
@Mixin(PortalForcer.class)
public abstract class PortalForcerMixin {
    /**
   * Shadowed {@link PortalForcer#level}.
   */
  @Shadow
  private ServerLevel level;

  /**
   * Redirects the call to {@link ServerLevel#setBlockAndUpdate(BlockPos, BlockState)} inside the method {@link PortalForcer#createPortal(BlockPos, Axis)}.
   * <p>
   * Calls the same redirected method, but with the correct {@link BlockState}.
   * 
   * @param caller
   * @param pos
   * @param state
   * @return whether the {@link BlockState} has been set in the {@link ServerLevel}.
   */
  @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlockAndUpdate(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
  private boolean redirectSetBlockStateNoFlags$createPortal(ServerLevel caller, BlockPos pos, BlockState state) {
    return caller.setBlockAndUpdate(pos, getCorrectBlockState(caller, state));
  }

  /**
   * Redirects the call to {@link ServerLevel#setBlock(BlockPos, BlockState, int)} inside the method {@link PortalForcer#createPortal(BlockPos, Axis)}.
   * <p>
   * Calls the same redirected method, but with the correct {@link BlockState}.
   * 
   * @param caller
   * @param pos
   * @param state
   * @param flags
   * @return whether the {@link BlockState} has been set in the {@link ServerLevel}.
   */
  @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", ordinal = 0))
  private boolean redirectSetBlockStateWithFlags$createPortal(ServerLevel caller, BlockPos pos, BlockState state, int flags) {
    return caller.setBlock(pos, getCorrectBlockState(caller, state), flags);
  }

  /**
   * Redirects the call to {@link Stream#filter(Predicate)} inside the method {@link PortalForcer#findPortalAround(BlockPos, boolean, WorldBorder)}.
   * <p>
   * Adds a new condition to the predicate to prevent teleporting from Nether Portals to Nightworld Portals and vice versa.
   * 
   * @param caller
   * @param predicate
   * @return
   */
  @Redirect(method = "findPortalAround", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;filter(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;", ordinal = 1))
  private Stream<PoiRecord> redirectFilter(Stream<PoiRecord> caller, Predicate<? super PoiRecord> predicate) {
    return caller.filter(poi -> predicate.test(poi) && (level.dimension() != Level.OVERWORLD || NightworldLoader.nightworldOriginThread.get() == NightworldPortalChecker.isNightworldPortal(level, poi.getPos())));
  }

  /**
   * Returns the correct {@link BlockState} to create a portal.
   * 
   * @param world destination {@link ServerLevel world}.
   * @param state
   * @return the correct {@link BlockState} to create a portal.
   */
  private BlockState getCorrectBlockState(ServerLevel world, BlockState state) {
    return (world.dimension() == NightworldLoader.NIGHTWORLD || NightworldLoader.nightworldOriginThread.get()) && state.is(Blocks.OBSIDIAN) ? Blocks.CRYING_OBSIDIAN.defaultBlockState() : state;
  }
}
