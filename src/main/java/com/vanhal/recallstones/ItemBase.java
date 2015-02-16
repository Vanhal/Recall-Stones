package com.vanhal.recallstones;

import java.util.List;

import com.vanhal.recallstones.Messages.SendParticles;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

public class ItemBase extends Item {
	public String itemName;
	
	//settings
	public int coolDownTime = 200;
	public boolean allowCrossDimension = true;
	public boolean requireCharge = true;
	public int chargesPerPearl = 10;
	
	public int maxCharge = 10;
	public int chargesPerUse = 2;
	
	protected IIcon inactiveStone;
	protected IIcon activeStone;
	
	protected String textureName = "";
	
	public ItemBase() {
		this.setCreativeTab(RecallStones.recallTab);
		this.setMaxStackSize(1);
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
	
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int dmg) {
		return (dmg == 0)? inactiveStone : activeStone;
	}
	
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		this.inactiveStone = register.registerIcon(RecallStones.MODID+":"+textureName);
		this.activeStone = register.registerIcon(RecallStones.MODID+":"+textureName+"Active");
	}
	
	//set the texture name for the stone
	protected void setTexture(String tex) {
		this.textureName = tex;
	}
	
	
	//deal with the cool down time
	public void onUpdate(ItemStack itemStack, World world, Entity entity, int par4, boolean par5) {
		if (!world.isRemote) {
			int coolDown = this.getCoolDown(itemStack);
			if (coolDown>0) {
				itemStack.stackTagCompound.setInteger("coolDown", coolDown - 1);
			} else if (coolDown<0) {
				itemStack.stackTagCompound.setInteger("coolDown", 0);
			}
		}
	}
	
	//get the current cool down level
	protected int getCoolDown(ItemStack itemStack) {
		if ( (itemStack.stackTagCompound != null) && (itemStack.stackTagCompound.hasKey("coolDown")) ) {
			return itemStack.stackTagCompound.getInteger("coolDown");
		}
		return 0;
	}
	
	//start the cool down timer (Set it to what ever the value is meant to be
	protected void startCoolDown(ItemStack itemStack) {
		if (itemStack.stackTagCompound != null) {
			itemStack.stackTagCompound.setInteger("coolDown", this.coolDownTime);
		}
	}
	
	//get the marked location in a nice string format
	protected String getLocationString(ItemStack itemStack) {
		String output = "";
		if (itemStack.stackTagCompound != null) {
			if (itemStack.stackTagCompound.hasKey("markX")) {
				output += (int)itemStack.stackTagCompound.getDouble("markX");
				output += ", "+(int)itemStack.stackTagCompound.getDouble("markY");
				output += ", "+(int)itemStack.stackTagCompound.getDouble("markZ");
			}
			
		}
		return output;
	}
	
	//getters for the 4 location fields
	public int getDimension(ItemStack itemStack) {
		if (itemStack.stackTagCompound != null) {
			return itemStack.stackTagCompound.getInteger("world");
		}
		return 0;
	}
	
	public double getLocX(ItemStack itemStack) {
		if (itemStack.stackTagCompound != null) {
			return itemStack.stackTagCompound.getDouble("markX");
		}
		return 0.0;
	}
	
	public double getLocY(ItemStack itemStack) {
		if (itemStack.stackTagCompound != null) {
			return itemStack.stackTagCompound.getDouble("markY");
		}
		return 0.0;
	}
	
	public double getLocZ(ItemStack itemStack) {
		if (itemStack.stackTagCompound != null) {
			return itemStack.stackTagCompound.getDouble("markZ");
		}
		return 0.0;
	}
	
	//initilise the NBT stuff
	public void init(ItemStack itemStack) {
		if (itemStack.stackTagCompound == null) {
			itemStack.stackTagCompound = new NBTTagCompound();
			if (this.requireCharge) {
				itemStack.stackTagCompound.setInteger("currentCharge", this.maxCharge);
			}
		}
	}
	
	
	//set the location that this stone will recall to
	public boolean setLocation(ItemStack itemStack, int dimension, double x, double y, double z) {
		this.init(itemStack);
		if (itemStack.stackTagCompound != null) {
			itemStack.stackTagCompound.setInteger("world", dimension);
			itemStack.stackTagCompound.setDouble("markX", Math.floor(x));
			itemStack.stackTagCompound.setDouble("markY", Math.floor(y));
			itemStack.stackTagCompound.setDouble("markZ", Math.floor(z));
			return true;
		}
		return false;
	}
	
	//do the actual teleport
	public boolean moveLocation(ItemStack itemStack, EntityPlayer player, World world) {
		int coolDown = this.getCoolDown(itemStack);
		if ( (itemStack.stackTagCompound != null) && (coolDown==0) ) {
			int targetDimension = itemStack.stackTagCompound.getInteger("world");
			if ( (player.dimension == targetDimension) || (allowCrossDimension) ) {
				
				if (this.checkTarget(
						itemStack.stackTagCompound.getInteger("world"),
						(int)itemStack.stackTagCompound.getDouble("markX"),
						(int)itemStack.stackTagCompound.getDouble("markY"),
						(int)itemStack.stackTagCompound.getDouble("markZ")
				)) {
					
					if (this.consumeCharge(itemStack)) {
						this.animateTP(player);
						
						if (player.dimension != targetDimension) {
							player.travelToDimension(targetDimension);
						}
							
						if (player.isRiding()){
							player.mountEntity((Entity)null);
	                    }
						player.setPositionAndUpdate(
								itemStack.stackTagCompound.getDouble("markX") + 0.5,
								itemStack.stackTagCompound.getDouble("markY"), 
								itemStack.stackTagCompound.getDouble("markZ") + 0.5
								);
	
						this.animateTP(player);
						
						this.startCoolDown(itemStack);
						return true;
					} else {
						this.tellPlayer(player, EnumChatFormatting.RED + "Stone does not have enough charges left");
						this.tellPlayer(player, "Craft with an Ender Pearl to recharge");
					}
				} else {
					this.tellPlayer(player, EnumChatFormatting.RED + "Something is blocking the destination");
				}
			} else {
				this.tellPlayer(player, EnumChatFormatting.RED + "You can not cross dimensions with this stone");
			}
		} else {
			this.tellPlayer(player, "Stone is still recharging");
		}
		return false;
	}
	
	//consume a charge on the stone, returns true is successful
	public boolean consumeCharge(ItemStack itemStack) {
		if (this.requireCharge) {
			if (itemStack.stackTagCompound == null) return false;
			
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
			if (itemStack.stackTagCompound != null) {
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
			if (itemStack.stackTagCompound == null) return false;
			int currentCharge = ( this.getCharge(itemStack) + (this.chargesPerPearl * (number-1)));
			if (currentCharge >= this.maxCharge) return false;
			return true;
		} else {
			return false;
		}
	}
	
	public void setCharge(ItemStack itemStack, int charge) {
		if (itemStack.stackTagCompound == null) {
			itemStack.stackTagCompound = new NBTTagCompound();
		}
		if (itemStack.stackTagCompound != null) {
			itemStack.stackTagCompound.setInteger("currentCharge", charge);
		}
	}
	
	public int getCharge(ItemStack itemStack) {
		int charge = this.maxCharge;
		if (itemStack.stackTagCompound != null) {
			if (itemStack.stackTagCompound.hasKey("currentCharge")) {
				charge = itemStack.stackTagCompound.getInteger("currentCharge");
			}
		}
		return charge;
	}
	
	
	//check the target location and see if it's clear to tp to
	public boolean checkTarget(int targetDimension, int targetX, int targetY, int targetZ) {
		
		World world = DimensionManager.getWorld(targetDimension);
		Block target1 = world.getBlock(targetX, targetY, targetZ);
		Block target2 = world.getBlock(targetX, targetY + 1, targetZ);

		if ( (target1.isReplaceable(world, targetX, targetY, targetZ)) && (target2.isReplaceable(world, targetX, targetY + 1, targetZ)) ) {
			return true;
		}
		
		return false;
	}
	
	protected void animateTP(EntityPlayer player) {
		player.worldObj.playSoundAtEntity(player, RecallStones.MODID+":warp", 0.8f, 1.0f);
		RecallStones.network.sendToAll(new SendParticles(player));
	}
	
	protected void tellPlayer(EntityPlayer player, String message) {
		player.addChatMessage(new ChatComponentText(message));
	}
	
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean par4) {
		int isActive = itemStack.getItemDamage();
		
		if (isActive==0) {
			list.add(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + "Unmarked");
			list.add(EnumChatFormatting.GRAY + "Sneak right click with item");
			list.add(EnumChatFormatting.GRAY + "to mark current location");
		} else {
			list.add(EnumChatFormatting.GRAY + "Marked at location: "+getLocationString(itemStack));
			list.add(EnumChatFormatting.GRAY + "Marked Dimension: "+itemStack.stackTagCompound.getInteger("world"));
			addCharge(itemStack, list);
		}
		
	}
	
	public void addCharge(ItemStack itemStack, List list) {
		if (itemStack.stackTagCompound != null) {
			if (this.requireCharge) {
				int currentCharge = itemStack.stackTagCompound.getInteger("currentCharge");
				list.add(EnumChatFormatting.DARK_GREEN  + "Current Charges: "+currentCharge+"/"+this.maxCharge+" ("+this.chargesPerUse+" per use)");
				if (currentCharge == 0) {
					list.add(EnumChatFormatting.RED  + "Craft with an Ender Pearl to recharge");
				}
			}

			if (itemStack.stackTagCompound.hasKey("coolDown")) {
				int coolDown = itemStack.stackTagCompound.getInteger("coolDown");
				if (coolDown>0) {
					list.add(EnumChatFormatting.RED  + "Recharging. Time left: "+(int)Math.ceil(coolDown/20));
				}
			}
		}
	}
	
}
