package com.vanhal.recallstones.networking;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;


public class SendParticles implements IMessage {
	
	double x, y, z;
	int dimension;
	
	public SendParticles() {
		
	}
	
	public SendParticles(EntityPlayer player) {
		this.x = player.posX;
		this.y = player.posY;
		this.z = player.posZ;
		this.dimension = player.dimension;
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeDouble(this.x);
		buffer.writeDouble(this.y);
		buffer.writeDouble(this.z);
		buffer.writeInt(this.dimension);
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		this.x = buffer.readDouble();
		this.y = buffer.readDouble();
		this.z = buffer.readDouble();
		this.dimension = buffer.readInt();
	}
	
    public static class Handler implements IMessageHandler<SendParticles, IMessage> {
        @Override
        public IMessage onMessage(SendParticles message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
            	message.handleServerSide(ctx.getServerHandler().playerEntity);
            } else if (ctx.side == Side.CLIENT) {
            	message.handleClientSide(Minecraft.getMinecraft().thePlayer);
            }
            return null;
        }
        
    }

	public void handleClientSide(EntityPlayer player) {
		if (player.dimension == this.dimension) {
			for (int i = 0; i < 32; ++i) {
				player.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, 
						this.x + (Math.random() - 0.5), 
						this.y - 1, 
						this.z + (Math.random() - 0.5), 
						0, 0.4 * Math.random(), 0
				);
			}
		}
	}

	public void handleServerSide(EntityPlayer player) {
		// Nothing to do here
	}

}
