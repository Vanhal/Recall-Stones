package com.vanhal.recallstones;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;

//recipe for upgrading a recall stone to a dimensional stone
public class RecipeUpgradeStone implements IRecipe {
	private ItemStack recallStone;
	private ItemStack dimenionalStone;
	
	public Item[] recipeList = {
			Items.ender_pearl, Items.blaze_rod, Items.ender_pearl, 
			Items.blaze_rod, RecallStones.itemRecallStone, Items.blaze_rod, 
			Items.ender_pearl, Items.blaze_rod, Items.ender_pearl
		};

	public RecipeUpgradeStone(ItemStack in, ItemStack out) {
		this.recallStone = in;
		this.dimenionalStone = out;
	}
	
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack stone = null;
		for (int i = 0; i < 9; i++) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack != null) {
				if (itemstack.getItem() instanceof ItemRecallStone) {
					stone = itemstack;
					break;
				}
			}
		}
		if ( (stone!=null) && (stone.getItemDamage()==1) ) {
			ItemRecallStone oldStone = (ItemRecallStone)stone.getItem();
			ItemStack output = dimenionalStone.copy();
			ItemDimensionStone newStone = (ItemDimensionStone)output.getItem();
			newStone.setLocation(output, oldStone.getDimension(stone), oldStone.getLocX(stone), oldStone.getLocY(stone), oldStone.getLocZ(stone));
			newStone.setCharge(output, oldStone.getCharge(stone));
			if (stone.getDisplayName()!="") {
				output.setStackDisplayName(stone.getDisplayName());
			}
			output.setItemDamage(1);
			return output;
		}
		return null;
	}

	public ItemStack getRecipeOutput() {
		ItemStack output = dimenionalStone.copy();
		output.setItemDamage(1);
		return output;
	}

	public int getRecipeSize() {
		return 9;
	}
	
	public boolean matches(InventoryCrafting inv, World world) {
		if (inv.getSizeInventory()==9) {
			for (int i = 0; i < 9; i++) {
				ItemStack itemstack = inv.getStackInSlot(i);
				if (itemstack == null) {
					return false;
				} else if (itemstack.getItem()!=recipeList[i]) {
					return false;
				}
			}
			if (inv.getStackInRowAndColumn(1, 1).getItemDamage()==1) {
				return true;
			}
			return false;
		} else {
			return false;
		}
		
	}
	
}
