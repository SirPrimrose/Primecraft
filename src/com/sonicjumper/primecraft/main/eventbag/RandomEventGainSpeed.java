package com.sonicjumper.primecraft.main.eventbag;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RandomEventGainSpeed extends RandomEvent {
	public RandomEventGainSpeed(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		pp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 0));
		broadcastEventMessage(pp.getFriendlyName(), " has gained a speed boost!");
	}
}
