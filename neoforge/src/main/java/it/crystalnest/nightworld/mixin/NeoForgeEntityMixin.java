package it.crystalnest.nightworld.mixin;

import it.crystalnest.nightworld.Constants;
import it.crystalnest.nightworld.api.EntityPortal;
import it.crystalnest.nightworld.api.NightworldPortalChecker;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Injects into {@link Entity} to alter dimension travel.
 */
@Mixin(Entity.class)
public abstract class NeoForgeEntityMixin implements EntityPortal {
  /**
   * Shadowed {@link Entity#level()}.
   */
  @Shadow
  public abstract Level level();

  /**
   * Redirects the call to {@link Entity#level()} inside the method {@link Entity#handleNetherPortal()}.<br />
   * Optionally changes the destination dimension.
   *
   * @param instance {@link MinecraftServer} owning the redirected method.
   * @param worldKey dimension key.
   * @return correct destination dimension.
   */
  @Redirect(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
  private ServerLevel redirectGetLevel(MinecraftServer instance, ResourceKey<Level> worldKey) {
    ServerLevel destination = instance.getLevel(worldKey);
    if (destination != null && NightworldPortalChecker.shouldTeleportToNightworld(level(), destination, portalEntrancePos())) {
      destination = instance.getLevel(level().dimension() == Level.OVERWORLD ? Constants.NIGHTWORLD : Level.OVERWORLD);
    }
    return destination;
  }
}
