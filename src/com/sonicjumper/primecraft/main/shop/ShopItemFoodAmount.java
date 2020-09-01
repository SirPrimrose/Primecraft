package com.sonicjumper.primecraft.main.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ShopItemFoodAmount extends ShopItem {
	public ShopItemFoodAmount(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		super(itemId, itemName, displayName, path, type, itemMat, multiBuy, itemCost, itemTier);
	}
	
	@Override
	public void onItemBought(PlayerProfile playerProfile) {
		playerProfile.setFoodAmount(playerProfile.getFoodAmount() + 5);
		if(playerProfile.getFoodAmount() > 64) {
			playerProfile.setFoodAmount(64);
			playerProfile.getPlayer().sendMessage(ChatColor.GRAY + "You have reached the maximum amount of food.");
		}
	}
}
