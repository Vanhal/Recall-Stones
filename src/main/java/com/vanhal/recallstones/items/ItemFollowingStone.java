package com.vanhal.recallstones.items;

import java.util.List;

import com.vanhal.recallstones.RecallStones;
import com.vanhal.recallstones.client.GUIHandler;
import com.vanhal.recallstones.utls.Ref;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemFollowingStone extends ItemBase {

	public ItemFollowingStone() {
		super();
		this.setName("followingStone");
		this.setName(Ref.MODID + ":" + "followingStone");
		this.allowCrossDimension = true;
		this.maxCharge = 100;
		this.chargesPerUse = 25;
	}
	
	public void initUser(ItemStack itemStack, String username) {
		this.init(itemStack);
		this.setUsername(itemStack, username);
		this.setDamage(itemStack, 0);
	}
	
	//set and get the player enity
	public void setPlayer(ItemStack itemStack, EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			String username = player.getName();
			this.setUsername(itemStack, username);
		}
	}
	
	public EntityPlayer getPlayer(ItemStack itemStack, World world) {
		if (!world.isRemote) {
			String username = this.getUsername(itemStack);
//			return MinecraftServer.getServer().getEntityWorld().getPlayerEntityByName(username);
		}
		return null;
	}
	
	
	//set and get the username
	public void setUsername(ItemStack itemStack, String username) {
		if (itemStack.getTagCompound()==null) itemStack.setTagCompound(new NBTTagCompound()); 
		itemStack.getTagCompound().setString("username", username);
	}
	
	public String getUsername(ItemStack itemStack) {
		if (itemStack.getTagCompound()==null) return null;
		return itemStack.getTagCompound().getString("username");
	}
	
	//warp to the player if they exist, otherwise don't
	public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			//test for the player online
			EntityPlayer targetPlayer = this.getPlayer(itemStack, world);
			if (targetPlayer == null) {
				this.tellPlayer(player, "The stone can not sense the location of " + this.getUsername(itemStack));
			} else {
				if (targetPlayer.isDead) {
					this.tellPlayer(player, this.getUsername(itemStack) + " is currently not in the land of the living");
				} else {
					//set the location of the player and warp
					this.setLocation(itemStack, targetPlayer.dimension, targetPlayer.posX, targetPlayer.posY, targetPlayer.posZ);
					this.moveLocation(itemStack, player, world);
				}
			}
		}
		return itemStack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		String username = this.getUsername(itemStack);

		if (username == null)
			list.add(TextFormatting.GRAY + "" + TextFormatting.ITALIC + "Unlinked");
		else
			list.add(TextFormatting.GRAY + "Linked to " + username);

		this.addCharge(itemStack, list);
	}
}
