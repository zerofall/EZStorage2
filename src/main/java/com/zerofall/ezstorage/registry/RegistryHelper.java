package com.zerofall.ezstorage.registry;

import java.lang.reflect.Constructor;
import java.util.List;

import com.google.common.collect.ObjectArrays;
import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.crafting.CraftingManager;
import com.zerofall.ezstorage.init.EZBlocks;
import com.zerofall.ezstorage.init.EZItems;
import com.zerofall.ezstorage.ref.Log;
import com.zerofall.ezstorage.util.JointList;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Helps register items and blocks in the game */
public class RegistryHelper {
	
	public static final List<IRecipe> RECIPES_TO_REGISTER = new JointList();
	public static final List<Block> BLOCKS_TO_REGISTER = new JointList();
	public static final List<Item> ITEMS_TO_REGISTER = new JointList();
	
	/** Register a list of blocks at once */
	public static void registerBlocks(Iterable<IRegistryBlock> regBlocks) {
		for(IRegistryBlock r : regBlocks) registerBlock(r);
	}
	
	/** Register a list of items at once */
	public static void registerItems(Iterable<IRegistryItem> regItems) {
		for(IRegistryItem r : regItems) {
			r.setRarity();
			registerItem(r);
		}
	}
	 
	/** Register the block correctly */
	public static void registerBlock(IRegistryBlock regBlock) {
		Block block = (Block)regBlock;
		ItemBlock item;
		
		// register the block by itself first
		BLOCKS_TO_REGISTER.add(block.setUnlocalizedName(block.getRegistryName().toString()));
        
		// try to get the ItemBlock
        if(regBlock.getItemClass() != null) {
	        try {
				Class<?>[] ctorArgClasses = new Class[regBlock.getItemClassArgs().length + 1];
		        ctorArgClasses[0] = Block.class; // start with the block
		        for (int idx = 1; idx < ctorArgClasses.length; idx++) {
		            ctorArgClasses[idx] = regBlock.getItemClassArgs()[idx - 1].getClass();
		        }
		        Constructor<? extends ItemBlock> itemCtor = regBlock.getItemClass().getConstructor(ctorArgClasses);
		        item = itemCtor.newInstance(ObjectArrays.concat(regBlock, regBlock.getItemClassArgs()));
	        } catch (Exception e) {
	        	Log.logger.error("Unable to register block " + block.getRegistryName());
	        	return;
	        }
	        
	        // register the ItemBlock if there are no errors
	        ITEMS_TO_REGISTER.add(item.setRegistryName(block.getRegistryName()));
        }
	}
	
	/** Register the item correctly */
	public static void registerItem(IRegistryItem regItem) {
		Item item = (Item)regItem;
		ITEMS_TO_REGISTER.add(item.setUnlocalizedName(item.getRegistryName().toString()));
	}
	
	/** Register blocks and fluids */
	@SubscribeEvent
	public void onBlockRegistry(Register<Block> e) {
		EZBlocks.mainRegistry();
		
		for(Block b : BLOCKS_TO_REGISTER) {
			e.getRegistry().register(b);
		}
		BLOCKS_TO_REGISTER.clear();
		
		Log.logger.info("Blocks registered.");
	}
	
	/** Register items */
	@SubscribeEvent
	public void onItemRegistry(Register<Item> e) {
		EZItems.mainRegistry();
		
		for(Item i : ITEMS_TO_REGISTER) {
			e.getRegistry().register(i);
		}
		ITEMS_TO_REGISTER.clear();
		
		EZStorage.proxy.registerRenders();
		
		Log.logger.info("Items registered.");
	}
	
	/** Register recipes */
	@SubscribeEvent
	public void onRecipeRegistry(Register<IRecipe> e) {
		CraftingManager.mainRegistry();
		
		for(IRecipe r : RECIPES_TO_REGISTER) {
			e.getRegistry().register(r);
		}
		RECIPES_TO_REGISTER.clear();
		
		Log.logger.info("Recipes registered.");
	}

}
