package it.crystalnest.nightworld.handlers;

import it.crystalnest.nightworld.Constants;
import it.crystalnest.nightworld.api.NightworldPortalChecker;
import it.crystalnest.nightworld.api.Teleportable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;

/**
 * {@link EntityTravelToDimensionEvent} handler.
 */
@EventBusSubscriber(bus = Bus.FORGE)
public final class EntityTravelToDimensionEventHandler {
  private EntityTravelToDimensionEventHandler() {}

  /**
   * Handles the {@link EntityTravelToDimensionEvent} by optionally setting the custom portal info for the entity.
   *
   * @param event {@link EntityTravelToDimensionEvent}.
   */
  @SubscribeEvent
  public static void handle(EntityTravelToDimensionEvent event) {
    Entity entity = event.getEntity();
    MinecraftServer server = entity.getServer();
    if (server != null && !entity.isRemoved() && (entity.level().dimension() == Constants.NIGHTWORLD || event.getDimension() == Constants.NIGHTWORLD)) {
      ((Teleportable) entity).setCustomPortalInfo(NightworldPortalChecker.getNightworldPortalInfo(entity, server.getLevel(event.getDimension())));
    }
  }
}
