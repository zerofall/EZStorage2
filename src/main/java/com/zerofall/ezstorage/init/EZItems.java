package com.zerofall.ezstorage.init;

import com.zerofall.ezstorage.config.EZConfig;
import com.zerofall.ezstorage.item.EZItem;
import com.zerofall.ezstorage.item.ItemDolly;
import com.zerofall.ezstorage.item.ItemKey;
import com.zerofall.ezstorage.registry.IRegistryItem;
import com.zerofall.ezstorage.registry.RegistryHelper;
import com.zerofall.ezstorage.util.JointList;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Mod items */
public class EZItems {

	private static JointList<IRegistryItem> items;

	public static void mainRegistry() {
		items = new JointList();
		init();
		register();
	}

	public static EZItem key;
	public static EZItem dolly_basic;
	public static EZItem dolly_super;

	private static void init() {
		items.join(
			key = new ItemKey(),
			dolly_basic = new ItemDolly(false),
			dolly_super = new ItemDolly(true)
		);
		if(!EZConfig.enableSecurity) items.remove(key); // security disabled
		if(!EZConfig.enableDolly) {
			items.remove(dolly_basic); // dollies disabled
			items.remove(dolly_super);
		}
	}

	private static void register() {
		RegistryHelper.registerItems(items);
	}

	/** Register model information */
	@SideOnly(Side.CLIENT)
	public static void registerRenders() {
		for (IRegistryItem item : items) {
			item.registerRender();
		}
	}

}
