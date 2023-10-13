package crystalspider.nightworld.handlers;

import crystalspider.nightworld.NightworldLoader;
import crystalspider.nightworld.api.EntityPortal;
import crystalspider.nightworld.api.Teleportable;
import net.minecraft.BlockUtil;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * {@link EntityTravelToDimensionEvent} handler.
 */
@EventBusSubscriber(bus = Bus.FORGE)
public class EntityTravelToDimensionEventHandler {
  /**
   * Handles the {@link EntityTravelToDimensionEvent} by optionally setting the custom portal info for the entity.
   * 
   * @param event
   */
  @SubscribeEvent
  public static void handle(EntityTravelToDimensionEvent event) {
    Entity entity = event.getEntity();
    MinecraftServer server = entity.getServer();
    if (server != null && !entity.isRemoved() && (entity.getLevel().dimension() == NightworldLoader.NIGHTWORLD || event.getDimension() == NightworldLoader.NIGHTWORLD)) {
      ((Teleportable) entity).setCustomPortalInfo(getNightworldPortalInfo(entity, server.getLevel(event.getDimension())));
    }
  }

  /**
   * Returns the exit portal portal info for traveling from or to the Nightworld.
   * 
   * @param entity
   * @param destination
   * @return
   */
  private static PortalInfo getNightworldPortalInfo(Entity entity, ServerLevel destination) {
    return ((EntityPortal) entity).exitPortal(destination, entity.blockPosition(), false, destination.getWorldBorder()).map(rect -> {
      Vec3 vec3d;
      Axis axis;
      BlockState blockState = entity.getLevel().getBlockState(((EntityPortal) entity).portalEntrancePos());
      if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
        axis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        FoundRectangle rectangle = BlockUtil.getLargestRectangleAround(((EntityPortal) entity).portalEntrancePos(), axis, PortalShape.MAX_WIDTH, Axis.Y, PortalShape.MAX_HEIGHT, pos -> entity.getLevel().getBlockState(pos) == blockState);
        vec3d = ((EntityPortal) entity).relativePortalPosition(axis, rectangle);
      } else {
        axis = Axis.X;
        vec3d = new Vec3(0.5, 0.0, 0.0);
      }
      return PortalShape.createPortalInfo(destination, rect, axis, vec3d, entity.getDimensions(entity.getPose()), entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
    }).orElse(null);
  }
}
