package com.sonicjumper.primecraft.main.shop;

import org.bukkit.Material;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public abstract class ShopItemNegativePotion extends ShopItemPotion {
	public ShopItemNegativePotion(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		super(itemId, itemName, displayName, path, type, itemMat, multiBuy, itemCost, itemTier);
	}
	
	@Override
	public void onItemBought(PlayerProfile playerProfile) {
		super.onItemBought(playerProfile);
		
		playerProfile.incrementMaxNegativePotionCount(this);
	}
}
