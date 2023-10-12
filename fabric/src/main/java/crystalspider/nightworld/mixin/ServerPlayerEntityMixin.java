package crystalspider.nightworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.fabricmc.fabric.impl.dimension.Teleportable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

/**
 * Injects into {@link ServerPlayerEntity} to alter dimension travel.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends Entity {
  /**
   * Mock constructor to make the compiler happy.
   */
  private ServerPlayerEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  /**
   * Injects into the method {@link ServerPlayerEntity#moveToWorld(ServerWorld)} after the call to {@link ServerPlayerEntity#getTeleportTarget(ServerWorld)}.
   * <p>
   * Resets the {@link net.fabricmc.fabric.mixin.dimension.EntityMixin#customTeleportTarget customTeleportTarget}.
   * 
   * @param destination
   * @param cir
   */
  @Inject(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getTeleportTarget(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/world/TeleportTarget;", shift = Shift.AFTER))
  private void onMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
    ((Teleportable) this).fabric_setCustomTeleportTarget(null);
  }
}
