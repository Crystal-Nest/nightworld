package it.crystalnest.nightworld.mixin;

import it.crystalnest.nightworld.api.EntityPortal;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Optional;

/**
 * Injects into {@link Entity} to implement {@link EntityPortal}.
 */
@Mixin(Entity.class)
public abstract class EntityMixin implements EntityPortal {
  /**
   * Shadowed {@link Entity#portalEntrancePos}.
   */
  @Shadow
  protected BlockPos portalEntrancePos;

  /**
   * Shadowed {@link Entity#getRelativePortalPosition(Direction.Axis, BlockUtil.FoundRectangle)}.
   *
   * @param axis portal alignment (X or Z).
   * @param rectangle portal frame.
   * @return relative portal position.
   */
  @Shadow
  protected abstract Vec3 getRelativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle rectangle);

  /**
   * Shadowed {@link Entity#getExitPortal(ServerLevel, BlockPos, boolean, WorldBorder)}.
   *
   * @param destination dimension.
   * @param pos position.
   * @param destIsNether whether the destination is the Nether.
   * @param worldBorder world border.
   * @return optional portal frame.
   */
  @Shadow
  protected abstract Optional<BlockUtil.FoundRectangle> getExitPortal(ServerLevel destination, BlockPos pos, boolean destIsNether, WorldBorder worldBorder);

  @Override
  public Optional<BlockUtil.FoundRectangle> exitPortal(ServerLevel destination, BlockPos pos, boolean destIsNether, WorldBorder worldBorder) {
    return getExitPortal(destination, pos, destIsNether, worldBorder);
  }

  @Override
  public Vec3 relativePortalPosition(Direction.Axis axis, BlockUtil.FoundRectangle rectangle) {
    return getRelativePortalPosition(axis, rectangle);
  }

  @Override
  public BlockPos portalEntrancePos() {
    return portalEntrancePos;
  }
}
