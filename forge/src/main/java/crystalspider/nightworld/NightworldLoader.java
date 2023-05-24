package crystalspider.nightworld;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Torch hit! mod loader.
 */
@Mod(NightworldLoader.MODID)
public class NightworldLoader {
  /**
   * ID of this mod.
   */
  public static final String MODID = "nightworld";

  /**
   * Network channel protocol version.
   */
  public static final String PROTOCOL_VERSION = "1.19-1.0";
  /**
   * {@link SimpleChannel} instance for compatibility client-server.
   */
  public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, (version) -> true);

  /**
   * {@link ResourceKey} for the dimension.
   */
  public static final ResourceKey<Level> NIGHTWORLD_DIMENSION = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(MODID, "nightworld"));
  /**
   * {@link ResourceKey} for the dimension type.
   */
  public static final ResourceKey<DimensionType> NIGHTWORLD_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, NIGHTWORLD_DIMENSION.location());

  public NightworldLoader() {}
}
