package com.zerofall.ezstorage.block;

import com.zerofall.ezstorage.config.EZConfig;

import net.minecraft.block.material.Material;

public class BlockCondensedStorage extends BlockStorage {

	public BlockCondensedStorage() {
		super("condensed_storage_box", Material.IRON);
	}
	
	@Override
	public int getCapacity() {
		return EZConfig.condensedCapacity;
	}
}
