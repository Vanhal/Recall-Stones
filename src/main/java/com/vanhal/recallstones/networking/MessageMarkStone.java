package com.vanhal.recallstones.networking;

import com.vanhal.recallstones.RecallStones;
import com.vanhal.recallstones.items.ItemRecallStone;
import com.vanhal.recallstones.items.ItemRecallStoneBlank;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class MessageMarkStone implements IMessage {
	
	String stoneName;
	EnumHand stoneHand;
	
	public MessageMarkStone() {}
	
	public MessageMarkStone(String name, EnumHand hand) {
		stoneName = name;
		stoneHand = hand;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, this.stoneName);
		buffer.writeInt(stoneHand.ordinal());
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		stoneName = ByteBufUtils.readUTF8String(buffer);
		stoneHand = EnumHand.values()[buffer.readInt()];
	}
	
    public static class Handler implements IMessageHandler<MessageMarkStone, IMessage> {
        @Override
        public IMessage onMessage(MessageMarkStone message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
            	message.handleServerSide(ctx.getServerHandler().playerEntity);
            } else if (ctx.side == Side.CLIENT) {
            	message.handleClientSide(Minecraft.getMinecraft().thePlayer);
            }
            return null;
        }
        
    }
	
	public void handleClientSide(EntityPlayer player) {
		RecallStones.logger.info("Recieved Client Message: "+stoneName+", "+stoneHand);
		if (!(player.getHeldItem(stoneHand).getItem() instanceof ItemRecallStoneBlank)) {
			return;
		}

		if(player.getHeldItem(stoneHand).stackSize == 1)
			// Destroy the stack if it's the last item
			player.setHeldItem(stoneHand, null);
		else
			player.getHeldItem(stoneHand).stackSize--;
	}

	public void handleServerSide(EntityPlayer player) {
		RecallStones.logger.info("Recieved Server Message: "+stoneName+", "+stoneHand);
		if (!(player.getHeldItem(stoneHand).getItem() instanceof ItemRecallStoneBlank))
			return;

		if (player.getHeldItem(stoneHand).stackSize > 1 && player.inventory.getFirstEmptyStack() == -1) {
			return;
		}

		// Prepare new stone
		ItemStack heldStack = player.getHeldItem(stoneHand);
		ItemRecallStoneBlank stone = (ItemRecallStoneBlank) heldStack.getItem();
		ItemRecallStone activeStone = stone.getActiveStone();
		ItemStack newStoneStack = new ItemStack(activeStone);
		activeStone.markStone(this.stoneName, player, newStoneStack);

		// Consume Blank Stone and add the new Recall Stone
		if(heldStack.stackSize == 1)
			// Destroy the stack if it's the last item
			player.setHeldItem(stoneHand, null);
		else
			heldStack.stackSize--;

		player.inventory.addItemStackToInventory(newStoneStack);
	}


}
