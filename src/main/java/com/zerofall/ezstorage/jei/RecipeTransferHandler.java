package com.zerofall.ezstorage.jei;

import java.util.List;
import java.util.Map;

import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.gui.ingredients.GuiIngredient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.zerofall.ezstorage.EZStorage;
import com.zerofall.ezstorage.gui.server.ContainerStorageCoreCrafting;
import com.zerofall.ezstorage.network.MessageRecipeSync;

/** The mod's recipe transfer handler */
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
	public IRecipeTransferError transferRecipe(Container container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer,
			boolean doTransfer) {
		if (doTransfer) {
			Map<Integer, GuiIngredient<ItemStack>> inputs = (Map<Integer, GuiIngredient<ItemStack>>) recipeLayout.getItemStacks().getGuiIngredients();
			NBTTagCompound recipe = new NBTTagCompound();
			JEIStackHelper helper = new JEIStackHelper();
			for (Slot slot : container.inventorySlots) {
				if (slot.inventory instanceof InventoryCrafting) {
					GuiIngredient<ItemStack> ingredient = inputs.get(slot.getSlotIndex() + 1);
					if (ingredient != null) {
						List<ItemStack> possibleItems = ingredient.getAllIngredients();
						NBTTagList tags = new NBTTagList();
						String ore = null;
						if ((ore = helper.getOreDictEquivalent(possibleItems)) != null) {
							NBTTagCompound tag = new NBTTagCompound();
							tag.setString("ore", ore);
							tags.appendTag(tag);
						} else {
							for (ItemStack is : possibleItems) {
								NBTTagCompound tag = new NBTTagCompound();
								is.writeToNBT(tag);
								tags.appendTag(tag);
							}
						}
						recipe.setTag("#" + slot.getSlotIndex(), tags);
					}
				}
			}

			// update the recipe serverside
			EZStorage.nw.sendToServer(new MessageRecipeSync(recipe));
		}
		return null;
	}
}
