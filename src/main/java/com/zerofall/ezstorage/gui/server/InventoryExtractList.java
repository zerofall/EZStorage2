package com.zerofall.ezstorage.gui.server;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class InventoryExtractList extends InventoryBasic {

	public InventoryExtractList(String title, int slotCount) {
		super(title, false, slotCount);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	@Override
	public ItemStack addItem(ItemStack stack) {
		return super.addItem(stack.copy());
		//return stack;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		return super.decrStackSize(index, count);
		//return ItemStack.EMPTY; // nothing returned
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return super.removeStackFromSlot(index);
		//return ItemStack.EMPTY; // nothing returned
	}

	/** Checks whether or not this inventory is empty */
	@Override
	public boolean isEmpty() {
		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (!this.getStackInSlot(i).isEmpty())
				return false;
		}
		return true;
	}

}
