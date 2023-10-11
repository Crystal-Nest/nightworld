package crystalspider.nightworld;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

/**
 * Nightworld mod loader.
 */
public class NightworldLoader implements ModInitializer {
  /**
   * ID of this mod.
   */
  public static final String MODID = "nightworld";

  /**
   * {@link RegistryKey} for the dimension.
   */
  public static final RegistryKey<World> NIGHTWORLD = RegistryKey.of(RegistryKeys.WORLD, new Identifier(MODID, "nightworld"));
  /**
   * {@link RegistryKey} for the dimension type.
   */
  public static final RegistryKey<DimensionType> NIGHTWORLD_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, NIGHTWORLD.getValue());

  /**
   * {@link ThreadLocal} to keep track of a player's origin dimension when teleporting through a Nether/Nightworld portal.
   */
  @Internal()
  public static final ThreadLocal<Boolean> nightworldOriginThread = ThreadLocal.withInitial(() -> false);

  @Override
	public void onInitialize() {}
}
