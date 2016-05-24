package com.vanhal.recallstones.items;

import java.util.List;

import com.vanhal.recallstones.RecallStones;
import com.vanhal.recallstones.networking.NetworkHandler;
import com.vanhal.recallstones.networking.SendParticles;
import com.vanhal.recallstones.utls.Ref;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

public abstract class ItemBase extends Item {
	public String itemName;
	
	//settings
	public int coolDownTime = 200;
	public boolean allowCrossDimension = true;
	public boolean requireCharge = true;
	public int chargesPerPearl = 10;

	public int maxCharge = 10;
	public int chargesPerUse = 2;
	
	public SoundEvent warpSound = new SoundEvent(new ResourceLocation(Ref.MODID+":warp"));
	
	public ItemBase() {
	}
	
	public void setConfig(Configuration config) {
		//values
		this.requireCharge = config.get(Configuration.CATEGORY_GENERAL, "requireCharge", true).getBoolean(true);
		this.chargesPerPearl = config.get(Configuration.CATEGORY_GENERAL, "chargesPerPearl", 5).getInt();

		this.chargesPerUse = config.get(this.itemName, "chargesPerUse", this.chargesPerUse).getInt();
		this.maxCharge = config.get(this.itemName, "maxCharge", this.maxCharge).getInt();
		this.coolDownTime = config.get(this.itemName, "coolDownTime", 10).getInt() * 20;
	}
	
	protected void setName(String name) {
		this.itemName = name;
		this.setUnlocalizedName(this.itemName);
	}

