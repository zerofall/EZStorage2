package com.zerofall.ezstorage.registry;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Interface to help with item registration */
public interface IRegistryItem extends IRegistryBase {

	/** Called to set the item's rarity defaults */
	public default void setRarity() {}

	@Override
	public default String getShorthandName() {
		return ((Item)this).getUnlocalizedName().substring(5);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public default void registerRender() {
		ModelLoader.setCustomModelResourceLocation((Item)this, 0, new ModelResourceLocation(((Item)this).getRegistryName(), "inventory"));
	}

}
