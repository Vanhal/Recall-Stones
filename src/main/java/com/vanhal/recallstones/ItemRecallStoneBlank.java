package com.vanhal.recallstones;

import java.util.List;

import com.vanhal.recallstones.Messages.SendParticles;

import com.vanhal.recallstones.client.GUIHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

public class ItemRecallStoneBlank extends ItemBase {

    protected ItemRecallStone activeItem;

    public ItemRecallStoneBlank() {
        super();
        this.setCreativeTab(RecallStones.recallTab);
        activeItem = RecallStones.itemRecallStone;
        this.setName("recallStoneBlank");
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        list.add(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + "Unmarked");
        list.add(EnumChatFormatting.GRAY + "Sneak right click with item");
        list.add(EnumChatFormatting.GRAY + "to mark current location");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
		if (world.isRemote) {
        	if (player.isSneaking() && player.inventory.getFirstEmptyStack() > -1)
            	player.openGui(RecallStones.instance, GUIHandler.RENAME_STONE, world, 0, 0, 0);
		}

        return itemStack;
    }

    public ItemRecallStone getActiveStone() {
        return activeItem;
    }
}
