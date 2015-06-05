package com.vanhal.recallstones;

import java.util.List;

import com.vanhal.recallstones.Messages.SendParticles;

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

public class ItemDimensionStoneBlank extends ItemRecallStoneBlank {

    public ItemDimensionStoneBlank() {
        super();
        this.activeItem = RecallStones.itemDimensionStone;
        this.setName("dimensionStoneBlank");
    }
}
