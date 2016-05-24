package com.vanhal.recallstones.items;

import com.vanhal.recallstones.recipes.RecipeCopyStone;
import com.vanhal.recallstones.recipes.RecipeFollowingStone;
import com.vanhal.recallstones.recipes.RecipeRechargeStone;
import com.vanhal.recallstones.recipes.RecipeUpgradeStone;
import com.vanhal.recallstones.utls.Ref;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;

public class RecallItems {

	public static void preInit(FMLPreInitializationEvent event) {
		//load the config file
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		//register the recipe types
		RecipeSorter.INSTANCE.register(Ref.MODID+":copyStone", RecipeCopyStone.class, RecipeSorter.Category.SHAPELESS, "after:forge:shapelessore");
		RecipeSorter.INSTANCE.register(Ref.MODID+":rechargeStone", RecipeRechargeStone.class, RecipeSorter.Category.SHAPELESS, "after:forge:shapelessore");
		RecipeSorter.INSTANCE.register(Ref.MODID+":upgradeStone", RecipeUpgradeStone.class, RecipeSorter.Category.SHAPELESS, "after:forge:shapelessore");
		RecipeSorter.INSTANCE.register(Ref.MODID+":followingStone", RecipeFollowingStone.class, RecipeSorter.Category.SHAPELESS, "after:forge:shapelessore");

		//add the recall stone
		itemRecallStone = new ItemRecallStone();
		itemRecallStone.setConfig(config);
		itemRecallStoneBlank = new ItemRecallStoneBlank();

		GameRegistry.registerItem(itemRecallStone, itemRecallStone.itemName);
		GameRegistry.registerItem(itemRecallStoneBlank, itemRecallStoneBlank.itemName);

		if (config.get(itemRecallStone.itemName, "isCraftible", true).getBoolean(true)) {
			GameRegistry.addRecipe(new ItemStack(itemRecallStoneBlank), new Object[]{
				"ses", "ede", "ses", 's', Blocks.STONE, 'e', Items.ENDER_PEARL, 'd', Items.DIAMOND
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
				"ebe", "bsb", "ebe", 'b', Items.BLAZE_ROD, 'e', Items.ENDER_PEARL, 's', itemRecallStoneBlank
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

		//finally save the config
		config.save();
	}
	
	public static void init(FMLInitializationEvent event) {
		itemRecallStone.init();
		itemDimensionStone.init();
		itemFollowingStone.init();
		itemRecallStoneBlank.init();
		itemDimensionStoneBlank.init();
		itemPlayerEssence.init();
	}
	
	public static void postInit() {

	}
	
	public static ItemRecallStone itemRecallStone;
	public static ItemDimensionStone itemDimensionStone;
	public static ItemFollowingStone itemFollowingStone;
	public static ItemRecallStoneBlank itemRecallStoneBlank;
	public static ItemDimensionStoneBlank itemDimensionStoneBlank;
	public static ItemPlayerEssence itemPlayerEssence;
	
	//settings
	public static boolean dropEssence = true;
}
