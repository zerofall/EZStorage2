package com.zerofall.ezstorage.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.registry.IRegistryBlock;

/** Superclass for all mod blocks */
public abstract class EZBlock extends Block implements IRegistryBlock {

	protected EZBlock(String name, Material materialIn) {
		super(materialIn);
		this.setRegistryName(name);
		this.setCreativeTab(EZStorage.instance.creativeTab);
		this.setHardness(2.0f);
	}

}
