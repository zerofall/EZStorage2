package com.zerofall.ezstorage.jei;

import com.zerofall.ezstorage.config.EZConfig;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IItemListOverlay;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

/** Support for JEI things */
@JEIPlugin
public class EZStoragePlugin extends BlankModPlugin {

	static IItemListOverlay jeiOverlay;
	static IJeiHelpers jeiHelpers;

	@Override
	public void register(IModRegistry registry) {
		if (EZConfig.jeiIntegration) {
			jeiHelpers = registry.getJeiHelpers();
			RecipeTransferHandler helper = new RecipeTransferHandler();
			registry.getRecipeTransferRegistry().addRecipeTransferHandler(helper, VanillaRecipeCategoryUid.CRAFTING);
			JEIUtils.jeiLoaded = true;
		}
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
		jeiOverlay = jeiRuntime.getItemListOverlay();
	}

}
