package com.vanhal.recallstones.items;

import java.util.List;

import com.vanhal.recallstones.RecallStones;
import com.vanhal.recallstones.client.GUIHandler;
import com.vanhal.recallstones.networking.SendParticles;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

public class ItemRecallStoneBlank extends ItemBase {

    protected ItemRecallStone activeItem;

    public ItemRecallStoneBlank() {
        super();
        this.setCreativeTab(RecallStones.recallTab);
        activeItem = RecallItems.itemRecallStone;
        this.setName("recallStoneBlank");
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
        list.add(TextFormatting.GRAY + "" + TextFormatting.ITALIC + "Unmarked");
        list.add(TextFormatting.GRAY + "Sneak right click with item");
        list.add(TextFormatting.GRAY + "to mark current location");
    }

    @Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStack, World world, EntityPlayer player, EnumHand hand) {
		if (world.isRemote) {
        	if (player.isSneaking() && player.inventory.getFirstEmptyStack() > -1)
            	player.openGui(RecallStones.instance, GUIHandler.RENAME_STONE, world, hand.ordinal(), 0, 0);
		}

        return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
    }

    public ItemRecallStone getActiveStone() {
        return activeItem;
    }
}
