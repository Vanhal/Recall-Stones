package com.vanhal.recallstones.Messages;

import net.minecraft.entity.player.EntityPlayer;

import com.vanhal.recallstones.ItemRecallStone;
import com.vanhal.utls.AbstractMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class MessageMarkStone extends AbstractMessage {
	
	String stoneName;
	
	public MessageMarkStone() {}
	
	public MessageMarkStone(String name) {
		this.stoneName = name;
	}

	@Override
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		ByteBufUtils.writeUTF8String(buffer, this.stoneName);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		// TODO Auto-generated method stub
		this.stoneName = ByteBufUtils.readUTF8String(buffer);
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// TODO Auto-generated method stub
		if (player.getHeldItem().getItem() instanceof ItemRecallStone) {
			ItemRecallStone stone = (ItemRecallStone)player.getHeldItem().getItem();
			stone.nameStone(this.stoneName, player, player.getHeldItem());
		}
	}


}
