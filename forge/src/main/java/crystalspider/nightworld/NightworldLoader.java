package crystalspider.nightworld;

import org.jetbrains.annotations.ApiStatus.Internal;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;

/**
 * Torch hit! mod loader.
 */
@Mod(NightworldLoader.MOD_ID)
public class NightworldLoader {
  /**
   * ID of this mod.
   */
  public static final String MOD_ID = "nightworld";

  /**
   * Network channel protocol version.
   */
  public static final int PROTOCOL_VERSION = 1_20_4__1_0;
  /**
   * {@link SimpleChannel} instance for compatibility client-server.
   */
  public static final SimpleChannel INSTANCE = ChannelBuilder.named(new ResourceLocation(MOD_ID, "main")).networkProtocolVersion(PROTOCOL_VERSION).optionalClient().simpleChannel();

  /**
   * {@link ResourceKey} for the dimension.
   */
  public static final ResourceKey<Level> NIGHTWORLD = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(MOD_ID, "nightworld"));
  /**
   * {@link ResourceKey} for the dimension type.
   */
  public static final ResourceKey<DimensionType> NIGHTWORLD_TYPE = ResourceKey.create(Registries.DIMENSION_TYPE, NIGHTWORLD.location());

  /**
   * {@link ThreadLocal} to keep track of a player's origin dimension when teleporting through a Nether/Nightworld portal.
   */
  @Internal()
  public static final ThreadLocal<Boolean> nightworldOriginThread = ThreadLocal.withInitial(() -> false);

  public NightworldLoader() {}
}
