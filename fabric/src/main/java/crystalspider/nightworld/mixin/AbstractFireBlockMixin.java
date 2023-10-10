package crystalspider.nightworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import crystalspider.nightworld.NightworldLoader;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@Mixin(AbstractFireBlock.class)
public abstract class AbstractFireBlockMixin {
  /**
   * Shadowed {@link AbstractFireBlock#isOverworldOrNether(World)}.
   * 
   * @return whether the world is Overworld or Nether.
   */
  @Shadow
  private static boolean isOverworldOrNether(World world) {
    throw new UnsupportedOperationException("Tried to call a dummy body of a shadowed method: AbstractFireBlock#isOverworldOrNether(World)");
  }

  @Redirect(method = "onBlockAdded", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractFireBlock;isOverworldOrNether(Lnet/minecraft/world/World;)Z"))
  private boolean redirectIsOverworldOrNether$onBlockAdded(World world) {
    return AbstractFireBlockMixin.isNightworldSuitableDimension(world);
  }

  @Redirect(method = "shouldLightPortalAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/AbstractFireBlock;isOverworldOrNether(Lnet/minecraft/world/World;)Z"))
  private static boolean redirectIsOverworldOrNether$shouldLightPortalAt(World world) {
    return AbstractFireBlockMixin.isNightworldSuitableDimension(world);
  }

  @ModifyVariable(method = "shouldLightPortalAt", at = @At(value = "INVOKE", target="Lnet/minecraft/util/math/Direction;values()[Lnet/minecraft/util/math/Direction;"))
	private static boolean onShouldLightPortalAt(boolean bl, World world, BlockPos blockPos, Direction direction) {
		BlockPos.Mutable mutableBlockPos = blockPos.mutableCopy();
		Direction[] directions = Direction.values();
    boolean b1 = false;
		for (Direction direction2 : directions) {
			if (!world.getBlockState(mutableBlockPos.set(blockPos).move(direction2)).isOf(Blocks.CRYING_OBSIDIAN)) continue;
      b1 = true;
      break;
		}
		return b1;
	}

  /**
   * Checks if the given {@link World} is a suitable dimension to light up a Nightworld portal.
   * 
   * @param world
   * @return
   */
  private static boolean isNightworldSuitableDimension(World world) {
    return AbstractFireBlockMixin.isOverworldOrNether(world) || world.getRegistryKey() == NightworldLoader.NIGHTWORLD;
  }
}
