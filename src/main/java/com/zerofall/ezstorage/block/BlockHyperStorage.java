package com.zerofall.ezstorage.block;

import com.zerofall.ezstorage.configuration.EZConfiguration;

import net.minecraft.block.material.Material;

public class BlockHyperStorage extends BlockStorage {

	public BlockHyperStorage() {
		super("hyper_storage_box", Material.IRON);
	}
	
	@Override
	public int getCapacity() {
		return EZConfiguration.hyperCapacity;
	}
}