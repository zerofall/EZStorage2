package com.zerofall.ezstorage.gui.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotExtractList extends Slot {

	public SlotExtractList(InventoryExtractList inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
	}

	// make sure the stack doesn't show zero items (clientside only)
//	@Override
//	public ItemStack getStack() {
//		if(!serverside) {
//			ItemStack s = null;
//			try {
//				s = super.getStack().copy();
//				s.stackSize = 1;
//			} catch (NullPointerException e) {}
//			return s;
//		} else {
//			return super.getStack();
//		}
//	}

}
