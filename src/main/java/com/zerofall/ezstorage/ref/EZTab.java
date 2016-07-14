package com.zerofall.ezstorage.ref;

import com.zerofall.ezstorage.init.EZBlocks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EZTab extends CreativeTabs {

	public EZTab() {
		super("EZStorage");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return Item.getItemFromBlock(EZBlocks.condensed_storage_box);
	}
}
