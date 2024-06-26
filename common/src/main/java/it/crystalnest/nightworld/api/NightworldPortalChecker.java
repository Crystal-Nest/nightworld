package it.crystalnest.nightworld.api;

import it.crystalnest.nightworld.Constants;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;

/**
 * Handles checking whether a portal frame is for a Nightworld Portal.
 */
public interface NightworldPortalChecker {
  /**
   * Checks whether an entity should go from {@code origin} to the Nightworld instead of the Nether.
   *
   * @param origin origin dimension.
   * @param destination destination dimension.
   * @param portalEntrancePos portal entrance position.
   * @return whether an entity should go to the Nightworld.
   */
  static boolean shouldTeleportToNightworld(Level origin, Level destination, BlockPos portalEntrancePos) {
    return destination.dimension() == Level.NETHER && (origin.dimension() == Level.OVERWORLD || origin.dimension() == Constants.NIGHTWORLD) && NightworldPortalChecker.isNightworldPortal(origin, portalEntrancePos);
  }

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
   * Returns the exit portal info for traveling from or to the Nightworld.
   *
   * @param entity entity traveling to a new dimension.
   * @param destination default dimension.
   * @return {@link PortalInfo} for a Nightworld portal.
   */
  static PortalInfo getNightworldPortalInfo(Entity entity, ServerLevel destination) {
    return ((EntityPortal) entity).exitPortal(destination, entity.blockPosition(), false, destination.getWorldBorder()).map(rect -> {
      Vec3 vec3d;
      Axis axis;
      BlockState blockState = entity.level.getBlockState(((EntityPortal) entity).portalEntrancePos());
      if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
        axis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        BlockUtil.FoundRectangle rectangle = BlockUtil.getLargestRectangleAround(((EntityPortal) entity).portalEntrancePos(), axis, PortalShape.MAX_WIDTH, Axis.Y, PortalShape.MAX_HEIGHT, pos -> entity.level.getBlockState(pos) == blockState);
        vec3d = ((EntityPortal) entity).relativePortalPosition(axis, rectangle);
      } else {
        axis = Axis.X;
        vec3d = new Vec3(0.5, 0.0, 0.0);
      }
      return PortalShape.createPortalInfo(destination, rect, axis, vec3d, entity, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
    }).orElse(null);
  }

  /**
   * Whether the portal is a Nightworld Portal.
   *
   * @return whether the portal is a Nightworld Portal.
   */
  boolean isNightworldPortal();
}
