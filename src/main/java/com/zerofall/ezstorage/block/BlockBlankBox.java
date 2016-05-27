package com.zerofall.ezstorage.block;

import net.minecraft.block.material.Material;

import com.zerofall.ezstorage.registry.IRegistryBlock;

/** A blank multiblock for simple extension */
public class BlockBlankBox extends StorageMultiblock {

	public BlockBlankBox() {
		super("blank_box", Material.WOOD);
	}

}
