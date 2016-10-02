package com.zerofall.ezstorage.config;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.ref.Log;
import com.zerofall.ezstorage.ref.RefStrings;

import net.minecraftforge.common.config.Configuration;

/** The config settings */
public class EZConfig {

	public static int basicCapacity;
	public static int condensedCapacity;
	public static int superCapacity;
	public static int ultraCapacity;
	public static int hyperCapacity;
	public static boolean classicRecipes;
	public static boolean toughHyper;
	public static boolean enableTerminal;
	public static boolean enableSecurity;
	public static boolean enableSearchModes;
	public static boolean enableOpOverride;
	public static boolean jeiIntegration;

	public static void syncConfig() {
		final Configuration config = EZStorage.config;
		config.load();

		final String OPTIONS = Configuration.CATEGORY_GENERAL + Configuration.CATEGORY_SPLITTER + "options";

		basicCapacity = config.getInt("Basic Storage Capacity", OPTIONS, 400, 100, 4000, "Basic");
		condensedCapacity = config.getInt("Condensed Storage Capacity", OPTIONS, 4000, 100, 40000, "Condensed");
		superCapacity = config.getInt("Super Storage Capacity", OPTIONS, 20000, 100, 100000, "Super");
		ultraCapacity = config.getInt("Ultra Storage Capacity", OPTIONS, 80000, 100, 400000, "Ultra");
		hyperCapacity = config.getInt("Hyper Storage Capacity", OPTIONS, 400000, 100, 4000000, "Hyper");
		classicRecipes = config.getBoolean("Enable Classic Recipes", OPTIONS, false,
				"If true, classic recipes (not using blank boxes) will be used.");
		toughHyper = config.getBoolean("Harder Hyper Recipe", OPTIONS, true, "If true, the hyper storage box will use 2 nether stars instead of 1.");
		enableTerminal = config.getBoolean("Enable Access Terminal", OPTIONS, true, "Should the access terminal be enabled?");
		enableSecurity = config.getBoolean("Enable Security", OPTIONS, true, "Should the security features be enabled?");
		enableSearchModes = config.getBoolean("Enable Search Modes", OPTIONS, true,
				"Should '$' in front of a term search ore dictionary names, '@' search mod ids and names, and '%' search creative tab names?");
		enableOpOverride = config.getBoolean("Enable Op Override", OPTIONS, true,
				"Should a server op with permission level 2+ in creative mode be able to override the security of systems on their server?");
		jeiIntegration = config.getBoolean("JEI Integration", OPTIONS, true, "Integrate " + RefStrings.NAME + " with JEI?");

		if (config.hasChanged())
			config.save();
		Log.logger.info("Configuration loaded.");
	}
}
