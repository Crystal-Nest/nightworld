package it.crystalnest.nightworld.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.portal.PortalShape;

/**
 * Handles checking whether a portal frame is for a Nightworld Portal.
 */
public interface NightworldPortalChecker {
  /**
   * Checks whether there is a Nightworld Portal in the given dimension at the given position.
   *
   * @param level dimension.
   * @param pos position.
   * @return whether there is a Nightworld portal.
   */
  static boolean isNightworldPortal(Level level, BlockPos pos) {
    return ((NightworldPortalChecker) new PortalShape(level, pos, level.getBlockState(pos).getOptionalValue(NetherPortalBlock.AXIS).orElse(Axis.X))).isNightworldPortal();
  }

  /**
   * Whether the portal is a Nightworld Portal.
   *
   * @return whether the portal is a Nightworld Portal.
   */
  boolean isNightworldPortal();
}
