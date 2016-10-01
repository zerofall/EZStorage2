package com.zerofall.ezstorage.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import com.zerofall.ezstorage.gui.server.InventoryExtractList;
import com.zerofall.ezstorage.tileentity.TileEntityExtractPort.EnumListMode;
import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;

/** The EZStorage inventory system */
public class EZInventory {

	public TileEntityStorageCore tile;
	public List<ItemGroup> inventory;
	public long maxItems = 0;

	public EZInventory(TileEntityStorageCore tile) {
		this.inventory = new ArrayList<ItemGroup>();
		this.tile = tile;
	}

	/** Input a stack to the system (always sort, e.g. from player action) */
	public ItemStack input(ItemStack itemStack) {
		return input(itemStack, true);
	}

	/** Input a stack to the system with sorting option */
	public ItemStack input(ItemStack itemStack, boolean sort) {
		// Inventory is full
		if (getTotalCount() >= maxItems) {
			return itemStack;
		}
		long space = maxItems - getTotalCount();

		// Only part of the stack can fit
		int amount = (int) Math.min(space, itemStack.stackSize);
		ItemStack result = mergeStack(itemStack, amount);

		// sort or no sort?
		if (sort) {
			tile.sortInventory();
		} else {
			tile.updateInventory();
		}

		return result;
	}

	/** Sort the inventory (block updates are separate) */
	public void sort() {
		tile.sortMode.sortInventory(this.inventory);
	}

	/** Attempt a stack merge */
	private ItemStack mergeStack(ItemStack itemStack, int amount) {
		for (ItemGroup group : inventory) {
			if (stacksEqual(group.itemStack, itemStack)) {
				group.count += amount;
				itemStack.stackSize -= amount;
				if (itemStack.stackSize <= 0) {
					return null;
				} else {
					return itemStack;
				}
			}
		}

		// need to add a space
		inventory.add(new ItemGroup(itemStack, amount));
		itemStack.stackSize -= amount;

		if (itemStack.stackSize <= 0) {
			return null;
		} else {
			return itemStack;
		}
	}

	/** Extract items from the inventory */
	// Type: 0= full stack, 1= half stack, 2= single
	public ItemStack getItemsAt(int index, int type) {
		return getItemsAt(index, type, -1);
	}

	/** Extract items from the inventory with precision */
	public ItemStack getItemsAt(int index, int type, int size) {
		return getItemsAt(index, type, size, false);
	}

	/** Extract items from the inventory with precision and peek support */
	public ItemStack getItemsAt(int index, int type, int size, boolean peek) {
		if (index >= inventory.size()) {
			return null;
		}
		ItemGroup group = inventory.get(index);
		ItemStack stack = group.itemStack.copy();
		if (size > 1) {
			if (type == 1) {
				size = size / 2;
			} else if (type == 2) {
				size = 1;
			}
		}

		return extractStack(group, size, peek);
	}

	/** Extract items on whitelist / blacklist match */
	public ItemStack getItemsExtractList(EnumListMode mode, InventoryExtractList list, int size, boolean peek) {

		// inventory is empty, treat it like IGNORE mode
		if (list.isEmpty())
			return getItemsAt(0, 0, size, peek);

		// not empty
		for (int i = 0; i < list.getSizeInventory(); i++) {
			ItemStack comp = list.getStackInSlot(i);
			if (comp == null)
				continue; // ignore empty slots

			for (ItemGroup g : this.inventory) {
				if (EZInventory.stacksEqualOreDict(comp, g.itemStack)) {
					if (mode == EnumListMode.BLACKLIST) {
						continue;
					} else {
						return extractStack(g, size, peek);
					}
				} else {
					if (mode == EnumListMode.WHITELIST) {
						continue;
					} else {
						return extractStack(g, size, peek);
					}
				}

			}
		}
		return null;
	}

	/** Extract items on whitelist / blacklist match */
	public ItemStack getItemsExtractList(EnumListMode mode, InventoryExtractList list, int size) {
		return getItemsExtractList(mode, list, size, false);
	}

	/** Peeks items on whitelist / blacklist match */
	public ItemStack peekItemsExtractList(EnumListMode mode, InventoryExtractList list) {
		return getItemsExtractList(mode, list, -1, true);
	}

	/** Extracts an itemstack from an item group */
	private ItemStack extractStack(ItemGroup group, int stackSize, boolean peek) {
		ItemStack stack = group.itemStack.copy();
		if (stackSize < 0) {
			stackSize = (int) Math.min(stack.getMaxStackSize(), group.count);
		} else {
			stackSize = Math.min(stack.getMaxStackSize(), (int) Math.min(stackSize, group.count)); // no
																									// more
																									// than
																									// 64
																									// at
																									// a
																									// time,
																									// and
																									// no
																									// more
																									// than
																									// the
																									// system
																									// has
		}
		stack.stackSize = stackSize;

		if (!peek) { // remove items when not peeking
			group.count -= stackSize;
			if (group.count <= 0) {
				inventory.remove(group);
				tile.sortInventory();
			} else {
				tile.updateInventory();
			}
		}

		return stack;
	}

	/** Get items and decrease their stack size in the inventory */
	public ItemStack getItems(ItemStack[] itemStacks) {
		for (ItemGroup group : inventory) {
			for (ItemStack itemStack : itemStacks) {
				if (stacksEqualOreDict(group.itemStack, itemStack)) {
					if (group.count >= itemStack.stackSize) {
						ItemStack stack = group.itemStack.copy();
						stack.stackSize = itemStack.stackSize;
						group.count -= itemStack.stackSize;
						if (group.count <= 0) {
							inventory.remove(group);
						}
						return stack;
					}
					return null;
				}
			}
		}
		return null;
	}

	/** Get the size of the inventory */
	public int slotCount() {
		return inventory.size();
	}

	/** Check stacks for equality to join them in the inventory */
	public static boolean stacksEqual(ItemStack stack1, ItemStack stack2) {
		if (stack1 == null && stack2 == null) {
			return true;
		}
		if (stack1 == null || stack2 == null) {
			return false;
		}
		if (stack1.getItem() == stack2.getItem()) {
			if (stack1.getItemDamage() == stack2.getItemDamage()) {
				if ((!stack1.hasTagCompound() && !stack2.hasTagCompound()) || (stack1.hasTagCompound() && stack1.getTagCompound().equals(stack2.getTagCompound()))) {
					return true;
				}
			}
		}
		return false;
	}

	/** Check stacks for oredict equality (wildcard damage support) */
	public static boolean stacksEqualOreDict(ItemStack stack1, ItemStack stack2) {
		boolean first = stacksEqual(stack1, stack2);
		boolean nullCheck = stack1 != null && stack2 != null;
		boolean second = stack1.getItem() == stack2.getItem();
		boolean third = stack1.getItemDamage() == OreDictionary.WILDCARD_VALUE || stack2.getItemDamage() == OreDictionary.WILDCARD_VALUE;
		return first || (nullCheck && second && third);
	}

	/** Get the total item count */
	public long getTotalCount() {
		long count = 0;
		for (ItemGroup group : inventory) {
			count += group.count;
		}
		return count;
	}

	/** Gets the index of an itemgroup in the inventory */
	public int indexOf(ItemGroup group) {
		int n = 0;
		for (ItemGroup g : inventory) {
			if (stacksEqual(g.itemStack, group.itemStack)) {
				return n;
			}
			n++;
		}
		return -1;
	}

	/** Convert this inventory to a string */
	@Override
	public String toString() {
		return inventory.toString();
	}
}
