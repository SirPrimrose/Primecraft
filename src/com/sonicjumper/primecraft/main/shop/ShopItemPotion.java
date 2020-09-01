package com.sonicjumper.primecraft.main.shop;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

public abstract class ShopItemPotion extends ShopItem {
	public ShopItemPotion(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		super(itemId, itemName, displayName, path, type, itemMat, multiBuy, itemCost, itemTier);
	}
	
	public abstract PotionEffect[] getPotionEffectsForTier(int tier);

	public abstract PotionType getDisplayEffect();
}
