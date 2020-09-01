package com.sonicjumper.primecraft.main.eventbag;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public class RandomEventGiveArtemisBow extends RandomEvent {
	public RandomEventGiveArtemisBow(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		ItemStack is = PrimeUtility.getNamedStack(Material.BOW, ChatColor.GOLD + "Artemis Bow", 1);
		is.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
		is.addUnsafeEnchantment(Enchantment.KNOCKBACK, 0);
		is.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 0);
		int firstBowIndex = pp.getPlayer().getInventory().first(Material.BOW);
		if(firstBowIndex != -1) {
			pp.getPlayer().getInventory().setItem(firstBowIndex, is);
		} else {
			pp.getPlayer().getInventory().setItem(1, is);
		}
		broadcastEventMessage(pp.getFriendlyName(), " found the Bow of Artemis!");
	}
}
