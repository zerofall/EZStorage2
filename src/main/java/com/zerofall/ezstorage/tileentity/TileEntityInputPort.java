package com.zerofall.ezstorage.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

/** The input port */
public class TileEntityInputPort extends TileEntityItemHandler {

	private ItemStack[] inv = new ItemStack[1];
	
	@Override
	public void update() {
		super.update();

		if(this.hasCore()) {
			ItemStack stack = this.inv[0];
			if(stack != null && stack.stackSize > 0) {
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
		if (stack != null) {
			if (stack.stackSize <= count) {
				setInventorySlotContents(index, null);
			} else {
				stack = stack.splitStack(count);
				if (stack.stackSize == 0) {
					setInventorySlotContents(index, null);
				}
			}
		}
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		inv[index] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public void clear() {
		for (int i = 0; i < inv.length; ++i) {
			inv[i] = null;
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
		return !worldObj.isBlockPowered(pos);
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
		return null;
	}

	@Override
	public NBTTagCompound writeDataToNBT(NBTTagCompound paramNBTTagCompound) {
		return paramNBTTagCompound;
	}

	@Override
	public void readDataFromNBT(NBTTagCompound paramNBTTagCompound) {}
}
