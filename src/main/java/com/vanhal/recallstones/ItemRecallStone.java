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
		//if (itemStack.stackSize == 1)	// Only allow movement if there is a single stone equipped
			this.moveLocation(itemStack, player, world);

		// HACK: Allowing teleportation with multiple stones selected makes for all stones being drained at once,
		//		but this code is double-printing error messages!
		//else
		//	tellPlayer(player, "Cannot use multiple stones at once!");
		return itemStack;
	}
	
	public void nameStone(String name, EntityPlayer player, ItemStack itemStack) {
		if (itemStack.getItemDamage()==0) {
			//Not yet active, record the location and store it
			if (this.setLocation(itemStack, player.dimension, player.posX, player.posY, player.posZ)) {
				itemStack.setItemDamage(1);
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
