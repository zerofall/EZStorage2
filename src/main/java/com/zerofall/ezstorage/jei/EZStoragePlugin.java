package com.zerofall.ezstorage.jei;

import com.zerofall.ezstorage.config.EZConfig;

import mezz.jei.api.IItemListOverlay;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;

/** Support for JEI things */
@JEIPlugin
public class EZStoragePlugin implements IModPlugin {
	
	public static IItemListOverlay jeiOverlay;

	@Override
	public void register(IModRegistry registry) {
		if(EZConfig.jeiIntegration) {
			RecipeTransferHandler helper = new RecipeTransferHandler();
			registry.getRecipeTransferRegistry().addRecipeTransferHandler(helper);
			JEIUtils.jeiLoaded = true;
		}
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		jeiOverlay = jeiRuntime.getItemListOverlay();
	}

}
