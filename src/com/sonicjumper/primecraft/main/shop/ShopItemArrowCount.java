package com.sonicjumper.primecraft.main.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ShopItemArrowCount extends ShopItem {
	public ShopItemArrowCount(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		super(itemId, itemName, displayName, path, type, itemMat, multiBuy, itemCost, itemTier);
	}
	
	@Override
	public void onItemBought(PlayerProfile playerProfile) {
		playerProfile.setMaxArrowCount(this, playerProfile.getMaxArrowCount(this) + 5);
		if(playerProfile.getMaxArrowCount(this) > 100) {
			playerProfile.setMaxArrowCount(this, 100);
			playerProfile.getPlayer().sendMessage(ChatColor.GRAY + "You have reached the maximum amount of arrows.");
		}
	}
}
