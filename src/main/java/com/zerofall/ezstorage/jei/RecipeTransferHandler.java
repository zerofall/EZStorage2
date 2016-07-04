package com.zerofall.ezstorage.jei;

import java.util.List;
import java.util.Map;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.ingredients.IGuiIngredient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.gui.server.ContainerStorageCoreCrafting;
import com.zerofall.ezstorage.network.RecipeMessage;
import com.zerofall.ezstorage.tileentity.TileEntityStorageCore;

public class RecipeTransferHandler implements IRecipeTransferHandler {

	@Override
	public Class<? extends Container> getContainerClass() {
		return ContainerStorageCoreCrafting.class;
	}

	@Override
	public String getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
		if (doTransfer) {
			Map<Integer, ? extends IGuiIngredient<ItemStack>> inputs = recipeLayout.getItemStacks().getGuiIngredients();
			NBTTagCompound recipe = new NBTTagCompound();
			for (Slot slot : (List<Slot>) container.inventorySlots) {
				if (slot.inventory instanceof InventoryCrafting) {
					IGuiIngredient<ItemStack> ingredient = inputs.get(slot.getSlotIndex()+1);
					if (ingredient != null) {
						List<ItemStack> possibleItems = ingredient.getAllIngredients();
						NBTTagList tags = new NBTTagList();
						for (ItemStack is : possibleItems) {
							NBTTagCompound tag = new NBTTagCompound();
							is.writeToNBT(tag);
							tags.appendTag(tag);
						}
						recipe.setTag("#" + slot.getSlotIndex(), tags);
					}
				}
			}

			// update the recipe on the server and on the client
			RecipeMessage msg = new RecipeMessage(recipe);
			EZStorage.networkWrapper.sendToServer(msg);
			recipeUpdateClient(msg, player);
		}
		return null;
	}

	ItemStack[][] recipe;

	/** Clientside recipe update */
	public void recipeUpdateClient(RecipeMessage message, EntityPlayer player) {
		Container container = player.openContainer;
		if (container instanceof ContainerStorageCoreCrafting) {
			ContainerStorageCoreCrafting con = (ContainerStorageCoreCrafting)container;
			TileEntityStorageCore tileEntity = con.tileEntity;

			// Empty grid into inventory
			con.clearGrid(player);

			this.recipe = new ItemStack[9][];
			for( int x = 0; x < this.recipe.length; x++ ) {
				NBTTagList list = message.recipe.getTagList( "#" + x, 10 );
				if( list.tagCount() > 0 ) {
					this.recipe[x] = new ItemStack[list.tagCount()];
					for( int y = 0; y < list.tagCount(); y++ ) {
						this.recipe[x][y] = ItemStack.loadItemStackFromNBT( list.getCompoundTagAt( y ) );
					}
				}
			}
			for (int i = 0; i < this.recipe.length; i++) {
				if (this.recipe[i] != null && this.recipe[i].length > 0) {
					Slot slot = con.getSlotFromInventory(con.craftMatrix, i);
					if (slot != null) {
						ItemStack retreived = tileEntity.inventory.getItemsNoDecrease(this.recipe[i]);
						if (retreived != null) {
							slot.putStack(retreived);
						}
					}
				}
			}
		}
	}
}
