package com.zerofall.ezstorage.registry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.zerofall.ezstorage.ref.RefStrings;

/** Interface to help with block registration */
public interface IRegistryBlock extends IRegistryBase {

	/** Gets the block's item class to use when registering */
	public default Class<? extends ItemBlock> getItemClass() {
		return ItemBlock.class;
	}

	/** Gets additional arguments to pass through to the ItemBlock constructor */
	public default Object[] getItemClassArgs() {
		return new Object[0];
	}

	@Override
	public default String getShorthandName() {
		return ((Block) this).getUnlocalizedName().substring(5);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public default void registerRender(ItemModelMesher mesher) {
		mesher.register(Item.getItemFromBlock((Block) this), 0,
				new ModelResourceLocation(RefStrings.MODID + ":" + this.getShorthandName(), "inventory"));
	}

}
