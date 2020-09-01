package com.sonicjumper.primecraft.main.shop;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ShopItemHarmingPotion extends ShopItemNegativePotion {
	public ShopItemHarmingPotion(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		super(itemId, itemName, displayName, path, type, itemMat, multiBuy, itemCost, itemTier);
	}
	
	@Override
	public boolean setupSpecialRequirements(PlayerProfile pp) {
		requiredTier = (int) ((pp.getMaxNegativePotionCount(this) * 4.5D) + 3);
		cost = (pp.getMaxNegativePotionCount(this) + 1) * 1000;
		failedReqMsg = "You need to be tier " + requiredTier + " before buying this item.";
		return pp.getTierLevel() >= requiredTier;
	}

	@Override
	public PotionEffect[] getPotionEffectsForTier(int tier) {
		return new PotionEffect[]{new PotionEffect(PotionEffectType.HARM, 1, (int) (Math.floor(tier / 8.0D)))};
	}
	
	@Override
	public PotionType getDisplayEffect() {
		return PotionType.INSTANT_DAMAGE;
	}
}
