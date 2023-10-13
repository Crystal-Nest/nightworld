package crystalspider.nightworld.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import crystalspider.nightworld.api.EntityPortal;
import crystalspider.nightworld.api.MinecraftEntity;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;

// FIXME: class net.minecraft.server.level.ServerPlayer cannot be cast to class crystalspider.nightworld.api.EntityPortal (net.minecraft.server.level.ServerPlayer is in module minecraft@1.19.4 of loader 'TRANSFORMER' @507d64aa; crystalspider.nightworld.api.EntityPortal is in module nightworld@1.0.0.0 of loader 'TRANSFORMER' @507d64aa)

@Mixin(Entity.class)
public abstract class EntityMixin implements EntityPortal, MinecraftEntity {
  /**
   * Shadowed {@link Entity#portalEntrancePos}.
   */
  @Shadow
  public BlockPos portalEntrancePos;

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
}
