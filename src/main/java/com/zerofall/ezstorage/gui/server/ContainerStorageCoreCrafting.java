package com.zerofall.ezstorage.gui.server;

import javax.annotation.Nonnull;

import com.zerofall.ezstorage.events.CoreEvents;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

/** The crafting-expansion storage core container */
public class ContainerStorageCoreCrafting extends ContainerStorageCore {

	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public IInventory craftResult = new InventoryCraftResult();
	private World worldObj;
	private long lastTick = -1;

	public ContainerStorageCoreCrafting(EntityPlayer player, World world, int x, int y, int z) {
		super(player, world, x, y, z);
		this.worldObj = world;
		this.addSlotToContainer(new SlotCrafting(player, this.craftMatrix, this.craftResult, 0, 116, 117));
		int i;
		int j;

		for (i = 0; i < 3; ++i) {
			for (j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 44 + j * 18, 99 + i * 18));
			}
		}
		this.onCraftMatrixChanged(this.craftMatrix);
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		this.craftResult.setInventorySlotContents(0, CraftingManager.findMatchingResult(this.craftMatrix, this.worldObj));
	}

	// Shift clicking
	@Override
	public @Nonnull ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {

		// make sure there's no multiclicking shenanigans going on
		if (playerIn instanceof EntityPlayerMP) {
			if (CoreEvents.serverTicks == lastTick) {
				EntityPlayerMP mp = (EntityPlayerMP) playerIn;
				mp.sendContainerToPlayer(this); // send an inventory sync
												// message just in case
				return ItemStack.EMPTY;
			}
			lastTick = CoreEvents.serverTicks; // keep track of server ticks
		} else {
			if (CoreEvents.clientTicks == lastTick)
				return ItemStack.EMPTY;
			lastTick = CoreEvents.clientTicks; // keep track of client ticks
		}

		// now do shift-click processing
		Slot slotObject = inventorySlots.get(index);
		if (slotObject != null && slotObject.getHasStack()) {
			if (slotObject instanceof SlotCrafting) {
				ItemStack[] recipe = new ItemStack[9];
				for (int i = 0; i < 9; i++) {
					recipe[i] = this.craftMatrix.getStackInSlot(i);
				}

				ItemStack itemstack1 = slotObject.getStack();
				ItemStack itemstack = ItemStack.EMPTY;
				ItemStack original = itemstack1.copy();
				int crafted = 0;
				int maxStackSize = itemstack1.getMaxStackSize();
				int crafting = itemstack1.getCount();
				for (int i = 0; i < itemstack1.getMaxStackSize(); i++) {

					if (slotObject.getHasStack() && slotObject.getStack().isItemEqual(itemstack1)) {
						if (crafting > maxStackSize) {
							return ItemStack.EMPTY;
						}
						itemstack1 = slotObject.getStack();
						itemstack = itemstack1.copy();
						if (crafted + itemstack1.getCount() > itemstack1.getMaxStackSize()) {
							return ItemStack.EMPTY;
						}
						boolean merged = this.mergeItemStack(itemstack1, this.rowCount() * 9, this.rowCount() * 9 + 36, true);
						if (!merged) {
							return ItemStack.EMPTY;
						} else {

							// It merged! grab another
							crafted += itemstack.getCount();
							slotObject.onSlotChange(itemstack1, itemstack);
							slotObject.onTake(playerIn, itemstack1);

							if (original.isItemEqual(slotObject.getStack())) {
								continue;
							}

							tryToPopulateCraftingGrid(recipe, playerIn);
						}
					} else {
						break;
					}
				}

				if (itemstack1.getCount() == itemstack.getCount()) {
					return ItemStack.EMPTY;
				}

				return itemstack;
			} else {
				ItemStack stackInSlot = slotObject.getStack();
				slotObject.putStack(this.tileEntity.inventory.input(stackInSlot));
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public @Nonnull ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		if (slotId >= 0 && slotId < inventorySlots.size()) {
			Slot slotObject = inventorySlots.get(slotId);
			if (slotObject != null && slotObject instanceof SlotCrafting) { // user clicked on result slot
				ItemStack[] recipe = new ItemStack[9];
				for (int i = 0; i < 9; i++) {
					recipe[i] = this.craftMatrix.getStackInSlot(i).copy();
				}
				
				ItemStack result = super.slotClick(slotId, dragType, clickTypeIn, player);
				if (!result.isEmpty()) {
					tryToPopulateCraftingGrid(recipe, player);
				}
				return result;
			}
		}
		return super.slotClick(slotId, dragType, clickTypeIn, player);
	}

	private void tryToPopulateCraftingGrid(ItemStack[] recipe, EntityPlayer playerIn) {
		clearCraftingGrid(playerIn);
		for (int j = 0; j < recipe.length; j++) {
			if (!recipe[j].isEmpty()) {

			    // if the item count is higher than 1, we take 1 item away.
			    if(recipe[j].getCount() != 1) {
                    recipe[j].setCount(recipe[j].getCount() - 1);
                }

				Slot slot = getSlotFromInventory(this.craftMatrix, j);
				if (slot != null) {
					ItemStack retrieved = tileEntity.inventory.getItems(new ItemStack[] { recipe[j] });
					if (!retrieved.isEmpty()) {
						slot.putStack(retrieved);
					}
				}
			}
		}
	}

	@Override
	protected int playerInventoryY() {
		return 162;
	}

	@Override
	protected int rowCount() {
		return 4;
	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn) {
		clearCraftingGrid(playerIn);
		super.onContainerClosed(playerIn);
	}

	public void clearCraftingGrid(EntityPlayer playerIn) {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = this.craftMatrix.getStackInSlot(i);
			if (!stack.isEmpty()) {
				ItemStack result = this.tileEntity.input(stack);
				this.craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);

				// drop items on the ground if the grid is unable to be cleared
				if (!result.isEmpty()) {
					playerIn.dropItem(result, false);
				}
			}
		}
	}
}
