package com.zerofall.ezstorage.item;

import net.minecraft.item.Item;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.registry.IRegistryItem;

/** Superclass for all mod items */
public abstract class EZItem extends Item implements IRegistryItem {
	
	public EZItem(String name) {
		this.setUnlocalizedName(name);
		this.setCreativeTab(EZStorage.instance.creativeTab);
	}
	
}
