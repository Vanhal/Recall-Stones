package com.vanhal.recallstones.items;

import net.minecraft.item.Item;

public class ItemDimensionStone extends ItemRecallStone {

	public ItemDimensionStone() {
		super();
		this.setName("dimensionStone");
		this.allowCrossDimension = true;
		this.maxCharge = 25;
		this.chargesPerUse = 5;
	}
}
