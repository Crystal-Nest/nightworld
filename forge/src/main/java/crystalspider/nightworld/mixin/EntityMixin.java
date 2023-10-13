package crystalspider.nightworld.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import crystalspider.nightworld.NightworldLoader;
import crystalspider.nightworld.api.EntityPortal;
import crystalspider.nightworld.api.MinecraftEntity;
import crystalspider.nightworld.api.NightworldPortalChecker;
import crystalspider.nightworld.api.Teleportable;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;

/**
 * Injects into {@link Entity} to alter dimension travel.
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements Teleportable, EntityPortal, MinecraftEntity {
  /**
   * Shadowed {@link Entity#portalEntrancePos}.
   */
  @Shadow
  public BlockPos portalEntrancePos;

  /**
   * Shadowed {@link Entity#getExitPortal(ServerLevel, BlockPos, boolean, WorldBorder)}.
   * 
   * @param destination
   * @param pos
   * @param destIsNether
   * @param worldBorder
   * @return
   */
  @Shadow
  protected abstract Optional<FoundRectangle> getExitPortal(ServerLevel destination, BlockPos pos, boolean destIsNether, WorldBorder worldBorder);

  @Override
  public Optional<FoundRectangle> exitPortal(ServerLevel destination, BlockPos pos, boolean destIsNether, WorldBorder worldBorder) {
    return getExitPortal(destination, pos, destIsNether, worldBorder);
  }

  @Override
  public Vec3 relativePortalPosition(Axis axis, FoundRectangle rectangle) {
    return getRelativePortalPosition(axis, rectangle);
  }

  @Override
  public BlockPos portalEntrancePos() {
    return portalEntrancePos;
  }

  /**
   * Redirects the call to {@link Entity#level()} inside the method {@link Entity#handleNetherPortal()}.
   * <p>
   * Optionally changes the destination dimension.
   * 
   * @param caller
   * @param worldKey
   * @return
   */
  @Redirect(method = "handleNetherPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;getLevel(Lnet/minecraft/resources/ResourceKey;)Lnet/minecraft/server/level/ServerLevel;"))
  private ServerLevel redirectGetLevel(MinecraftServer caller, ResourceKey<Level> worldKey) {
    Level origin = level();
    ServerLevel destination = caller.getLevel(worldKey);
    if (
      destination != null &&
      (origin.dimension() == Level.OVERWORLD || origin.dimension() == NightworldLoader.NIGHTWORLD) &&
      destination.dimension() == Level.NETHER &&
      NightworldPortalChecker.isNightworldPortal(origin, portalEntrancePos())
    ) {
      destination = caller.getLevel(origin.dimension() == Level.OVERWORLD ? NightworldLoader.NIGHTWORLD : Level.OVERWORLD);
    }
    return destination;
  }
}
