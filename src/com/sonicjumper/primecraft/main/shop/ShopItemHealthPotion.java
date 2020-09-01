package com.sonicjumper.primecraft.main.shop;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ShopItemHealthPotion extends ShopItemPositivePotion {
	public ShopItemHealthPotion(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		super(itemId, itemName, displayName, path, type, itemMat, multiBuy, itemCost, itemTier);
	}
	
	@Override
	public boolean setupSpecialRequirements(PlayerProfile pp) {
		requiredTier = (pp.getMaxPositivePotionCount(this) * 4) + 3;
		cost = (pp.getMaxPositivePotionCount(this) + 1) * 500;
		failedReqMsg = "You need to be tier " + requiredTier + " before buying this item.";
		return pp.getTierLevel() >= requiredTier;
	}

	@Override
	public PotionEffect[] getPotionEffectsForTier(int tier) {
		return new PotionEffect[]{new PotionEffect(PotionEffectType.HEAL, 1, (int) (Math.floor(tier / 4.0D)))};
	}
	
	@Override
	public PotionType getDisplayEffect() {
		return PotionType.INSTANT_HEAL;
	}
}
