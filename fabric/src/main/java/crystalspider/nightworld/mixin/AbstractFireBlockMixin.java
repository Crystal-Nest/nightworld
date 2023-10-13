package crystalspider.nightworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crystalspider.nightworld.NightworldLoader;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

/**
 * Injects into {@link AbstractFireBlock} to alter dimension travel.
 */
@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {
  /**
   * Shadowed {@link AbstractFireBlock#isOverworldOrNether(World)}.
   * 
   * @return whether the {@link World} is {@link World#OVERWORLD Overworld} or {@link World#NETHER Nether}.
   */
  @Shadow
  private static boolean isOverworldOrNether(World world) {
    throw new UnsupportedOperationException("Tried to call a dummy body of a shadowed method: AbstractFireBlock#isOverworldOrNether(World)");
  }

  /**
   * Redirects the call to {@link AbstractFireBlock#isOverworldOrNether(World)} inside the method {@link AbstractFireBlock#onBlockAdded(BlockState, World, BlockPos, BlockState, boolean)}.
   * <p>
   * Checks also whether the {@link World} is suitable for a Nightworld Portal.
   * 
   * @param world
   * @return check result.
   */
  @Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractFireBlock;isOverworldOrNether(Lnet/minecraft/world/World;)Z"))
  private boolean redirectIsOverworldOrNether$onBlockAdded(World world) {
    return AbstractFireBlockMixin.isNightworldSuitableDimension(world);
  }

  /**
   * Redirects the call to {@link AbstractFireBlock#isOverworldOrNether(World)} inside the method {@link AbstractFireBlock#shouldLightPortalAt(World, BlockPos, Direction)}.
   * <p>
   * Checks also whether the {@link World} is suitable for a Nightworld Portal.
   * 
   * @param world
   * @return check result.
   */
  @Redirect(method = "shouldLightPortalAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractFireBlock;isOverworldOrNether(Lnet/minecraft/world/World;)Z"))
  private static boolean redirectIsOverworldOrNether$shouldLightPortalAt(World world) {
    return AbstractFireBlockMixin.isNightworldSuitableDimension(world);
  }

  /**
   * Redirects the call to {@link BlockState#isOf(Block)} inside the method {@link AbstractFireBlock#shouldLightPortalAt(World, BlockPos, Direction)}.
   * <p>
   * Also checks if the {@link BlockState} is {@link Blocks#CRYING_OBSIDIAN Crying Obsidian}.
   * 
   * @param caller
   * @param block
   * @return whether the {@link BlockState} is of the given {@link Block} or {@link Blocks#CRYING_OBSIDIAN Crying Obsidian}.
   */
  @Redirect(method = "shouldLightPortalAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isOf(Lnet/minecraft/block/Block;)Z"))
  private static boolean redirectIsOf$shouldLightPortalAt(BlockState caller, Block block) {
    return caller.isOf(block) || caller.isOf(Blocks.CRYING_OBSIDIAN);
  }

  /**
   * Checks whether the given {@link World} is a suitable dimension to light up a Nightworld portal.
   * 
   * @param world
   * @return whether the given {@link World} is a suitable dimension to light up a Nightworld portal.
   */
  private static boolean isNightworldSuitableDimension(World world) {
    return AbstractFireBlockMixin.isOverworldOrNether(world) || world.getRegistryKey() == NightworldLoader.NIGHTWORLD;
  }
}
