package com.sonicjumper.primecraft.main.eventbag;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public class RandomEventHordeAttack extends RandomEvent {
	private Random rand = new Random();
	
	private ArrayList<EntityType> entityTypes;
	
	public RandomEventHordeAttack(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
		
		entityTypes = new ArrayList<EntityType>();
		entityTypes.add(EntityType.ZOMBIE);
		entityTypes.add(EntityType.SKELETON);
		entityTypes.add(EntityType.PIG_ZOMBIE);
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		int numOfMobs = rand.nextInt(3) + 2;
		for(int i = 0; i < numOfMobs; i++) {
			LivingEntity entity = (LivingEntity) pp.getPlayer().getWorld().spawnEntity(PrimeUtility.getRandomNearbyBlock(pp.getPlayer().getLocation(), Material.AIR, 5, true).getLocation(), entityTypes.get(rand.nextInt(entityTypes.size())));
			entity.setCustomNameVisible(true);
			entity.setCustomName(ChatColor.DARK_GRAY + "Horde Warrior");
			entity.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10000, 2, true, false));
			entity.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10000, 1, true, false));
			entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000, 0, true, false));
		}
		broadcastEventMessage(pp.getFriendlyName(), " was attacked by the Mob Horde.");
	}
}
