package com.sonicjumper.primecraft.main.eventbag;

import java.util.ArrayList;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RandomEventSeniorCitizen extends RandomEvent {
	ArrayList<PotionEffect> plagueEffects;
	
	public RandomEventSeniorCitizen(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
		
		plagueEffects = new ArrayList<PotionEffect>();
		plagueEffects.add(new PotionEffect(PotionEffectType.BLINDNESS, 600, 1));
		plagueEffects.add(new PotionEffect(PotionEffectType.SLOW, 600, 1));
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		pp.getPlayer().addPotionEffects(plagueEffects);
		broadcastEventMessage(pp.getFriendlyName(), " became a senior citizen.");
	}
}
