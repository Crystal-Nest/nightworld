package crystalspider.nightworld.api;

import java.util.Optional;

import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;

/**
 * An {@link Entity} interacting with a Portal.
 */
public interface EntityPortal {
  /**
   * Returns the optional exit portal rectangle.
   * 
   * @param destination
   * @param pos
   * @param destIsNether
   * @param worldBorder
   * @return
   */
  Optional<FoundRectangle> exitPortal(ServerLevel destination, BlockPos pos, boolean destIsNether, WorldBorder worldBorder);
  /**
   * Returns the relative portal position.
   * 
   * @param axis
   * @param rectangle
   * @return
   */
  Vec3 relativePortalPosition(Axis axis, FoundRectangle rectangle);
  /**
   * Returns the portal entrance position.
   * 
   * @return
   */
  BlockPos portalEntrancePos();
}
