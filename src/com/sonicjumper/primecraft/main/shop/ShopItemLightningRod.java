package com.sonicjumper.primecraft.main.shop;

import org.bukkit.Material;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ShopItemLightningRod extends ShopItemOffensive {
	public ShopItemLightningRod(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		super(itemId, itemName, displayName, path, type, itemMat, multiBuy, itemCost, itemTier);
	}
	
	@Override
	public boolean setupSpecialRequirements(PlayerProfile pp) {
		requiredTier = (int) ((pp.getMaxOffensiveCount(this) * 6.5D) + 5);
		cost = (pp.getMaxOffensiveCount(this) + 1) * 1500;
		failedReqMsg = "You need to be tier " + requiredTier + " before buying this item.";
		return pp.getTierLevel() >= requiredTier;
	}
}
