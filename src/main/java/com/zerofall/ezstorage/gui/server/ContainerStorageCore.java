package com.zerofall.ezstorage.gui.server;

import javax.annotation.Nonnull;

import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** The storage core container */
public class ContainerStorageCore extends Container {

	public TileEntityStorageCore tileEntity;

	public ContainerStorageCore(EntityPlayer player, World world, int x, int y, int z) {
		this.tileEntity = ((TileEntityStorageCore) world.getTileEntity(new BlockPos(x, y, z)));
		int startingY = 18;
		int startingX = 8;

		// the EZStorage slots
		IInventory inventory = new InventoryBasic("title", false, this.rowCount() * 9);
		for (int i = 0; i < this.rowCount(); i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9, startingX + j * 18, startingY + i * 18));
			}
		}

		// the player inventory
		bindPlayerInventory(player.inventory);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, (j + i * 9) + 9, playerInventoryX() + j * 18, playerInventoryY() + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, playerInventoryX() + i * 18, playerInventoryY() + 58));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	@Override
	public boolean enchantItem(EntityPlayer player, int action) {
		switch (action) {
		case 0: // change sort mode and update
			tileEntity.sortMode = tileEntity.sortMode.rotateMode();
			tileEntity.sortInventory();
			return true;
		case 1: // clear the crafting grid if it exists
			if (this instanceof ContainerStorageCoreCrafting) {
				((ContainerStorageCoreCrafting) this).clearCraftingGrid(player);
				tileEntity.sortInventory();
				return true;
			}
		}
		return false;
	}

	/** Shift click a slot */
	@Override
	public @Nonnull ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		Slot slotObject = inventorySlots.get(index);
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			slotObject.putStack(this.tileEntity.inventory.input(stackInSlot));
		}
		return ItemStack.EMPTY;
	}

	/** Default slot click handling. Also checks for shift-clicking to sort the inventory appropriately */
	@Override
	public @Nonnull ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		ItemStack val = ItemStack.EMPTY;
		if (slotId < this.rowCount() * 9 && slotId >= 0) {
			val = ItemStack.EMPTY; // use custom handler for clicks on the inventory
		} else {
			val = super.slotClick(slotId, dragType, clickTypeIn, player);
			if (clickTypeIn == ClickType.QUICK_MOVE)
				this.tileEntity.sortInventory(); // sort only on insert shift-click
		}
		return val;
	}

	/** Click a custom slot to take or insert items */
	public @Nonnull ItemStack customSlotClick(int slotId, int clickedButton, int shiftPressed, EntityPlayer playerIn) {
		ItemStack heldStack = playerIn.inventory.getItemStack();

		if (heldStack.isEmpty()) {
            ItemStack retrievedStack = this.tileEntity.inventory.getItemsAt(slotId, clickedButton);

            // take item from system if the user inventory is not full
            if (playerIn.inventory.getFirstEmptyStack() != -1) {
                if (retrievedStack.isEmpty()) return ItemStack.EMPTY;

                // check for shift clicking
                if (clickedButton == 0 && shiftPressed == 1) {
                    if (!this.mergeItemStack(retrievedStack, this.rowCount() * 9, this.rowCount() * 9 + 36, true)) {
                        this.tileEntity.inventory.input(retrievedStack);
                    }
                } else playerIn.inventory.setItemStack(retrievedStack);
                return retrievedStack;

            } else playerIn.inventory.setItemStack(retrievedStack);
        } else playerIn.inventory.setItemStack(this.tileEntity.inventory.input(heldStack));

		return ItemStack.EMPTY;
	}

	protected int playerInventoryX() {
		return 8;
	}

	protected int playerInventoryY() {
		return 140;
	}

	protected int rowCount() {
		return 6;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.tileEntity.sortInventory();
	}
}
