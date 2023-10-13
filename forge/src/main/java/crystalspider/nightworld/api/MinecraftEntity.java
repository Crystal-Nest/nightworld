package crystalspider.nightworld.api;

import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface MinecraftEntity {
  Level getLevel();
  Vec3 getRelativePortalPosition(Axis axis, FoundRectangle rectangle);
}
