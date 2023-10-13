package crystalspider.nightworld.handlers;

import crystalspider.nightworld.NightworldLoader;
import crystalspider.nightworld.api.EntityPortal;
import crystalspider.nightworld.api.NightworldPortalChecker;
import crystalspider.nightworld.api.Teleportable;
import net.minecraft.BlockUtil;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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
 * 
 */
@EventBusSubscriber(bus = Bus.FORGE)
public class EntityTravelToDimensionEventHandler {
  /**
   * 
   * 
   * @param event
   */
  @SubscribeEvent
  public static void handle(EntityTravelToDimensionEvent event) {
    Entity entity = event.getEntity();
    MinecraftServer server = entity.getServer();
    if (server != null) {
      Level origin = entity.getLevel();
      ServerLevel destination = server.getLevel(event.getDimension());
      if (
        destination != null &&
        !entity.isRemoved() &&
        (origin.dimension() == Level.OVERWORLD || origin.dimension() == NightworldLoader.NIGHTWORLD) &&
        destination.dimension() == Level.NETHER &&
        NightworldPortalChecker.isNightworldPortal(origin, ((EntityPortal) entity).portalEntrancePos())
      ) {
        destination = server.getLevel(origin.dimension() == Level.OVERWORLD ? NightworldLoader.NIGHTWORLD : Level.OVERWORLD);
        ((Teleportable) entity).setCustomPortalInfo(getNightworldTeleportTarget(entity, destination));
      }
    }
  }

  /**
   * 
   * 
   * @param entity
   * @param destination
   * @return
   */
  private static PortalInfo getNightworldTeleportTarget(Entity entity, ServerLevel destination) {
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
      return PortalShape.createPortalInfo(destination, rect, axis, vec3d, entity, entity.getDeltaMovement(), entity.getYRot(), entity.getXRot());
    }).orElse(null);
  }
}
