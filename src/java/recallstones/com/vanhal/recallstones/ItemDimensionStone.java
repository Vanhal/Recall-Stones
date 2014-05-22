package com.vanhal.recallstones;

import net.minecraft.item.Item;

public class ItemDimensionStone extends ItemRecallStone {

	public ItemDimensionStone() {
		super();
		this.setName("dimensionStone");
		this.setTexture("dimensionStone");
		this.allowCrossDimension = true;
		this.maxCharge = 25;
		this.chargesPerUse = 5;
	}
}
