package com.vanhal.recallstones.recipes;

import java.util.ArrayList;
import java.util.List;

import com.vanhal.recallstones.RecallStones;
import com.vanhal.recallstones.items.ItemFollowingStone;
import com.vanhal.recallstones.items.ItemPlayerEssence;
import com.vanhal.recallstones.items.RecallItems;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;

public class RecipeFollowingStone extends ShapelessRecipes {

	public RecipeFollowingStone() {
		super(new ItemStack(RecallItems.itemPlayerEssence), getRecipe());
	}
	
	public static List<ItemStack> getRecipe() {
		List<ItemStack> list = new ArrayList<ItemStack>();
		list.add(new ItemStack(RecallItems.itemRecallStone));
		list.add(new ItemStack(RecallItems.itemPlayerEssence));
		return list;
	}
	
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		int invSize = inv.getSizeInventory();
		ItemStack essence = null;
		for (int i = 0; i < invSize; i++)  {
			ItemStack temp = inv.getStackInSlot(i);
			if (temp!=null) {
				if (temp.getItem() instanceof ItemPlayerEssence) {
					essence = temp;
					break;
				}
			}
		}
		if (essence!=null) {
			ItemPlayerEssence itemEssence = (ItemPlayerEssence)essence.getItem();
			String targetPlayer = itemEssence.getUsername(essence);
			if (targetPlayer!="") {
				ItemStack output = new ItemStack(RecallItems.itemFollowingStone);
				ItemFollowingStone stone = (ItemFollowingStone) output.getItem();
				stone.initUser(output, targetPlayer);
				
				return output;
			}
		}
		return null;
    }
	
	public int getRecipeSize() {
		return 2;
	}
	
	
	public ItemStack getRecipeOutput() {
		ItemStack output = new ItemStack(RecallItems.itemFollowingStone);
		return output;
	}
}