	//deal with the cool down time
	@Override
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
		if (!world.isRemote) {
			int coolDown = this.getCoolDown(itemStack);
			if (coolDown>0) {
				itemStack.getTagCompound().setInteger("coolDown", coolDown - 1);
			} else if (coolDown<0) {
				itemStack.getTagCompound().setInteger("coolDown", 0);
			}
		}
	}
	
	//get the current cool down level
	protected int getCoolDown(ItemStack itemStack) {
		if ( (itemStack.getTagCompound() != null) && (itemStack.getTagCompound().hasKey("coolDown")) ) {
			return itemStack.getTagCompound().getInteger("coolDown");
		}
		return 0;
	}
	
	//start the cool down timer (Set it to what ever the value is meant to be
	protected void startCoolDown(ItemStack itemStack) {
		if (itemStack.getTagCompound() != null) {
			itemStack.getTagCompound().setInteger("coolDown", this.coolDownTime);
		}
	}
	
	//get the marked location in a nice string format
	protected String getLocationString(ItemStack itemStack) {
		String output = "";
		if (itemStack.getTagCompound() != null) {
			if (itemStack.getTagCompound().hasKey("markX")) {
				output += (int)itemStack.getTagCompound().getDouble("markX");
				output += ", "+(int)itemStack.getTagCompound().getDouble("markY");
				output += ", "+(int)itemStack.getTagCompound().getDouble("markZ");
			}
			
		}
		return output;
	}
	
	//getters for the 4 location fields
	public int getDimension(ItemStack itemStack) {
		if (itemStack.getTagCompound() != null) {
			return itemStack.getTagCompound().getInteger("world");
		}
		return 0;
	}
	
	public double getLocX(ItemStack itemStack) {
		if (itemStack.getTagCompound() != null) {
			return itemStack.getTagCompound().getDouble("markX");
		}
		return 0.0;
	}
	
	public double getLocY(ItemStack itemStack) {
		if (itemStack.getTagCompound() != null) {
			return itemStack.getTagCompound().getDouble("markY");
		}
		return 0.0;
	}
	
	public double getLocZ(ItemStack itemStack) {
		if (itemStack.getTagCompound() != null) {
			return itemStack.getTagCompound().getDouble("markZ");
		}
		return 0.0;
	}
	
	//initilise the NBT stuff
	public void init(ItemStack itemStack) {
		if (itemStack.getTagCompound() == null) {
			itemStack.setTagCompound(new NBTTagCompound());
			if (this.requireCharge) {
				itemStack.getTagCompound().setInteger("currentCharge", this.maxCharge);
			}
		}
	}
	
	
	//set the location that this stone will recall to
	public boolean setLocation(ItemStack itemStack, int dimension, double x, double y, double z) {
		this.init(itemStack);
		if (itemStack.getTagCompound() != null) {
			itemStack.getTagCompound().setInteger("world", dimension);
			itemStack.getTagCompound().setDouble("markX", Math.floor(x));
			itemStack.getTagCompound().setDouble("markY", Math.floor(y));
			itemStack.getTagCompound().setDouble("markZ", Math.floor(z));
			return true;
		}
		return false;
	}
	
	//do the actual teleport
	public boolean moveLocation(ItemStack itemStack, EntityPlayer player, World world) {
		int coolDown = this.getCoolDown(itemStack);
		if ( (itemStack.getTagCompound() != null) && (coolDown==0) ) {
			int targetDimension = itemStack.getTagCompound().getInteger("world");
			if ( (player.dimension == targetDimension) || (allowCrossDimension) ) {
				
				if (this.checkTarget(
						itemStack.getTagCompound().getInteger("world"),
						(int)itemStack.getTagCompound().getDouble("markX"),
						(int)itemStack.getTagCompound().getDouble("markY"),
						(int)itemStack.getTagCompound().getDouble("markZ")
				)) {
					
					if (this.consumeCharge(itemStack)) {
						this.animateTP(player);
						
						if (player.dimension != targetDimension) {
							player.changeDimension(targetDimension);
						}
							
						if (player.isRiding()){
							player.dismountRidingEntity();
	                    }
						player.setPositionAndUpdate(
								itemStack.getTagCompound().getDouble("markX") + 0.5,
								itemStack.getTagCompound().getDouble("markY"), 
								itemStack.getTagCompound().getDouble("markZ") + 0.5
								);
	
						this.animateTP(player);
						
						this.startCoolDown(itemStack);
						return true;
					} else {
						this.tellPlayer(player, TextFormatting.RED + "Stone does not have enough charges left");
						this.tellPlayer(player, "Craft with an Ender Pearl to recharge");
					}
				} else {
					this.tellPlayer(player, TextFormatting.RED + "Something is blocking the destination");
				}
			} else {
				this.tellPlayer(player, TextFormatting.RED + "You can not cross dimensions with this stone");
			}
		} else {
			this.tellPlayer(player, "Stone is still recharging");
		}
		return false;
	}
	
	//consume a charge on the stone, returns true is successful
	public boolean consumeCharge(ItemStack itemStack) {
		if (this.requireCharge) {
			if (itemStack.getTagCompound() == null) return false;
			
			int currentCharge = this.getCharge(itemStack);
			
			if ((currentCharge - this.chargesPerUse)<0) {
				return false;
			} else {
				this.setCharge(itemStack, currentCharge - this.chargesPerUse);
				return true;
			}
		} else {
			return true;
		}
	}
	
	//add a single charge item (pearl)
	public ItemStack addCharge(ItemStack itemStack, int number) {
		if (this.requireCharge) {
			if (itemStack.getTagCompound() != null) {
				int currentCharge = this.getCharge(itemStack);
				int newCharge = currentCharge + (this.chargesPerPearl * number);
				if (newCharge>=this.maxCharge) newCharge = this.maxCharge;
				this.setCharge(itemStack, newCharge);
			}
		}
		return itemStack;
	}
	
	//check if we are able to add any charge
	public boolean canAddCharge(ItemStack itemStack, int number) {
		if (this.requireCharge) {
			if (itemStack.getTagCompound() == null) return false;
			int currentCharge = ( this.getCharge(itemStack) + (this.chargesPerPearl * (number-1)));

			return currentCharge < this.maxCharge;
		} else {
			return false;
		}
	}
	
	public void setCharge(ItemStack itemStack, int charge) {
		if (itemStack.getTagCompound() == null) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
		if (itemStack.getTagCompound() != null) {
			itemStack.getTagCompound().setInteger("currentCharge", charge);
		}
	}
	
	public int getCharge(ItemStack itemStack) {
		int charge = this.maxCharge;
		if (itemStack.getTagCompound() != null) {
			if (itemStack.getTagCompound().hasKey("currentCharge")) {
				charge = itemStack.getTagCompound().getInteger("currentCharge");
			}
		}
		return charge;
	}
	
	
	//check the target location and see if it's clear to tp to
	public boolean checkTarget(int targetDimension, int targetX, int targetY, int targetZ) {
		World world = DimensionManager.getWorld(targetDimension);
		Block target1 = world.getBlockState(new BlockPos(targetX, targetY, targetZ)).getBlock();
		Block target2 = world.getBlockState(new BlockPos(targetX, targetY + 1, targetZ)).getBlock();
		
		return (target1.isReplaceable(world, new BlockPos(targetX, targetY, targetZ)))
				&& (target2.isReplaceable(world, new BlockPos(targetX, targetY + 1, targetZ)));
	}
	
	protected void animateTP(EntityPlayer player) {
		player.worldObj.playSound(player, player.getPosition(), warpSound, 
				SoundCategory.AMBIENT, 0.8f, 1.0f);
		NetworkHandler.sendToAllAroundNearby(new SendParticles(player), player);
	}
	
	protected void tellPlayer(EntityPlayer player, String message) {
		player.addChatMessage(new TextComponentString(message));
	}

	@SideOnly(Side.CLIENT)
	public abstract void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4);
	
	public void addCharge(ItemStack itemStack, List list) {
		if (itemStack.getTagCompound() != null) {
			if (this.requireCharge) {
				int currentCharge = getCharge(itemStack);
				list.add(TextFormatting.DARK_GREEN  + "Current Charges: "+currentCharge+"/"+this.maxCharge+" ("+this.chargesPerUse+" per use)");
				if (currentCharge == 0) {
					list.add(TextFormatting.RED  + "Craft with an Ender Pearl to recharge");
				}
			}

			if (itemStack.getTagCompound().hasKey("coolDown")) {
				int coolDown = itemStack.getTagCompound().getInteger("coolDown");
				if (coolDown>0) {
					list.add(TextFormatting.RED  + "Recharging. Time left: "+(int)Math.ceil(coolDown/20));
				}
			}
		}
	}
	
	public void init() {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
			.register(this, 0, new ModelResourceLocation(Ref.MODID + ":" + itemName, "inventory"));
	}
	
}
