package com.vanhal.recallstones.Messages;

import com.vanhal.recallstones.ItemRecallStone;
import com.vanhal.recallstones.ItemRecallStoneBlank;
import net.minecraft.entity.player.EntityPlayer;
import com.vanhal.utls.AbstractMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.ByteBufUtils;
import net.minecraft.item.ItemStack;

public class MessageMarkStone extends AbstractMessage {
	
	String stoneName;
	
	public MessageMarkStone() {}
	
	public MessageMarkStone(String name) {
		this.stoneName = name;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.stoneName);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		this.stoneName = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		if (!(player.getHeldItem().getItem() instanceof ItemRecallStoneBlank)) {
			return;
		}

		if(player.getHeldItem().stackSize == 1)
			// Destroy the stack if it's the last item
			player.destroyCurrentEquippedItem();
		else
			player.getHeldItem().stackSize--;
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		if (!(player.getHeldItem().getItem() instanceof ItemRecallStoneBlank))
			return;

		if (player.getHeldItem().stackSize > 1 && player.inventory.getFirstEmptyStack() == -1) {
			return;
		}

		// Prepare new stone
		ItemStack heldStack = player.getHeldItem();
		ItemRecallStoneBlank stone = (ItemRecallStoneBlank) heldStack.getItem();
		ItemRecallStone activeStone = stone.getActiveStone();
		ItemStack newStoneStack = new ItemStack(activeStone);
		activeStone.markStone(this.stoneName, player, newStoneStack);

		// Consume Blank Stone and add the new Recall Stone
		if(heldStack.stackSize == 1)
			// Destroy the stack if it's the last item
			player.destroyCurrentEquippedItem();
		else
			heldStack.stackSize--;

		player.inventory.addItemStackToInventory(newStoneStack);
	}


}
