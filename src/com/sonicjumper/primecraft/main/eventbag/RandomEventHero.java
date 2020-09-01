package com.sonicjumper.primecraft.main.eventbag;

import java.util.ArrayList;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RandomEventHero extends RandomEvent {
	ArrayList<PotionEffect> plagueEffects;
	
	public RandomEventHero(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
		
		plagueEffects = new ArrayList<PotionEffect>();
		plagueEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 400, 1));
		plagueEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, 400, 1));
		plagueEffects.add(new PotionEffect(PotionEffectType.ABSORPTION, 400, 3));
		plagueEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 400, 2));
		plagueEffects.add(new PotionEffect(PotionEffectType.SPEED, 400, 2));
		plagueEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 400, 2));
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		pp.getPlayer().addPotionEffects(plagueEffects);
		broadcastEventMessage(pp.getFriendlyName(), " became a hero!");
	}
}
