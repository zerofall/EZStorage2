package com.zerofall.ezstorage.gui.server;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.zerofall.ezstorage.network.EZNetwork;
import com.zerofall.ezstorage.tileentity.TileEntitySecurityBox;

/** The secure box container */
public class ContainerSecurityBox extends Container {
	
	private TileEntitySecurityBox tileSecure;
	
	public ContainerSecurityBox(InventoryPlayer player, TileEntitySecurityBox tile) {
		this.tileSecure = tile;
		
		int yoff_inv = 64;
		
		// player inventory placement
		int i;
		
		for(i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + yoff_inv));
			}
		}
		
		for(i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142 + yoff_inv));
		}
		
	}
	
	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);		
		// send a sync message to the client
		EZNetwork.sendSecureSyncMsg(tileSecure.getWorld(), tileSecure.getPos(), tileSecure.getAllowedPlayers());
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
	
	/** Call for removal, then do a resync */
	@Override
	public boolean enchantItem(EntityPlayer player, int action) {
		boolean flag = tileSecure.getAllowedPlayers().remove(action) != null;
		tileSecure.markDirty();
		EZNetwork.sendSecureSyncMsg(tileSecure.getWorld(), tileSecure.getPos(), tileSecure.getAllowedPlayers());
		return flag;
	}
	
	/**
	* Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	*/
	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
	{
		ItemStack itemstack = null;
		Slot slot = this.inventorySlots.get(par2);

		int INPUT = -1;

		if (slot != null && slot.getHasStack())
		{
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			// itemstack is in player inventory, try to place in slot
			if (par2 != INPUT)
			{
				// item in player's inventory, but not in action bar
				if (par2 >= INPUT+1 && par2 < INPUT+28)
				{
					// place in action bar
					if (!this.mergeItemStack(itemstack1, INPUT+28, INPUT+37, false))
					{
						return null;
					}
				}
				// item in action bar - place in player inventory
				else if (par2 >= INPUT+28 && par2 < INPUT+37 && !this.mergeItemStack(itemstack1, INPUT+1, INPUT+28, false))
				{
					return null;
				}
			}

			if (itemstack1.stackSize == 0)
			{
				slot.putStack((ItemStack)null);
			}
			else
			{
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize)
			{
				return null;
			}

			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}
		return itemstack;
	}
}
