package com.sonicjumper.primecraft.main.eventbag;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RandomEventAddArmorEnchant extends RandomEvent {
	private Enchantment armorEnchantment;
	private String armorMessage;
	
	public RandomEventAddArmorEnchant(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers, Enchantment enchant, String message) {
		super(type, rarity, minPlayers, maxPlayers);
		armorEnchantment = enchant;
		armorMessage = message;
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		for(ItemStack armor : pp.getPlayer().getInventory().getArmorContents()) {
			if(armor != null) {
				armor.addUnsafeEnchantment(armorEnchantment, 1);
			}
		}
		broadcastEventMessage(pp.getFriendlyName(), armorMessage);
	}
}
