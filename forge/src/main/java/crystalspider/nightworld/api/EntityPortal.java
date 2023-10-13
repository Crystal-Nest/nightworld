package crystalspider.nightworld.api;

import java.util.Optional;

import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.Vec3;

public interface EntityPortal {
  Optional<FoundRectangle> exitPortal(ServerLevel destination, BlockPos pos, boolean destIsNether, WorldBorder worldBorder);
  Vec3 relativePortalPosition(Axis axis, FoundRectangle rectangle);
  BlockPos portalEntrancePos();
}
