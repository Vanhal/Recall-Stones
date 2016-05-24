package com.vanhal.recallstones.client;

import com.vanhal.recallstones.RecallStones;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class GUIHandler implements IGuiHandler {
	
	public static final int RENAME_STONE = 1;
	
	public GUIHandler() {
		NetworkRegistry.INSTANCE.registerGuiHandler(RecallStones.instance, this);
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GUIHandler.RENAME_STONE) {
			return new GUINameStone(player, EnumHand.values()[x]);
		} else {
			return null;
		}
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if (ID == GUIHandler.RENAME_STONE) {
			//return new GUINameStone(player);
			return null;
		} else {
			return null;
		}
	}

}
