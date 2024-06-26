package it.crystalnest.nightworld.mixin;

import it.crystalnest.nightworld.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Injects into {@link BaseFireBlock} to alter dimension travel.
 */
@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {
  /**
   * Shadowed {@link BaseFireBlock#inPortalDimension(Level)}.
   *
   * @return whether the dimension is {@link Level#OVERWORLD Overworld} or {@link Level#NETHER Nether}.
   */
  @Shadow
  private static boolean inPortalDimension(Level world) {
    throw new UnsupportedOperationException("Tried to call a dummy body of a shadowed method: BaseFireBlock#inPortalDimension(Level)");
  }

  /**
   * Checks whether the given dimension is a suitable to light up a Nightworld portal.
   *
   * @param level dimension.
   * @return whether the given dimension is a suitable to light up a Nightworld portal.
   */
  @Unique
  private static boolean isNightworldSuitableDimension(Level level) {
    return BaseFireBlockMixin.inPortalDimension(level) || level.dimension() == Constants.NIGHTWORLD;
  }

  /**
   * Redirects the call to {@link BaseFireBlock#inPortalDimension(Level)} inside the method {@link BaseFireBlock#isPortal(Level, BlockPos, Direction)}.<br />
   * Checks also whether the dimension is suitable for a Nightworld Portal.
   *
   * @param level dimension.
   * @return check result.
   */
  @Redirect(method = "isPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseFireBlock;inPortalDimension(Lnet/minecraft/world/level/Level;)Z"))
  private static boolean redirectInPortalDimension$isPortal(Level level) {
    return BaseFireBlockMixin.isNightworldSuitableDimension(level);
  }

  /**
   * Redirects the call to {@link Level#getBlockState(BlockPos)} inside the method {@link BaseFireBlock#isPortal(Level, BlockPos, Direction)}.<br />
   * If the {@link BlockState} is {@link Blocks#CRYING_OBSIDIAN Crying Obsidian}, returns {@link Blocks#OBSIDIAN Obsidian} instead.
   *
   * @param instance {@link Level} owning the redirected method.
   * @param pos position.
   * @return {@link Blocks#OBSIDIAN Obsidian} or the original {@link BlockState}.
   */
  @Redirect(method = "isPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
  private static BlockState redirectGetBlockState(Level instance, BlockPos pos) {
    return instance.getBlockState(pos).is(Blocks.CRYING_OBSIDIAN) ? Blocks.OBSIDIAN.defaultBlockState() : instance.getBlockState(pos);
  }

  /**
   * Redirects the call to {@link BaseFireBlock#inPortalDimension(Level)} inside the method {@link BaseFireBlock#onPlace(BlockState, Level, BlockPos, BlockState, boolean)}.<br />
   * Checks also whether the {@link Level} is suitable for a Nightworld Portal.
   *
   * @param level dimension.
   * @return check result.
   */
  @Redirect(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseFireBlock;inPortalDimension(Lnet/minecraft/world/level/Level;)Z"))
  private boolean redirectInPortalDimension$onPlace(Level level) {
    return BaseFireBlockMixin.isNightworldSuitableDimension(level);
  }
}
