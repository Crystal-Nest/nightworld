package crystalspider.nightworld;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.util.registry.Registry;

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
  public static final RegistryKey<World> NIGHTWORLD = RegistryKey.of(Registry.WORLD_KEY, new Identifier(MODID, "nightworld"));
  /**
   * {@link RegistryKey} for the dimension type.
   */
  public static final RegistryKey<DimensionType> NIGHTWORLD_TYPE = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, NIGHTWORLD.getValue());

  /**
   * {@link ThreadLocal} to keep track of a player's origin dimension when teleporting through a Nether/Nightworld portal.
   */
  @Internal()
  public static final ThreadLocal<Boolean> nightworldOriginThread = ThreadLocal.withInitial(() -> false);

  @Override
	public void onInitialize() {}
}
