package com.sonicjumper.primecraft.main.eventbag;

import java.util.Random;

import org.bukkit.entity.Arrow;
import org.bukkit.util.Vector;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RandomEventArrowStorm extends RandomEvent {
	Random rand = new Random();
	
	public RandomEventArrowStorm(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		int shots = 100 + rand.nextInt(50);
		double increment = (Math.PI * 2.0D) / shots;
		for(int i = 0; i < shots; i++) {
			double x = Math.cos(i * increment);
			double z = Math.sin(i * increment);
			Arrow arrow = pp.getPlayer().launchProjectile(Arrow.class, new Vector(x * 0.25D, rand.nextDouble() * 1.0D + 2.0D, z * 0.25D));
			arrow.setCritical(true);
			instaKillArrows.add(arrow);
		}
		broadcastEventMessage(pp.getFriendlyName(), " launched an arrow storm.");
	}
}
