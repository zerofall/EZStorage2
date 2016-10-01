package com.zerofall.ezstorage.gui.server;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class InventoryExtractList extends InventoryBasic {

	public InventoryExtractList(String title, int slotCount) {
		super(title, false, slotCount);
	}

	@Override
	public int getInventoryStackLimit() {
		return 0;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		super.decrStackSize(index, count);
		return null; // nothing returned
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		super.removeStackFromSlot(index);
		return null; // nothing returned
	}

	/** Checks whether or not this inventory is empty */
	public boolean isEmpty() {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (this.getStackInSlot(i) != null)
				return false;
		}
		return true;
	}

}
