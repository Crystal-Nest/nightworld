package it.crystalnest.nightworld.api;

import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * An {@link Entity} interacting with a Portal.
 */
public interface EntityPortal {
  /**
   * Returns the optional exit portal rectangle.
   *
   * @param destination destination level.
   * @param pos destination position.
   * @param destIsNether whether the destination is the Nether.
   * @param worldBorder world border.
   * @return optional exit portal rectangle.
   */
  Optional<FoundRectangle> exitPortal(ServerLevel destination, BlockPos pos, boolean destIsNether, WorldBorder worldBorder);

  /**
   * Returns the relative portal position.
   *
   * @param axis portal alignment (X or Z).
   * @param rectangle portal rectangle.
   * @return relative portal position.
   */
  Vec3 relativePortalPosition(Axis axis, FoundRectangle rectangle);

  /**
   * Returns the portal entrance position.
   *
   * @return portal entrance position.
   */
  BlockPos portalEntrancePos();
}
