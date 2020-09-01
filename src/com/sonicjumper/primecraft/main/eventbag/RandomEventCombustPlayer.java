package com.sonicjumper.primecraft.main.eventbag;

import java.util.Random;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RandomEventCombustPlayer extends RandomEvent {
	private Random rand = new Random();
	
	public RandomEventCombustPlayer(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		pp.getPlayer().setFireTicks(rand.nextInt(100) + 100);
		broadcastEventMessage(pp.getFriendlyName(), " spontaneously combusted!");
	}
}
