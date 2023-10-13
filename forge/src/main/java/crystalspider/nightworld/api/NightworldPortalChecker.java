package crystalspider.nightworld.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.portal.PortalShape;

/**
 * Handles checking whether a Nether Portal frame makes the portal a Nightworld Portal.
 */
public interface NightworldPortalChecker {
  /**
   * Checks if there is a Nightworld Portal in the given {@link Level world} at the given {@link BlockPos position}.
   * 
   * @param world
   * @param pos
   * @return
   */
  public static boolean isNightworldPortal(Level world, BlockPos pos) {
    return ((NightworldPortalChecker) new PortalShape(world, pos, world.getBlockState(pos).getOptionalValue(NetherPortalBlock.AXIS).orElse(Axis.X))).isNightworldPortal();
  }

  /**
   * Whether the portal is a Nightworld Portal.
   * 
   * @return
   */
  public boolean isNightworldPortal();
}
