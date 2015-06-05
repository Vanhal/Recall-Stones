package com.vanhal.recallstones;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeCopyStone implements IRecipe {

    private ItemBase activeStone;
    private ItemBase blankStone;

    public RecipeCopyStone(ItemBase activeStone, ItemBase blankStone) {
        this.activeStone = activeStone;
        this.blankStone = blankStone;
    }

    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack activeStoneStack = null;
        int numBlankStones = 0;

        int invSize = inv.getSizeInventory();
        for (int i = 0; i < invSize; i++) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack != null) {
                if (itemstack.getItem() == activeStone) {
                    activeStoneStack = itemstack;
                } else {
                    numBlankStones++;
                }
            }
        }

        // Trust that we found a stone stack, since this recipe must pass Match to get here
        ItemStack output = activeStoneStack.copy();

        // Set the stack size to that of the number of empty stones added
        output.stackSize = numBlankStones + 1;
        ItemBase outputStone = (ItemBase)output.getItem();
        outputStone.setCharge(output, outputStone.maxCharge);

        return output;
    }

    public ItemStack getRecipeOutput() {
        ItemStack output = new ItemStack(activeStone);
        return output;
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        int invSize = inv.getSizeInventory();
        boolean haveBlankStone = false;
        boolean haveActiveStone = false;
        int otherItems = 0;
        for (int i = 0; i < invSize; i++) {
            ItemStack itemStack = inv.getStackInSlot(i);
            if (itemStack != null) {
                if (itemStack.getItem() == blankStone) {
                    haveBlankStone = true;
                } else if (itemStack.getItem() == activeStone) {
                    if (haveActiveStone
                            // Only accept fully-charged stones
                            || activeStone.getCharge(itemStack) != activeStone.maxCharge)
                        otherItems++;
                    else haveActiveStone = true;
                } else {
                    otherItems++;
                }
            }
        }

        return haveBlankStone && haveActiveStone && (otherItems == 0);
    }

}
