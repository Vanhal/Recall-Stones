package com.vanhal.recallstones;

import com.vanhal.recallstones.Messages.MessageMarkStone;
import com.vanhal.recallstones.Messages.SendParticles;
import com.vanhal.recallstones.client.GUIHandler;
import com.vanhal.utls.PacketPipeline;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

@Mod(modid = RecallStones.MODID, version = RecallStones.VERSION)
public class RecallStones {
	public static final String MODID = "recallstones";
	public static final String VERSION = "@VERSION@";

	@Instance("recallstones")
    public static RecallStones instance;

	public static final PacketPipeline network = new PacketPipeline();

	//Blocks

	//Items
	public static ItemRecallStone itemRecallStone;
	public static ItemDimensionStone itemDimensionStone;
	public static ItemFollowingStone itemFollowingStone;
	public static ItemRecallStoneBlank itemRecallStoneBlank;
	public static ItemDimensionStoneBlank itemDimensionStoneBlank;
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
		itemRecallStoneBlank = new ItemRecallStoneBlank();

		GameRegistry.registerItem(itemRecallStone, itemRecallStone.itemName);
		GameRegistry.registerItem(itemRecallStoneBlank, itemRecallStoneBlank.itemName);

		if (config.get(itemRecallStone.itemName, "isCraftible", true).getBoolean(true)) {
			GameRegistry.addRecipe(new ItemStack(itemRecallStoneBlank), new Object[]{
				"ses", "ede", "ses", 's', Blocks.stone, 'e', Items.ender_pearl, 'd', Items.diamond
			});
		}

		//add the dimensional stone
		itemDimensionStone = new ItemDimensionStone();
		itemDimensionStone.setConfig(config);
		itemDimensionStoneBlank = new ItemDimensionStoneBlank();

		GameRegistry.registerItem(itemDimensionStone, itemDimensionStone.itemName);
		GameRegistry.registerItem(itemDimensionStoneBlank, itemDimensionStoneBlank.itemName);

		if (config.get(itemDimensionStone.itemName, "isCraftible", true).getBoolean(true)) {
			//add the normal recipe
			GameRegistry.addRecipe(new ItemStack(itemDimensionStoneBlank), new Object[]{
				"ebe", "bsb", "ebe", 'b', Items.blaze_rod, 'e', Items.ender_pearl, 's', itemRecallStoneBlank
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

		//add copy recipies
		GameRegistry.addRecipe(new RecipeCopyStone(itemRecallStone, itemRecallStoneBlank));
		GameRegistry.addRecipe(new RecipeCopyStone(itemDimensionStone, itemDimensionStoneBlank));

		//add items to dungeon loot
		if (config.get(Configuration.CATEGORY_GENERAL, "dungeonLoot", true).getBoolean(true)) {
			ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, new WeightedRandomChestContent(new ItemStack(itemRecallStoneBlank) , 1, 1, 4));
			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(itemRecallStoneBlank) , 1, 1, 4));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(new ItemStack(itemRecallStoneBlank) , 1, 1, 4));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(new ItemStack(itemRecallStoneBlank) , 1, 1, 4));

			ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(new ItemStack(itemDimensionStoneBlank) , 1, 1, 1));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_JUNGLE_CHEST, new WeightedRandomChestContent(new ItemStack(itemDimensionStoneBlank) , 1, 1, 1));
			ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(new ItemStack(itemDimensionStoneBlank) , 1, 1, 1));
		}

		//finally save the config
		config.save();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		//init the network packets
		network.initialise();
		network.registerPacket(MessageMarkStone.class);
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
