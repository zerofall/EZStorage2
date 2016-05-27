package com.zerofall.ezstorage.init;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.zerofall.ezstorage.config.EZConfig;
import com.zerofall.ezstorage.item.EZItem;
import com.zerofall.ezstorage.item.ItemKey;
import com.zerofall.ezstorage.registry.IRegistryItem;
import com.zerofall.ezstorage.util.JointList;

/** Mod items */
public class EZItems {
	
	private static JointList<IRegistryItem> items;
	
	public static void mainRegistry() {
		items = new JointList();
		init();
		register();
	}
	
	public static EZItem key;
	
	private static void init() {
		items.join(
			key = new ItemKey()
		);
		if(!EZConfig.enableSecurity) items.remove(key); // security disabled
	}
	
	private static void register() {
		for(IRegistryItem item : items) {
			GameRegistry.registerItem((Item)item, item.getShorthandName());
		}
	}
	
	/** Register model information */
	@SideOnly(Side.CLIENT)
	public static void registerRenders() {
		for(IRegistryItem item : items) {
			item.registerRender(Minecraft.getMinecraft().getRenderItem().getItemModelMesher());
		}
	}

}
