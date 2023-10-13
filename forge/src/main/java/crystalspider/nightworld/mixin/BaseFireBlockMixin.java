package crystalspider.nightworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crystalspider.nightworld.NightworldLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Injects into {@link BaseFireBlock} to alter dimension travel.
 */
@Mixin(BaseFireBlock.class)
public abstract class BaseFireBlockMixin {
  /**
   * Shadowed {@link BaseFireBlock#inPortalDimension(Level)}.
   * 
   * @return whether the {@link Level} is {@link Level#OVERWORLD Overworld} or {@link Level#NETHER Nether}.
   */
  @Shadow
  private static boolean inPortalDimension(Level world) {
    throw new UnsupportedOperationException("Tried to call a dummy body of a shadowed method: BaseFireBlock#inPortalDimension(Level)");
  }
  
  /**
   * Redirects the call to {@link BaseFireBlock#inPortalDimension(Level)} inside the method {@link BaseFireBlock#onPlace(BlockState, Level, BlockPos, BlockState, boolean)}.
   * <p>
   * Checks also whether the {@link Level} is suitable for a Nightworld Portal.
   * 
   * @param world
   * @return check result.
   */
  @Redirect(method = "onPlace", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseFireBlock;inPortalDimension(Lnet/minecraft/world/level/Level;)Z"))
  private boolean redirectIsOverworldOrNether$onBlockAdded(Level world) {
    return BaseFireBlockMixin.isNightworldSuitableDimension(world);
  }

  /**
   * Redirects the call to {@link BaseFireBlock#inPortalDimension(Level)} inside the method {@link BaseFireBlock#isPortal(Level, BlockPos, Direction)}.
   * <p>
   * Checks also whether the {@link Level} is suitable for a Nightworld Portal.
   * 
   * @param world
   * @return check result.
   */
  @Redirect(method = "isPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/BaseFireBlock;inPortalDimension(Lnet/minecraft/world/level/Level;)Z"))
  private static boolean redirectIsOverworldOrNether$isPortal(Level world) {
    return BaseFireBlockMixin.isNightworldSuitableDimension(world);
  }

  /**
   * Redirects the call to {@link Level#getBlockState(BlockPos)} inside the method {@link BaseFireBlock#isPortal(Level, BlockPos, Direction)}.
   * <p>
   * If the {@link BlockState} is {@link Blocks#CRYING_OBSIDIAN Crying Obsidian}, returns {@link Blocks#OBSIDIAN Obsidian} instead.
   * 
   * @param caller
   * @param pos
   * @return {@link Blocks#OBSIDIAN Obsidian} or the original {@link BlockState}.
   */
  @Redirect(method = "isPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"))
  private static BlockState redirectIsOf$isPortal(Level caller, BlockPos pos) {
    return caller.getBlockState(pos).is(Blocks.CRYING_OBSIDIAN) ? Blocks.OBSIDIAN.defaultBlockState() : caller.getBlockState(pos);
  }

  /**
   * Checks whether the given {@link Level} is a suitable dimension to light up a Nightworld portal.
   * 
   * @param world
   * @return whether the given {@link Level} is a suitable dimension to light up a Nightworld portal.
   */
  private static boolean isNightworldSuitableDimension(Level world) {
    return BaseFireBlockMixin.inPortalDimension(world) || world.dimension() == NightworldLoader.NIGHTWORLD;
  }
}
