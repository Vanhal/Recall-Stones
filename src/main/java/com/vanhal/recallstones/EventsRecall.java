package com.vanhal.recallstones;

import com.vanhal.recallstones.items.ItemPlayerEssence;
import com.vanhal.recallstones.items.RecallItems;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
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
		if (event.getSource().getSourceOfDamage()!=null) {
			if (event.getSource().getSourceOfDamage() instanceof EntityPlayer) {
				//drop essence
				if (RecallItems.dropEssence) {
					EntityPlayer deadPlayer = event.getEntityPlayer();
					ItemPlayerEssence essence = RecallItems.itemPlayerEssence;
					ItemStack essenceStack = new ItemStack(essence);
					essence.setPlayer(essenceStack, deadPlayer);
					event.getEntity().entityDropItem(essenceStack, 0.5F);
				}
			}
		}
	}

}
