package com.vanhal.recallstones;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeRechargeStone implements IRecipe {
	private ItemStack inputItem;

	public RecipeRechargeStone(ItemStack itemStack) {
		inputItem = itemStack;
	}

	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack stone = null;
		int numberPearls = 0;
		int invSize = inv.getSizeInventory();
		for (int i = 0; i < invSize; i++)  {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack != null) {
				if (itemstack.getItem() instanceof ItemBase) {
					stone = itemstack;
				} else if (itemstack.getItem().getClass() == Items.ender_pearl.getClass()) {
					numberPearls++;
				}
			}
        }
		ItemStack output = stone.copy();
		ItemBase outputStone = (ItemBase)output.getItem();
		if (outputStone.canAddCharge(output, numberPearls)) {
			return outputStone.addCharge(output, numberPearls);
		} else {
			return (ItemStack)null;
		}
	}
	
	public ItemStack getRecipeOutput() {
		ItemStack output = inputItem.copy();
		return output;
	}

	@Override
	public int getRecipeSize() {
		return 1;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		int invSize = inv.getSizeInventory();
		boolean haveStone = false;
		int numberPearls = 0;
		int otherItems = 0;
		for (int i = 0; i < invSize; i++)  {
			ItemStack temp = inv.getStackInSlot(i);
			if (temp!=null) {
				if (temp.getItem() instanceof ItemBase) {
					if (!haveStone) haveStone = true;
					else otherItems++;
				} else if (temp.getItem().getClass() == Items.ender_pearl.getClass()) {
					numberPearls++;
				} else {
					otherItems++;
				}
			}
		}
		if ( (haveStone) && (numberPearls>0) && (otherItems == 0) ) {
			return true;
		}
		return false;
	}

}
