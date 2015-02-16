package com.vanhal.recallstones;

import com.vanhal.recallstones.Messages.SendName;
import com.vanhal.recallstones.Messages.SendParticles;
import com.vanhal.recallstones.client.GUIHandler;
import com.vanhal.utls.PacketPipeline;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

@Mod(modid = RecallStones.MODID, version = RecallStones.VERSION)
public class RecallStones {
	public static final String MODID = "recallstones";
	public static final String VERSION = "@VERSION@";

	@Instance("recallstones")
    public static RecallStones instance;

	public static final PacketPipeline network = new PacketPipeline();

	//Blocks

	//Items
	public static ItemBase itemRecallStone;
	public static ItemBase itemDimensionStone;
	public static ItemBase itemFollowingStone;
	public static ItemPlayerEssence itemPlayerEssence;

	//settings
	public static boolean dropEssence = true;

	//Creative Tab
	public static CreativeTabs recallTab = new CreativeTabs("recallTab") {
		@Override
		public Item getTabIconItem() {
			return itemRecallStone;
		}
	};

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		//load the config file
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		//add the recall stone
		itemRecallStone = new ItemRecallStone();
		itemRecallStone.setConfig(config);
		GameRegistry.registerItem(itemRecallStone, itemRecallStone.itemName);

		if (config.get(itemRecallStone.itemName, "isCraftible", true).getBoolean(true)) {
			GameRegistry.addRecipe(new ItemStack(itemRecallStone), new Object[]{
				"ses", "ede", "ses", 's', Blocks.stone, 'e', Items.ender_pearl, 'd', Items.diamond
			});
		}

		//add the dimensional stone
		itemDimensionStone = new ItemDimensionStone();
		itemDimensionStone.setConfig(config);
		GameRegistry.registerItem(itemDimensionStone, itemDimensionStone.itemName);

		if (config.get(itemDimensionStone.itemName, "isCraftible", true).getBoolean(true)) {
			//add the normal recipe
			GameRegistry.addRecipe(new ItemStack(itemDimensionStone), new Object[]{
				"ebe", "bsb", "ebe", 'b', Items.blaze_rod, 'e', Items.ender_pearl, 's', itemRecallStone
			});
			//add the upgrade recipe
			GameRegistry.addRecipe(new RecipeUpgradeStone(new ItemStack(itemRecallStone), new ItemStack(itemDimensionStone)));
		}

		//add following stone
		itemFollowingStone = new ItemFollowingStone();
		itemFollowingStone.setConfig(config);
		GameRegistry.registerItem(itemFollowingStone, itemFollowingStone.itemName);

		//add the player essence
		dropEssence = config.get(Configuration.CATEGORY_GENERAL, "dropPlayerEssence", true).getBoolean(true);
		itemPlayerEssence = new ItemPlayerEssence();
		GameRegistry.registerItem(itemPlayerEssence, itemPlayerEssence.itemName);

		//add the following recipe
		if (config.get(itemFollowingStone.itemName, "isCraftible", true).getBoolean(true)) {

			GameRegistry.addRecipe(new RecipeFollowingStone());
		}

		//add refuel recipes
		GameRegistry.addRecipe(new RecipeRechargeStone(new ItemStack(itemRecallStone)));
		GameRegistry.addRecipe(new RecipeRechargeStone(new ItemStack(itemDimensionStone)));


		//add items to dungeon loot
		if (config.get(Configuration.CATEGORY_GENERAL, "dungeonLoot", true).getBoolean(true)) {
			ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, new WeightedRandomChestContent(new ItemStack(itemRecallStone) , 1, 1, 4));
			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(itemRecallStone) , 1, 1, 4));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(new ItemStack(itemRecallStone) , 1, 1, 4));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(new ItemStack(itemRecallStone) , 1, 1, 4));

			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(itemDimensionStone) , 1, 1, 1));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(new ItemStack(itemDimensionStone) , 1, 1, 1));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(new ItemStack(itemDimensionStone) , 1, 1, 1));
		}

		//finally save the config
		config.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		//init the network packets
		network.initialise();
		network.registerPacket(SendName.class);
		network.registerPacket(SendParticles.class);
		//init the GUIHandler
		new GUIHandler();

		//init the event handlers
		MinecraftForge.EVENT_BUS.register(new EventsRecall());
	}

	@EventHandler
	public void postInitialise(FMLPostInitializationEvent event) {
		network.postInitialise();
	}
}
