package com.zerofall.ezstorage.init;

import com.zerofall.ezstorage.Reference;
import com.zerofall.ezstorage.block.BlockCondensedStorage;
import com.zerofall.ezstorage.block.BlockCraftingBox;
import com.zerofall.ezstorage.block.BlockHyperStorage;
import com.zerofall.ezstorage.block.BlockInputPort;
import com.zerofall.ezstorage.block.BlockOutputPort;
import com.zerofall.ezstorage.block.BlockSearchBox;
import com.zerofall.ezstorage.block.BlockStorage;
import com.zerofall.ezstorage.block.BlockStorageCore;
import com.zerofall.ezstorage.tileentity.TileEntityInputPort;
import com.zerofall.ezstorage.tileentity.TileEntityOutputPort;
import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class EZBlocks {
	
	public static Block storage_core;
	public static Block storage_box;
	public static Block condensed_storage_box;
	public static Block hyper_storage_box;
	public static Block input_port;
	public static Block output_port;
	public static Block crafting_box;
	public static Block search_box;
	
	public static void init()
	{
		storage_core = new BlockStorageCore();
		storage_box = new BlockStorage();
		condensed_storage_box = new BlockCondensedStorage();
		hyper_storage_box = new BlockHyperStorage();
		input_port = new BlockInputPort();
		output_port = new BlockOutputPort();
		crafting_box = new BlockCraftingBox();
		search_box = new BlockSearchBox();
	}
	
	public static void register()
	{
		GameRegistry.registerBlock(storage_core, storage_core.getUnlocalizedName().substring(5));
		GameRegistry.registerTileEntity(TileEntityStorageCore.class, "TileEntityStorageCore");
		GameRegistry.registerBlock(storage_box, storage_box.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(condensed_storage_box, condensed_storage_box.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(hyper_storage_box, hyper_storage_box.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(input_port, input_port.getUnlocalizedName().substring(5));
		GameRegistry.registerTileEntity(TileEntityInputPort.class, "TileEntityInputPort");
		GameRegistry.registerBlock(output_port, output_port.getUnlocalizedName().substring(5));
		GameRegistry.registerTileEntity(TileEntityOutputPort.class, "TileEntityOutputPort");
		GameRegistry.registerBlock(crafting_box, crafting_box.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(search_box, search_box.getUnlocalizedName().substring(5));
		registerRecipes();
	}	
	
	public static void registerRenders()
	{
		registerRender(storage_core);
		registerRender(storage_box);
		registerRender(condensed_storage_box);
		registerRender(hyper_storage_box);
		registerRender(input_port);
		registerRender(output_port);
		registerRender(crafting_box);
		registerRender(search_box);
	}
	
	public static void registerRender(Block block)
	{
		Item item = Item.getItemFromBlock(block);
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(Reference.MOD_ID + ":" + item.getUnlocalizedName().substring(5), "inventory"));
	}
	
	public static void registerRecipes() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(storage_core),
		    	"ABA",
		    	"BCB",
		    	"ABA",
		    	'A', "logWood", 'B', Items.IRON_INGOT, 'C', Blocks.CHEST));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(storage_box),
		    	"ABA",
		    	"B B",
		    	"ABA",
		    	'A', "logWood", 'B', Blocks.CHEST));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(condensed_storage_box),
		    	"ACA",
		    	"CBC",
		    	"ACA",
		    	'A', "blockIron", 'B', storage_box, 'C', Blocks.IRON_BARS));
		GameRegistry.addRecipe(new ItemStack(hyper_storage_box),
		    	"ABA",
		    	"ACA",
		    	"AAA",
		    	'A', Blocks.OBSIDIAN, 'B', Items.NETHER_STAR, 'C', condensed_storage_box);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(input_port),
		    	"ABA",
		    	"BCB",
		    	"ABA",
		    	'A', Blocks.HOPPER, 'B', Blocks.PISTON, 'C', "blockQuartz"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(output_port),
		    	"A",
		    	"B",
		    	"A",
		    	'A', Blocks.PISTON, 'B', input_port));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(crafting_box),
		    	"ABA",
		    	"BCB",
		    	"ABA",
		    	'A', Items.ENDER_EYE, 'B', Blocks.CRAFTING_TABLE, 'C', "gemDiamond"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(search_box),
		    	"ABA",
		    	"BCB",
		    	"ABA",
		    	'A', "blockIron", 'B', Items.ENCHANTED_BOOK, 'C', Items.COMPASS));
	}
}
