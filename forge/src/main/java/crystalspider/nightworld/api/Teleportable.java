package crystalspider.nightworld.api;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.portal.PortalInfo;

/**
 * Any Entity that can travel between dimensions.
 */
public interface Teleportable {
  /**
   * Current custom portal info.
   * 
   * @return
   */
  @Nullable PortalInfo getCustomPortalInfo();
  /**
   * Returns a copy of the current custom portal info and sets to {@code null} the reference instance.
   * 
   * @return
   */
  @Nullable PortalInfo consumeCustomPortalInfo();
  /**
   * Sets the current custom portal info.
   * 
   * @param portalInfo
   */
  void setCustomPortalInfo(@Nullable PortalInfo portalInfo);
}
