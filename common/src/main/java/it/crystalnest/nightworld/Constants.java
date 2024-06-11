package it.crystalnest.nightworld;

import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Common shared constants across all loaders.
 */
@ApiStatus.Internal
public final class Constants {
	private Constants() {}

	/**
	 * Mod id.
	 */
	public static final String MOD_ID = "nightworld";

	/**
	 * Mod logger.
	 */
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
}
