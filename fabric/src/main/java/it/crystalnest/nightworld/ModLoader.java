package it.crystalnest.nightworld;

import net.fabricmc.api.ModInitializer;
import org.jetbrains.annotations.ApiStatus;

/**
 * Mod loader.
 */
@ApiStatus.Internal
public final class ModLoader implements ModInitializer {
  @Override
  public void onInitialize() {
    CommonModLoader.init();
  }
}
