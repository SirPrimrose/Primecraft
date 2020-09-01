package com.sonicjumper.primecraft.main.eventbag;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RandomEventLightningStorm extends RandomEvent {
	public RandomEventLightningStorm(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		pp.getPlayer().getWorld().strikeLightning(pp.getPlayer().getLocation());
		broadcastEventMessage(pp.getFriendlyName(), " was struck by the lightning storm!");
	}

}
