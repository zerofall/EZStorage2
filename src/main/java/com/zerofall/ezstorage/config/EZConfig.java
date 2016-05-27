package com.zerofall.ezstorage.config;

import com.zerofall.ezstorage.EZStorage;

import net.minecraftforge.common.config.Configuration;

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

	public static void syncConfig() {
		final Configuration config = EZStorage.config;
		config.load();

		final String OPTIONS = config.CATEGORY_GENERAL + config.CATEGORY_SPLITTER + "options";

		basicCapacity = config.getInt("Basic Storage Capacity", OPTIONS, 400, 100, 4000, "Basic");
		condensedCapacity = config.getInt("Condensed Storage Capacity", OPTIONS, 4000, 100, 40000, "Condensed");
		superCapacity = config.getInt("Super Storage Capacity", OPTIONS, 20000, 100, 100000, "Super");
		ultraCapacity = config.getInt("Ultra Storage Capacity", OPTIONS, 80000, 100, 400000, "Ultra");
		hyperCapacity = config.getInt("Hyper Storage Capacity", OPTIONS, 400000, 100, 4000000, "Hyper");
		classicRecipes = config.getBoolean("Enable Classic Recipes", OPTIONS, false, "If true, classic recipes (not using blank boxes) will be used.");
		toughHyper = config.getBoolean("Harder Hyper Recipe", OPTIONS, true, "If true, the hyper storage box will use 2 nether stars instead of 1.");
		enableTerminal = config.getBoolean("Enable Access Terminal", OPTIONS, true, "Should the access terminal be enabled?");
		enableSecurity = config.getBoolean("Enable Security", OPTIONS, true, "Should the security features be enabled?");

		if(config.hasChanged()) config.save();
	}
}
