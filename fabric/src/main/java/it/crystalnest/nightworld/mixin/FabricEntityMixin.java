package it.crystalnest.nightworld.mixin;

import it.crystalnest.nightworld.Constants;
import it.crystalnest.nightworld.api.NightworldPortalChecker;
import net.fabricmc.fabric.impl.dimension.Teleportable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.PortalInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Injects into {@link Entity} to alter dimension travel.
 */
@Mixin(Entity.class)
public abstract class FabricEntityMixin {
  /**
   * Shadowed {@link Entity#portalEntrancePos}.
   */
  @Shadow
  protected BlockPos portalEntrancePos;

  /**
   * Shadowed {@link Entity#level}.
   */
  @Shadow
  public Level level;

  /**
   * Shadowed {@link Entity#isRemoved()}.
   *
   * @return whether the entity is removed
   */
  @Shadow
  public abstract boolean isRemoved();

  /**
   * Shadowed {@link Entity#changeDimension(ServerLevel)}.
   *
   * @param destination dimension.
   * @return entity in the new dimension.
   */
  @Shadow
  public abstract Entity changeDimension(ServerLevel destination);

  /**
   * Redirects the call to {@link Entity#handleNetherPortal()} inside the method {@link Entity#tick()}.<br />
   * Changes the {@link PortalInfo} if the entity is in a Nightworld Portal.
   *
   * @param instance {@link Entity} owning the redirected method.
   * @param destination dimension.
   * @return entity in the new dimension.
   */
  @Redirect(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;"))
  private Entity redirectChangeDimension(Entity instance, ServerLevel destination) {
    ServerLevel actualDestination = destination;
    if (!level.isClientSide && !this.isRemoved() && NightworldPortalChecker.shouldTeleportToNightworld(level, destination, portalEntrancePos)) {
      actualDestination = ((ServerLevel) level).getServer().getLevel(level.dimension() == Level.OVERWORLD ? Constants.NIGHTWORLD : Level.OVERWORLD);
      //noinspection UnstableApiUsage
      ((Teleportable) this).fabric_setCustomTeleportTarget(NightworldPortalChecker.getNightworldPortalInfo(instance, actualDestination));
    }
    return this.changeDimension(actualDestination);
  }

  /**
   * Injects into the method {@link Entity#changeDimension(ServerLevel)} after the call to {Entity#getTeleportTarget(ServerWorld)}.<br />
   * Resets the {@link net.fabricmc.fabric.mixin.dimension.EntityMixin#customTeleportTarget customTeleportTarget}.
   *
   * @param destination dimension.
   * @param cir {@link CallbackInfoReturnable}.
   */
  @SuppressWarnings("UnstableApiUsage")
  @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;findDimensionEntryPoint(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/level/portal/PortalInfo;", shift = Shift.AFTER))
  private void onChangeDimension(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
    ((Teleportable) this).fabric_setCustomTeleportTarget(null);
  }
}
