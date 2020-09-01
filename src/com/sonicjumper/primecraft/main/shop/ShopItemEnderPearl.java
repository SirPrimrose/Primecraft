package com.sonicjumper.primecraft.main.shop;

import org.bukkit.Material;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ShopItemEnderPearl extends ShopItemTactical {
	public ShopItemEnderPearl(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		super(itemId, itemName, displayName, path, type, itemMat, multiBuy, itemCost, itemTier);
	}
	
	@Override
	public boolean setupSpecialRequirements(PlayerProfile pp) {
		requiredTier = (pp.getMaxTacticalCount(this) * 2) + 1;
		cost = (pp.getMaxTacticalCount(this) + 1) * 500;
		failedReqMsg = "You need to be tier " + requiredTier + " before buying this item.";
		return pp.getTierLevel() >= requiredTier;
	}
}
