package it.crystalnest.nightworld.mixin;

import java.awt.*;
import java.util.Optional;

import it.crystalnest.nightworld.CommonModLoader;
import it.crystalnest.nightworld.api.NightworldPortalChecker;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.level.portal.PortalShape;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import net.fabricmc.fabric.impl.dimension.Teleportable;


/**
 * Injects into {@link Entity} to alter dimension travel.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
  /**
   * Shadowed {@link Entity#world}.
   */
  @Shadow
  public abstract Level level();

  /**
   * Shadowed {@link Entity#portalEntrancePos}.
   */

  @Shadow
  protected BlockPos portalEntrancePos;

  /**
   * Shadowed {@link Entity#blockPosition()}.
   * 
   * @return entity block position.
   */
  @Shadow
  public abstract BlockPos blockPosition();

  /**
   * Shadowed {@link Entity#position()}.
   * 
   * @return exact entity position.
   */
  @Shadow
  public abstract Vec3 position();

  /**
   * Shadowed {@link Entity#getDeltaMovement()}.
   * 
   * @return entity velocity.
   */
  @Shadow
  public abstract Vec3 getDeltaMovement();

  /**
   * Shadowed {@link Entity#getYRot()}.
   * 
   * @return entity yaw.
   */
  @Shadow
  public abstract float getYRot();

  /**
   * Shadowed {@link Entity#getXRot()}.
   * 
   * @return entity pitch.
   */
  @Shadow
  public abstract float getXRot();

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
   * @param destination
   * @return
   */
  @Shadow
  public abstract Entity changeDimension(ServerLevel destination);

  /**
   * Shadowed {@link Entity#getExitPortal(ServerLevel, BlockPos, boolean, WorldBorder)}.
   * 
   * @param destWorld
   * @param destPos
   * @param destIsNether
   * @param worldBorder
   * @return
   */
  @Shadow
  protected abstract Optional <BlockUtil.FoundRectangle> getExitPortal(ServerLevel destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder);

  /**
   * Shadowed {@link Entity#getRelativePortalPosition(Direction.Axis, Rectangle)}.
   * 
   * @param portalAxis
   * @param portalRect
   * @return
   */
  @Shadow
  protected abstract Vec3 getRelativePortalPosition(Direction.Axis portalAxis, BlockUtil.FoundRectangle portalRect);

  /**
   * Redirects the call to {@link Entity#moveToWorld(ServerWorld)} inside the method {@link Entity#tickPortal()}.
   * <p>
   * Changes the {@link TeleportTarget} if the entity is in a Nightworld Portal.
   * 
   * @param caller
   * @param destination
   * @return
   */
  @Redirect(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;changeDimension(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/entity/Entity;"))
  private Entity redirectMoveToWorld(Entity caller, ServerLevel destination) {
    ServerLevel actualDestination = destination;
    if (
      !level().isClientSide &&
      !this.isRemoved() &&
      (level().dimension() == Level.OVERWORLD || level().dimension() == CommonModLoader.NIGHTWORLD) &&
      destination.dimension() == Level.NETHER &&
      NightworldPortalChecker.isNightworldPortal(level(), portalEntrancePos)
    ) {
      actualDestination = ((ServerLevel) level()).getServer().getLevel(level().dimension() == Level.OVERWORLD ? CommonModLoader.NIGHTWORLD : Level.OVERWORLD);
      ((Teleportable) this).fabric_setCustomTeleportTarget(this.getNightworldTeleportTarget(caller, actualDestination));
    }
    return this.changeDimension(actualDestination);
  }
  


  /**
   * Injects into the method {@link Entity#onMoveToWorld(ServerLevel)} after the call to {@link Entity#getTeleportTarget(ServerWorld)}.
   * <p>
   * Resets the {@link net.fabricmc.fabric.mixin.dimension.EntityMixin#customTeleportTarget customTeleportTarget}.
   * 
   * @param destination
   * @param cir
   */
  @Inject(method = "changeDimension", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;findDimensionEntryPoint(Lnet/minecraft/server/level/ServerLevel;)Lnet/minecraft/world/level/portal/PortalInfo;", shift = Shift.AFTER))
  private void onMoveToWorld(ServerLevel destination, CallbackInfoReturnable<Entity> cir) {
    ((Teleportable) this).fabric_setCustomTeleportTarget(null);
  }

  /**
   * Partial copy-paste of {@link Entity#getTeleportTarget(ServerWorld)}, changed to return the proper {@link TeleportTarget} for teleporting into the Nightworld.
   * 
   * @param caller
   * @param destination
   * @return
   */
  @Nullable
  private PortalInfo getNightworldTeleportTarget(Entity caller, ServerLevel destination) {
    return this.getExitPortal(destination, blockPosition(), false, destination.getWorldBorder()).map(rect -> {
      Vec3 vec3;
      Direction.Axis axis;
      BlockState blockState = level().getBlockState(this.portalEntrancePos);
      if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_AXIS)) {
        axis = blockState.getValue(BlockStateProperties.HORIZONTAL_AXIS);
        BlockUtil.FoundRectangle rectangle = BlockUtil.getLargestRectangleAround(this.portalEntrancePos, axis, PortalShape.MAX_WIDTH, Direction.Axis.Y, PortalShape.MAX_HEIGHT, pos -> level().getBlockState(pos) == blockState);
        vec3 = this.getRelativePortalPosition(axis, rectangle);
      } else {
        axis = Direction.Axis.X;
        vec3 = new Vec3(0.5, 0.0, 0.0);
      }
      return PortalShape.createPortalInfo(destination, rect, axis, vec3, caller, getDeltaMovement(), getYRot(), getXRot());
    }).orElse(null);
  }
}
