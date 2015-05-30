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
    private ItemStack inputItem;

    public RecipeCopyStone(ItemStack itemStack) {
        inputItem = itemStack;
    }

    public ItemStack getCraftingResult(InventoryCrafting inv) {
        ItemStack linkedStone = null;
        int numEmptyStones = 0;

        int invSize = inv.getSizeInventory();
        for (int i = 0; i < invSize; i++) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (itemstack != null) {
                if (itemstack.getItem() instanceof ItemBase) {
                    if (itemstack.getItemDamage() != 0) {
                        linkedStone = itemstack;
                    } else {
                        numEmptyStones++;
                    }
                }
            }
        }

        ItemStack output = linkedStone.copy();

        // HACK: Set the stak size to that of the number of empty stones added
        output.stackSize += numEmptyStones;
        ItemBase outputStone = (ItemBase)output.getItem();
        outputStone.setCharge(output, outputStone.maxCharge);

        return output;
    }

    public ItemStack getRecipeOutput() {
        ItemStack output = inputItem.copy();
        output.setItemDamage(1);
        return output;
    }

    @Override
    public int getRecipeSize() {
        return 1;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World world) {
        int invSize = inv.getSizeInventory();
        boolean haveEmptyStone = false;
        boolean haveLinkedStone = false;
        int otherItems = 0;
        for (int i = 0; i < invSize; i++)  {
            ItemStack temp = inv.getStackInSlot(i);
            if (temp!=null) {
                if (temp.getItem() == inputItem.getItem()) {
                    if(temp.getItemDamage() == 0) {
                        haveEmptyStone = true;
                    } else {
                        if(haveLinkedStone) otherItems++;
                        else haveLinkedStone = true;
                    }
                } else {
                    otherItems++;
                }
            }
        }
        if ( haveEmptyStone && haveLinkedStone && (otherItems == 0) ) {
            return true;
        }
        return false;
    }

}
