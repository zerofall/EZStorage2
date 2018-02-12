package com.zerofall.ezstorage.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/** The input port */
public class TileEntityInputPort extends TileEntityItemHandler {

	private ItemStack[] inv = new ItemStack[]{ItemStack.EMPTY};
	
	@Override
	public void update() {
		super.update();

		if(this.hasCore()) {
			ItemStack stack = this.inv[0];
			if (!stack.isEmpty() && stack.getCount() > 0) {
				this.inv[0] = this.core.input(stack);
			}
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString("input_port");
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inv[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		ItemStack stack = getStackInSlot(index);
		if (!stack.isEmpty()) {
			if (stack.getCount() <= count) {
				setInventorySlotContents(index, ItemStack.EMPTY);
			} else {
				stack = stack.splitStack(count);
				if (stack.getCount() == 0) {
					setInventorySlotContents(index, ItemStack.EMPTY);
				}
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv[index] = stack;
		if (!stack.isEmpty() && stack.getCount() > getInventoryStackLimit()) {
			stack.setCount(getInventoryStackLimit());
		}
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public void clear() {
		for (int i = 0; i < inv.length; ++i) {
			inv[i] = ItemStack.EMPTY;
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		int[] slots = new int[1];
		slots[0] = 0;
		return slots;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return !world.isBlockPowered(pos);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}

	@Override
	public String getName() {
		return "input_port";
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ItemStack.EMPTY;
	}

	@Override
	public NBTTagCompound writeDataToNBT(NBTTagCompound paramNBTTagCompound) {
		return paramNBTTagCompound;
	}

	@Override
	public void readDataFromNBT(NBTTagCompound paramNBTTagCompound) {}
}
