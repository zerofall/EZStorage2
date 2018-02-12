package com.zerofall.ezstorage.crafting;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.zerofall.ezstorage.config.EZConfig;
import com.zerofall.ezstorage.init.EZBlocks;
import com.zerofall.ezstorage.init.EZItems;

/** Mod recipes */
public class CraftingManager {

	public static void mainRegistry() {
		addCraftingRecipes();
	}

	/** Add the crafting recipes */
	private static void addCraftingRecipes() {
		// primary recipes

		// blank box
		RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.blank_box, 2), "ABA", "BCB", "ABA", 'A', "logWood", 'B', "plankWood", 'C',
				"stickWood");

		// old recipes enabled
		if (EZConfig.classicRecipes) {

			// storage core
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.storage_core), "ABA", "BCB", "ABA", 'A', "logWood", 'B', "ingotIron", 'C',
					Blocks.CHEST);

			// basic storage box
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.storage_box), "ABA", "B B", "ABA", 'A', "logWood", 'B', Blocks.CHEST);

			// input port
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.input_port), "ABA", "BCB", "ABA", 'A', Blocks.HOPPER, 'B', Blocks.PISTON, 'C',
					"blockQuartz");

			// crafting box
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.crafting_box), "ABA", "BCB", "ABA", 'A', Items.ENDER_EYE, 'B',
					Blocks.CRAFTING_TABLE, 'C', "gemDiamond");

			// search box
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.search_box), "ABA", "BCB", "ABA", 'A', "blockIron", 'B', Items.ENCHANTED_BOOK, 'C',
					Items.COMPASS);

			// sorting box
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.sort_box), "ABA", "BCB", "ABA", 'A', "logWood", 'B', Items.COMPARATOR, 'C',
					Items.MAP);

			// security box
			if (EZConfig.enableSecurity)
				RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.security_box), "ABA", "BCB", "ABA", 'A', "blockIron", 'B', Blocks.IRON_BARS,
						'C', EZItems.key);

		} else { // blank box recipes

			// storage core
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.storage_core), "C", "B", "I", 'B', new ItemStack(EZBlocks.blank_box), 'C',
					Blocks.CHEST, 'I', "ingotIron");

			// basic storage box
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.storage_box), " C ", "CBC", " C ", 'B', new ItemStack(EZBlocks.blank_box), 'C',
					Blocks.CHEST);

			// input port
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.input_port), "HQH", "PBP", "PHP", 'B', new ItemStack(EZBlocks.blank_box), 'H',
					Blocks.HOPPER, 'P', Blocks.PISTON, 'Q', "blockQuartz");

			// crafting box
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.crafting_box), "EDE", "CBC", "CEC", 'B', new ItemStack(EZBlocks.blank_box), 'E',
					Items.ENDER_EYE, 'C', Blocks.CRAFTING_TABLE, 'D', "gemDiamond");

			// search box
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.search_box), "EDE", "CBC", "CEC", 'B', new ItemStack(EZBlocks.blank_box), 'E',
					Items.ENCHANTED_BOOK, 'C', "blockIron", 'D', Items.COMPASS);

			// sorting box
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.sort_box), "EDE", "CBC", "CEC", 'B', new ItemStack(EZBlocks.blank_box), 'E',
					Items.COMPARATOR, 'C', "logWood", 'D', Items.MAP);

			// security box
			if (EZConfig.enableSecurity)
				RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.security_box), "EDE", "CBC", "CEC", 'B', new ItemStack(EZBlocks.blank_box),
						'E', "blockIron", 'C', Blocks.IRON_BARS, 'D', EZItems.key);

		}

		// secondary recipes

		// condensed storage box
		RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.condensed_storage_box), "ACA", "CBC", "ACA", 'A', "blockIron", 'B',
				EZBlocks.storage_box, 'C', Blocks.IRON_BARS);

		// super storage box
		RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.super_storage_box), "ACA", "CBC", "ACA", 'A', "blockGold", 'B',
				EZBlocks.condensed_storage_box, 'C', "nuggetGold");

		// ultra storage box
		RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.ultra_storage_box), "ACA", "CBC", "ACA", 'A', "blockDiamond", 'B',
				EZBlocks.super_storage_box, 'C', "gemDiamond");

		// hyper storage box
		RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.hyper_storage_box), "ABA", "ACA", EZConfig.toughHyper ? "ABA" : "AAA", 'A',
				Blocks.OBSIDIAN, 'B', Items.NETHER_STAR, 'C', EZBlocks.ultra_storage_box);

		// ejection port
		RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.output_port), " D ", "ABA", " A ", 'A', Blocks.PISTON, 'D', Blocks.DISPENSER, 'B',
				EZBlocks.input_port);

		// extraction port
		RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.extract_port), "ICI", "ABA", "ICI", 'A', Blocks.HOPPER, 'C', Items.COMPARATOR, 'I',
				Blocks.IRON_BARS, 'B', EZBlocks.input_port);

		// terminal
		if (EZConfig.enableTerminal)
			RecipeHelper.addShapedRecipe(new ItemStack(EZBlocks.access_terminal), "IXI", "XAX", "IXI", 'X', "paneGlass", 'I', Blocks.IRON_BARS,
					'A', EZBlocks.storage_core);

		// key
		if (EZConfig.enableSecurity)
			RecipeHelper.addShapedRecipe(new ItemStack(EZItems.key), "XXI", "XX ", 'I', "ingotGold", 'X', "nuggetGold");
		
		// dollies
		if (EZConfig.enableDolly) {
			RecipeHelper.addShapedRecipe(new ItemStack(EZItems.dolly_basic), "X X","X X","PPP", 'X',"ingotIron", 'P',Blocks.STONE_PRESSURE_PLATE);
			RecipeHelper.addShapedRecipe(new ItemStack(EZItems.dolly_super), " X "," D "," X ", 'X',"ingotGold", 'D',EZItems.dolly_basic);
		}

	}

}
