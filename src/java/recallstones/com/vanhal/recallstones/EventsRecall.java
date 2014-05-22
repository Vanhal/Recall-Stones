package com.vanhal.recallstones;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;;


//http://www.minecraftforum.net/topic/1419836-131-forge-4x-events-howto/
//http://www.minecraftforge.net/wiki/Event_Reference
public class EventsRecall {
	


	@SubscribeEvent
	public void PlayerDropsEvent(PlayerDropsEvent event) {
		if (event.source.getSourceOfDamage()!=null) {
			if (event.source.getSourceOfDamage() instanceof EntityPlayer) {
				//drop essence
				if (RecallStones.dropEssence) {
					EntityPlayer deadPlayer = event.entityPlayer;
					ItemPlayerEssence essence = RecallStones.itemPlayerEssence;
					ItemStack essenceStack = new ItemStack(essence);
					essence.setPlayer(essenceStack, deadPlayer);
					event.entity.entityDropItem(essenceStack, 0.5F);
				}
			}
		}
	}

}
