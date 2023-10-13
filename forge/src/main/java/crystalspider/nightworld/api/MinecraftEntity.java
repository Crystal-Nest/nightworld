package crystalspider.nightworld.api;

import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Utility wrapper for {@link Entity}.
 */
public interface MinecraftEntity {
  /**
   * Refers to {@link Entity#getLevel()}.
   * 
   * @return
   */
  Level level();
  /**
   * Refers to {@link Entity#getRelativePortalPosition(Axis, FoundRectangle)}.
   * 
   * @param axis
   * @param rectangle
   * @return
   */
  Vec3 getRelativePortalPosition(Axis axis, FoundRectangle rectangle);
}
