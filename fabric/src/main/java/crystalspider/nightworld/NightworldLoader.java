package crystalspider.nightworld;

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
  public static final RegistryKey<World> NIGHTWORLD_DIMENSION = RegistryKey.of(RegistryKeys.WORLD, new Identifier(MODID, "nightworld"));
  /**
   * {@link RegistryKey} for the dimension type.
   */
  public static final RegistryKey<DimensionType> NIGHTWORLD_TYPE = RegistryKey.of(RegistryKeys.DIMENSION_TYPE, NIGHTWORLD_DIMENSION.getValue());

  @Override
	public void onInitialize() {}
}
