package com.zerofall.ezstorage.init;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.zerofall.ezstorage.block.BlockAccessTerminal;
import com.zerofall.ezstorage.block.BlockBlankBox;
import com.zerofall.ezstorage.block.BlockCondensedStorage;
import com.zerofall.ezstorage.block.BlockCraftingBox;
import com.zerofall.ezstorage.block.BlockExtractPort;
import com.zerofall.ezstorage.block.BlockHyperStorage;
import com.zerofall.ezstorage.block.BlockInputPort;
import com.zerofall.ezstorage.block.BlockOutputPort;
import com.zerofall.ezstorage.block.BlockSearchBox;
import com.zerofall.ezstorage.block.BlockSecurityBox;
import com.zerofall.ezstorage.block.BlockSortBox;
import com.zerofall.ezstorage.block.BlockStorage;
import com.zerofall.ezstorage.block.BlockStorageCore;
import com.zerofall.ezstorage.block.BlockSuperStorage;
import com.zerofall.ezstorage.block.BlockUltraStorage;
import com.zerofall.ezstorage.block.EZBlock;
import com.zerofall.ezstorage.config.EZConfig;
import com.zerofall.ezstorage.ref.RefStrings;
import com.zerofall.ezstorage.registry.IRegistryBlock;
import com.zerofall.ezstorage.tileentity.TileEntityExtractPort;
import com.zerofall.ezstorage.tileentity.TileEntityInputPort;
import com.zerofall.ezstorage.tileentity.TileEntityEjectPort;
import com.zerofall.ezstorage.tileentity.TileEntitySecurityBox;
import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;
import com.zerofall.ezstorage.util.JointList;

/** Mod blocks */
public class EZBlocks {
	
	private static JointList<IRegistryBlock> blocks;
	
	public static void mainRegistry() {
		blocks = new JointList();
		init();
		register();
	}
	
	public static EZBlock blank_box;
	public static EZBlock storage_core;
	public static EZBlock storage_box;
	public static EZBlock condensed_storage_box;
	public static EZBlock super_storage_box;
	public static EZBlock ultra_storage_box;
	public static EZBlock hyper_storage_box;
	public static EZBlock input_port;
	public static EZBlock output_port;
	public static EZBlock extract_port;
	public static EZBlock crafting_box;
	public static EZBlock search_box;
	public static EZBlock sort_box;
	public static EZBlock access_terminal;
	public static EZBlock security_box;
	
	private static void init() {
		blocks.join(
			blank_box = new BlockBlankBox(),
			storage_core = new BlockStorageCore(),
			storage_box = new BlockStorage(),
			condensed_storage_box = new BlockCondensedStorage(),
			super_storage_box = new BlockSuperStorage(),
			ultra_storage_box = new BlockUltraStorage(),
			hyper_storage_box = new BlockHyperStorage(),
			input_port = new BlockInputPort(),
			output_port = new BlockOutputPort(),
			extract_port = new BlockExtractPort(),
			crafting_box = new BlockCraftingBox(),
			search_box = new BlockSearchBox(),
			sort_box = new BlockSortBox(),
			access_terminal = new BlockAccessTerminal(),
			security_box = new BlockSecurityBox()
		);
		if(!EZConfig.enableTerminal) blocks.remove(access_terminal); // terminal disabled
		if(!EZConfig.enableSecurity) blocks.remove(security_box); // security disabled
	}
	
	/** Register the blocks and tile entities */
	private static void register() {
		for(IRegistryBlock block : blocks) {
			GameRegistry.registerBlock((Block)block, block.getItemClass(), block.getShorthandName(), block.getItemClassArgs());
		}
		GameRegistry.registerTileEntity(TileEntityStorageCore.class, RefStrings.MODID + ":TileEntityStorageCore");
		GameRegistry.registerTileEntity(TileEntityInputPort.class, RefStrings.MODID + ":TileEntityInputPort");
		GameRegistry.registerTileEntity(TileEntityEjectPort.class, RefStrings.MODID + ":TileEntityOutputPort");
		GameRegistry.registerTileEntity(TileEntityExtractPort.class, RefStrings.MODID + ":TileEntityExtractPort");
		GameRegistry.registerTileEntity(TileEntitySecurityBox.class, RefStrings.MODID + ":TileEntitySecurityBox");
	}
	
	/** Register model information */
	@SideOnly(Side.CLIENT)
	public static void registerRenders() {
		for(IRegistryBlock block : blocks) {
			block.registerRender(Minecraft.getMinecraft().getRenderItem().getItemModelMesher());
		}
	}
}
