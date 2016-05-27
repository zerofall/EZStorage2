package com.zerofall.ezstorage.crafting;

import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

/** Helps with crafting recipe management */
public class RecipeHelper {
	
	/** Add a shaped ore recipe */
	public static void addShapedOreRecipe(ItemStack result, Object... recipe) {
		GameRegistry.addRecipe(new ShapedOreRecipe(result, recipe));
	}
	
	/** Add a shapeless ore recipe */
	public static void addShapelessOreRecipe(ItemStack result, Object... recipe) {
		GameRegistry.addRecipe(new ShapelessOreRecipe(result, recipe));
	}
	
	/** Remove all recipes that give 'stackResult' */
	public static void removeRecipes(ItemStack stackResult) {
		List<IRecipe> allRecipes = CraftingManager.getInstance().getRecipeList();
		Iterator<IRecipe> remover = allRecipes.iterator();
		while(remover.hasNext()) {
			IRecipe current = remover.next();
			if(ItemStack.areItemStacksEqual(stackResult, current.getRecipeOutput())) {
				remover.remove(); // get rid of it
			}
		}
	}

}
