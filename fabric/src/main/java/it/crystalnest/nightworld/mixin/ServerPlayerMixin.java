package it.crystalnest.nightworld.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;

import net.fabricmc.fabric.impl.dimension.Teleportable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


/**
 * Injects into {@link ServerPlayerMixin} to alter dimension travel.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Entity {
  /**
   * Mock constructor to make the compiler happy.
   */
  private ServerPlayerMixin(EntityType<?> type, Level level) {
    super(type, level);
  }

  /**
   * Injects into the method {@link ServerPlayerEntity#moveToWorld(ServerWorld)} after the call to {@link ServerPlayerEntity#getTeleportTarget(ServerWorld)}.
   * <p>
   * Resets the {@link net.fabricmc.fabric.mixin.dimension.EntityMixin#customTeleportTarget customTeleportTarget}.
   * 
   * @param destination
   * @param cir
   */
  @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;findDimensionEntryPoint(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/level/portal/PortalInfo;", shift = Shift.AFTER))
  private void changeDimension(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
    ((Teleportable) this).fabric_setCustomTeleportTarget(null);
  }
}
