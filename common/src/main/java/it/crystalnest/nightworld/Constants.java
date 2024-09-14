package it.crystalnest.nightworld;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common shared constants across all loaders.
 */
@ApiStatus.Internal
public final class Constants {
  /**
   * Mod ID.
   */
  public static final String MOD_ID = "nightworld";

  /**
   * {@link ResourceKey} for the dimension.
   */
  public static final ResourceKey<Level> NIGHTWORLD = ResourceKey.create(Registries.DIMENSION, new ResourceLocation("server_sided_portals", "nightworld"));

  /**
   * Mod logger.
   */
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  private Constants() {}
}
