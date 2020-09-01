package com.sonicjumper.primecraft.main.eventbag;

import java.util.ArrayList;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RandomEventPlague extends RandomEvent {
	ArrayList<PotionEffect> plagueEffects;
	
	public RandomEventPlague(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
		
		plagueEffects = new ArrayList<PotionEffect>();
		plagueEffects.add(new PotionEffect(PotionEffectType.CONFUSION, 400, 1));
		plagueEffects.add(new PotionEffect(PotionEffectType.POISON, 400, 1));
		plagueEffects.add(new PotionEffect(PotionEffectType.BLINDNESS, 400, 1));
		plagueEffects.add(new PotionEffect(PotionEffectType.SLOW, 400, 1));
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		pp.getPlayer().addPotionEffects(plagueEffects);
		broadcastEventMessage(pp.getFriendlyName(), " was struck by the plague.");
	}
}
