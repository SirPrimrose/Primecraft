package com.sonicjumper.primecraft.main.shop;

import org.bukkit.Material;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ShopItemKnockbackArrow extends ShopItem {
	public ShopItemKnockbackArrow(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		super(itemId, itemName, displayName, path, type, itemMat, multiBuy, itemCost, itemTier);
	}
	
	@Override
	public boolean setupSpecialRequirements(PlayerProfile pp) {
		requiredTier = (int) (((pp.getMaxArrowCount(this) / 3.0D) * 2.5D) + 3);
		cost = ((pp.getMaxArrowCount(this) / 3) + 1) * 300 + 200;
		failedReqMsg = "You need to be tier " + requiredTier + " before buying this item.";
		return pp.getTierLevel() >= requiredTier;
	}
	
	@Override
	public void onItemBought(PlayerProfile playerProfile) {
		super.onItemBought(playerProfile);
		
		playerProfile.incrementMaxArrowCount(this);
		playerProfile.incrementMaxArrowCount(this);
		playerProfile.incrementMaxArrowCount(this);
	}
}
