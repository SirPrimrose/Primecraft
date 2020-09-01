package com.sonicjumper.primecraft.main.eventbag;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RandomEventHealPlayer extends RandomEvent {
	public RandomEventHealPlayer(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		pp.getPlayer().setHealth(20.0D);
		broadcastEventMessage(pp.getFriendlyName(), " was fully healed.");
	}
}
