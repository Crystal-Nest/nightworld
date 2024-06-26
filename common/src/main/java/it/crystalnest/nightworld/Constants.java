package it.crystalnest.nightworld;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common shared constants across all loaders.
 */
@ApiStatus.Internal
public final class Constants {
  /**
   * {@link ThreadLocal} to keep track of a player's origin dimension when teleporting through a Nether/Nightworld portal.
   */
  public static final ThreadLocal<Boolean> NIGHTWORLD_ORIGIN_THREAD = ThreadLocal.withInitial(() -> false);

  /**
   * Mod id.
   */
  public static final String MOD_ID = "nightworld";

  /**
   * {@link ResourceKey} for the dimension.
   */
  public static final ResourceKey<Level> NIGHTWORLD = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(MOD_ID, "nightworld"));

  /**
   * {@link ResourceKey} for the dimension type.
   */
  public static final ResourceKey<DimensionType> NIGHTWORLD_TYPE = ResourceKey.create(Registry.DIMENSION_TYPE_REGISTRY, NIGHTWORLD.location());

  /**
   * Mod logger.
   */
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  private Constants() {}
}
