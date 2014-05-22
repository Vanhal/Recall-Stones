package com.vanhal.recallstones.Messages;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

import com.vanhal.utls.AbstractMessage;

public class SendParticles extends AbstractMessage {
	
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
	public void encodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		buffer.writeDouble(this.x);
		buffer.writeDouble(this.y);
		buffer.writeDouble(this.z);
		buffer.writeInt(this.dimension);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf buffer) {
		this.x = buffer.readDouble();
		this.y = buffer.readDouble();
		this.z = buffer.readDouble();
		this.dimension = buffer.readInt();
	}

	@Override
	public void handleClientSide(EntityPlayer player) {
		if (player.dimension == this.dimension) {
			for (int i = 0; i < 32; ++i) {
				player.worldObj.spawnParticle("largesmoke", 
						this.x + (Math.random() - 0.5), 
						this.y - 1, 
						this.z + (Math.random() - 0.5), 
						0, 0.4 * Math.random(), 0
				);
			}
		}
	}

	@Override
	public void handleServerSide(EntityPlayer player) {
		// TODO Auto-generated method stub
	}

}
