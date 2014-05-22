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
		this.setTexture("recallStone");
		this.maxCharge = 10;
		this.chargesPerUse = 2;
		this.allowCrossDimension = false;
	}
	
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		int isActive = itemStack.getItemDamage();
		if (world.isRemote) {
			if (isActive==0) {
				if (player.isSneaking()) {
					player.openGui(RecallStones.instance, GUIHandler.RENAME_STONE, world, 0, 0, 0);
				}
			}
		}
		
		if (!world.isRemote) {
			if (isActive==1) {
				//already marked, so recall
				this.moveLocation(itemStack, player, world);
				
			}
		}
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
	
	
	

}
