package com.sonicjumper.primecraft.main.shop;

import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ShopItemRegenerationPotion extends ShopItemPositivePotion {
	public ShopItemRegenerationPotion(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		super(itemId, itemName, displayName, path, type, itemMat, multiBuy, itemCost, itemTier);
	}
	
	@Override
	public boolean setupSpecialRequirements(PlayerProfile pp) {
		requiredTier = (pp.getMaxPositivePotionCount(this) * 5) + 4;
		cost = (pp.getMaxPositivePotionCount(this) + 1) * 1500;
		failedReqMsg = "You need to be tier " + requiredTier + " before buying this item.";
		return pp.getTierLevel() >= requiredTier;
	}

	@Override
	public PotionEffect[] getPotionEffectsForTier(int tier) {
		return new PotionEffect[]{
				new PotionEffect(PotionEffectType.REGENERATION, (int) ((((tier * 5) + 2) * 20) / (Math.floor(tier / 10.0D) + 1)), (int) (Math.floor(tier / 10.0D))),
				new PotionEffect(PotionEffectType.HEALTH_BOOST, ((tier * 7) + 2) * 20, (int) (Math.floor(tier / 4.0D) + 1))
				};
	}
	
	@Override
	public PotionType getDisplayEffect() {
		return PotionType.REGEN;
	}
}
