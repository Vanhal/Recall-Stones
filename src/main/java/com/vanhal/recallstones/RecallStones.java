package com.vanhal.recallstones;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vanhal.recallstones.client.GUIHandler;
import com.vanhal.recallstones.items.ItemDimensionStone;
import com.vanhal.recallstones.items.ItemDimensionStoneBlank;
import com.vanhal.recallstones.items.ItemFollowingStone;
import com.vanhal.recallstones.items.ItemPlayerEssence;
import com.vanhal.recallstones.items.ItemRecallStone;
import com.vanhal.recallstones.items.ItemRecallStoneBlank;
import com.vanhal.recallstones.items.RecallItems;
import com.vanhal.recallstones.networking.MessageMarkStone;
import com.vanhal.recallstones.networking.NetworkHandler;
import com.vanhal.recallstones.networking.SendParticles;
import com.vanhal.recallstones.utls.Ref;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = Ref.MODID, version = Ref.Version)
public class RecallStones {

	@Instance(Ref.MODID)
    public static RecallStones instance;

	//logger
	public static final Logger logger = LogManager.getLogger(Ref.MODID);

	//Creative Tab
	public static CreativeTabs recallTab = new CreativeTabs("recallTab") {
		@Override
		public Item getTabIconItem() {
			return RecallItems.itemRecallStone;
		}
	};
	
	public RecallStones() {
		logger.info("Recalling back to home");
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		NetworkHandler.registerMessageHandler(MessageMarkStone.Handler.class, MessageMarkStone.class, Side.SERVER);
		NetworkHandler.registerMessageHandler(SendParticles.Handler.class, SendParticles.class, Side.CLIENT);
		
		RecallItems.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		//init the GUIHandler
		new GUIHandler();
		
		RecallItems.init(event);

		//init the event handlers
		MinecraftForge.EVENT_BUS.register(new EventsRecall());
	}

	@EventHandler
	public void postInitialise(FMLPostInitializationEvent event) {
		RecallItems.postInit();
	}
}
