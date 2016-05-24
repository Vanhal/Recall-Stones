package com.vanhal.recallstones.items;

import java.util.List;
import java.util.Random;

import com.vanhal.recallstones.client.GUIHandler;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemRecallStone extends ItemBase {

	public ItemRecallStone() {
		super();
		this.setName("recallStone");
		this.maxCharge = 10;
		this.chargesPerUse = 2;
		this.allowCrossDimension = false;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			if (itemStack.stackSize == 1) {
				this.moveLocation(itemStack, player, world);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
			} else if(player.inventory.getFirstEmptyStack() > -1){
				ItemStack used = itemStack.splitStack(1);
				this.moveLocation(used, player, world);
				player.inventory.addItemStackToInventory(itemStack);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, used);
			} else {
				tellPlayer(player, "No room for leftover Recall Stones!");
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStack);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
	}

	public void markStone(String name, EntityPlayer player, ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemRecallStone) {
			if (this.setLocation(itemStack, player.dimension, player.posX, player.posY, player.posZ)) {
				if (name!="") {
					itemStack.setStackDisplayName(name);
				}
				this.tellPlayer(player, "Your Location has been marked");
			}
		}
	}

	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		if (itemStack != null) {
			if (itemStack.hasTagCompound()) {
				list.add(TextFormatting.GRAY + "Marked at location: " + getLocationString(itemStack));
				list.add(TextFormatting.GRAY + "Marked Dimension: " + itemStack.getTagCompound().getInteger("world"));
				addCharge(itemStack, list);
			}
		}
	}
}
