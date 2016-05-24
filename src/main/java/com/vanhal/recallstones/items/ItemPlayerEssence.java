package com.vanhal.recallstones.items;

import java.util.List;

import com.vanhal.recallstones.RecallStones;
import com.vanhal.recallstones.utls.Ref;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemPlayerEssence extends Item {
	public String itemName = "playerEssence";
	
	public ItemPlayerEssence() {
		this.setCreativeTab(RecallStones.recallTab);
		this.setMaxStackSize(16);
		this.setUnlocalizedName(this.itemName);
	}
	
	public void setPlayer(ItemStack itemStack, EntityPlayer player) {
		if (!player.worldObj.isRemote) {
			String username = player.getName();
			if (itemStack.getTagCompound()==null) itemStack.setTagCompound(new NBTTagCompound()); 
			itemStack.getTagCompound().setString("username", username);
		}
	}
	
	public String getUsername(ItemStack itemStack) {
		if (itemStack.getTagCompound()==null) return null;
		return itemStack.getTagCompound().getString("username");
	}
	
	public EntityPlayer getPlayer(ItemStack itemStack, World world) {
		if (!world.isRemote) {
			String username = this.getUsername(itemStack);
			//return MinecraftServer.getServer().getConfigurationManager().func_152612_a(username);
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		String username = this.getUsername(itemStack);
		if (username != null) {
			list.add(TextFormatting.GRAY + "Essence of "+username);
		}
	}
	
	public void init() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(this, 0, new ModelResourceLocation(Ref.MODID + ":" + itemName, "inventory"));
	}
}
