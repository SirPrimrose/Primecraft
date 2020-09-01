package com.sonicjumper.primecraft.main.eventbag;

import java.util.Random;

import org.bukkit.entity.Item;
import org.bukkit.util.Vector;

import com.sonicjumper.primecraft.main.player.MoneyItem;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public class RandomEventDropCoins extends RandomEvent {
	private Random rand = new Random();
	
	public RandomEventDropCoins(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		int drops = rand.nextInt(10) + 5;
		for(int i = 0; i < drops; i++) {
			Item item = pp.getPlayer().getWorld().dropItem(pp.getPlayer().getLocation(), PrimeUtility.getNonStackingItemStack(MoneyItem.getRandomMoneyItem().getMaterial()));
			item.setPickupDelay(40);
			item.setVelocity(new Vector(0.125D - (rand.nextDouble() * 0.25D), rand.nextDouble() * 1.0D + 0.5D, 0.125D - (rand.nextDouble() * 0.25D)));
		}
		broadcastEventMessage(pp.getFriendlyName(), " has found a smattering of coins!");
	}
}
