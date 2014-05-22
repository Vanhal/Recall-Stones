package com.vanhal.recallstones;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemPlayerEssence extends Item {
	public String itemName = "playerEssence";
	
	public ItemPlayerEssence() {
		this.setCreativeTab(RecallStones.recallTab);
		this.setMaxStackSize(16);
		this.setTextureName(RecallStones.MODID+":"+this.itemName);
		this.setUnlocalizedName(this.itemName);
	}
	
	public void setPlayer(ItemStack itemStack, EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			String username = player.getCommandSenderName();
			if (itemStack.stackTagCompound==null) itemStack.stackTagCompound = new NBTTagCompound(); 
			itemStack.stackTagCompound.setString("username", username);
		}
	}
	
	public String getUsername(ItemStack itemStack) {
		if (itemStack.stackTagCompound==null) return null;
		return itemStack.stackTagCompound.getString("username");
	}
	
	public EntityPlayer getPlayer(ItemStack itemStack, World world) {
		if (!world.isRemote) {
			String username = this.getUsername(itemStack);
			return MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(username);
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		String username = this.getUsername(itemStack);
		if (username != null) {
			list.add(EnumChatFormatting.GRAY + "Essence of "+username);
		}
	}
}
