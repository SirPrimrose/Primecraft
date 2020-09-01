package com.sonicjumper.primecraft.main.eventbag;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RandomEventAddWeaponEnchant extends RandomEvent {
	private Enchantment weaponEnchantment;
	private String weaponMessage;
	
	public RandomEventAddWeaponEnchant(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers, Enchantment enchant, String message) {
		super(type, rarity, minPlayers, maxPlayers);
		weaponEnchantment = enchant;
		weaponMessage = message;
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		ItemStack sword = pp.getPlayer().getInventory().getItem(0);
		if(sword != null) {
			sword.addUnsafeEnchantment(weaponEnchantment, 1);
		}
		/*ItemStack bow = pp.getPlayer().getInventory().getItem(1);
		if(bow != null) {
			bow.addUnsafeEnchantment(weaponEnchantment, 1);
		}*/
		broadcastEventMessage(pp.getFriendlyName(), weaponMessage);
	}

}
