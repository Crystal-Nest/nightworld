package crystalspider.nightworld.api;

import net.minecraft.block.NetherPortalBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.minecraft.world.dimension.NetherPortal;

/**
 * Handles checking whether a Nether Portal frame makes the portal a Nightworld Portal.
 */
public interface NightworldPortalChecker {
  /**
   * Checks if there is a Nightworld Portal in the given {@link World world} at the given {@link BlockPos position}.
   * 
   * @param world
   * @param pos
   * @return
   */
  public static boolean isNightworldPortal(World world, BlockPos pos) {
    return ((NightworldPortalChecker) new NetherPortal(world, pos, world.getBlockState(pos).getOrEmpty(NetherPortalBlock.AXIS).orElse(Axis.X))).isNightworldPortal();
  }

  /**
   * Whether the portal is a Nightworld Portal.
   * 
   * @return
   */
  public boolean isNightworldPortal();
}
