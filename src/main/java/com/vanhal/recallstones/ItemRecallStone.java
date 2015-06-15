package com.vanhal.recallstones;

import java.util.List;
import java.util.Random;

import com.vanhal.recallstones.client.GUIHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemRecallStone extends ItemBase {

	public ItemRecallStone() {
		super();
		this.setName("recallStone");
		this.maxCharge = 10;
		this.chargesPerUse = 2;
		this.allowCrossDimension = false;
	}

	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			if (itemStack.stackSize == 1) {
				this.moveLocation(itemStack, player, world);
				return itemStack;
			} else if(player.inventory.getFirstEmptyStack() > -1){
				ItemStack used = itemStack.splitStack(1);
				this.moveLocation(used, player, world);
				player.inventory.addItemStackToInventory(itemStack);
				return used;
			} else {
				tellPlayer(player, "No room for leftover Recall Stones!");
				return itemStack;
			}
		}
		return itemStack;
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
		list.add(EnumChatFormatting.GRAY + "Marked at location: " + getLocationString(itemStack));
		list.add(EnumChatFormatting.GRAY + "Marked Dimension: " + itemStack.stackTagCompound.getInteger("world"));
		addCharge(itemStack, list);
	}
}
