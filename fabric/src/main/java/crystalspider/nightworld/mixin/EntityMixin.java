package crystalspider.nightworld.mixin;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import crystalspider.nightworld.NightworldLoader;
import crystalspider.nightworld.api.NightworldPortalChecker;
import net.fabricmc.fabric.impl.dimension.Teleportable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.BlockLocating.Rectangle;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.AreaHelper;

/**
 * Injects into {@link Entity} to alter dimension travel.
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
  /**
   * Shadowed {@link Entity#world}.
   */
  @Shadow
  public World world;

  /**
   * Shadowed {@link Entity#lastNetherPortalPosition}.
   */
  @Shadow
  protected BlockPos lastNetherPortalPosition;

  /**
   * Shadowed {@link Entity#getBlockPos()}.
   * 
   * @return entity block position.
   */
  @Shadow
  public abstract BlockPos getBlockPos();

  /**
   * Shadowed {@link Entity#getPos()}.
   * 
   * @return exact entity position.
   */
  @Shadow
  public abstract Vec3d getPos();

  /**
   * Shadowed {@link Entity#getVelocity()}.
   * 
   * @return entity velocity.
   */
  @Shadow
  public abstract Vec3d getVelocity();

  /**
   * Shadowed {@link Entity#getYaw()}.
   * 
   * @return entity yaw.
   */
  @Shadow
  public abstract float getYaw();

  /**
   * Shadowed {@link Entity#getPitch()}.
   * 
   * @return entity pitch.
   */
  @Shadow
  public abstract float getPitch();

  /**
   * Shadowed {@link Entity#isRemoved()}.
   * 
   * @return whether the entity is removed
   */
  @Shadow
  public abstract boolean isRemoved();

  /**
   * Shadowed {@link Entity#moveToWorld(ServerWorld)}.
   * 
   * @param destination
   * @return
   */
  @Shadow
  public abstract Entity moveToWorld(ServerWorld destination);

  /**
   * Shadowed {@link Entity#getPortalRect(ServerWorld, BlockPos, boolean, WorldBorder)}.
   * 
   * @param destWorld
   * @param destPos
   * @param destIsNether
   * @param worldBorder
   * @return
   */
  @Shadow
  protected abstract Optional<Rectangle> getPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder);

  /**
   * Shadowed {@link Entity#positionInPortal(Axis, Rectangle)}.
   * 
   * @param portalAxis
   * @param portalRect
   * @return
   */
  @Shadow
  protected abstract Vec3d positionInPortal(Axis portalAxis, Rectangle portalRect);

  /**
   * Redirects the call to {@link Entity#moveToWorld(ServerWorld)} inside the method {@link Entity#tickPortal()}.
   * <p>
   * Changes the {@link TeleportTarget} if the entity is in a Nightworld Portal.
   * 
   * @param caller
   * @param destination
   * @return
   */
  @Redirect(method = "tickPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;moveToWorld(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/entity/Entity;"))
  private Entity redirectMoveToWorld(Entity caller, ServerWorld destination) {
    ServerWorld actualDestination = destination;
    if (
      !world.isClient &&
      !this.isRemoved() &&
      (world.getRegistryKey() == World.OVERWORLD || world.getRegistryKey() == NightworldLoader.NIGHTWORLD) &&
      destination.getRegistryKey() == World.NETHER &&
      NightworldPortalChecker.isNightworldPortal(world, lastNetherPortalPosition)
    ) {
      actualDestination = ((ServerWorld) world).getServer().getWorld(world.getRegistryKey() == World.OVERWORLD ? NightworldLoader.NIGHTWORLD : World.OVERWORLD);
      ((Teleportable) this).fabric_setCustomTeleportTarget(this.getNightworldTeleportTarget(caller, actualDestination));
    }
    return this.moveToWorld(actualDestination);
  }
  
  /**
   * Injects at the start of the method {@link Entity#getPortalRect(ServerWorld, BlockPos, boolean, WorldBorder)}.
   * <p>
   * Sets the nightworld origin dimension flag for this entity.
   * 
   * @param destWorld
   * @param destPos
   * @param destIsNether
   * @param worldBorder
   * @param cir
   */
  @Inject(method = "getPortalRect", at = @At(value = "HEAD"))
  private void onGetPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder, CallbackInfoReturnable<Optional<BlockLocating.Rectangle>> cir) {
    NightworldLoader.nightworldOriginThread.set(world.getRegistryKey() == NightworldLoader.NIGHTWORLD);
  }

  /**
   * Injects into the method {@link Entity#moveToWorld(ServerWorld)} after the call to {@link Entity#getTeleportTarget(ServerWorld)}.
   * <p>
   * Resets the {@link net.fabricmc.fabric.mixin.dimension.EntityMixin#customTeleportTarget customTeleportTarget}.
   * 
   * @param destination
   * @param cir
   */
  @Inject(method = "moveToWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getTeleportTarget(Lnet/minecraft/server/world/ServerWorld;)Lnet/minecraft/world/TeleportTarget;", shift = Shift.AFTER))
  private void onMoveToWorld(ServerWorld destination, CallbackInfoReturnable<Entity> cir) {
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
  private TeleportTarget getNightworldTeleportTarget(Entity caller, ServerWorld destination) {
    return this.getPortalRect(destination, getBlockPos(), false, destination.getWorldBorder()).map(rect -> {
      Vec3d vec3d;
      Axis axis;
      BlockState blockState = world.getBlockState(this.lastNetherPortalPosition);
      if (blockState.contains(Properties.HORIZONTAL_AXIS)) {
        axis = blockState.get(Properties.HORIZONTAL_AXIS);
        Rectangle rectangle = BlockLocating.getLargestRectangle(this.lastNetherPortalPosition, axis, AreaHelper.MAX_WIDTH, Axis.Y, AreaHelper.field_31824, pos -> world.getBlockState(pos) == blockState);
        vec3d = this.positionInPortal(axis, rectangle);
      } else {
        axis = Axis.X;
        vec3d = new Vec3d(0.5, 0.0, 0.0);
      }
      return AreaHelper.getNetherTeleportTarget(destination, rect, axis, vec3d, caller.getDimensions(caller.getPose()), getVelocity(), getYaw(), getPitch());
    }).orElse(null);
  }
}
