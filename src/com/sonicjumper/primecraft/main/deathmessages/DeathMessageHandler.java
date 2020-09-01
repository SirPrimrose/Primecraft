package com.sonicjumper.primecraft.main.deathmessages;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.sonicjumper.primecraft.main.deathmessages.DeathMessage.DeathType;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;

public class DeathMessageHandler {
	public static DeathMessage[] allMessages = new DeathMessage[128];
	
	//PVP Messages
	public static DeathMessage meleePvpKill1 = new DeathMessage("eliminated", DamageCause.ENTITY_ATTACK, DeathType.PvP, null);
	public static DeathMessage meleePvpKill2 = new DeathMessage("annihilated", DamageCause.ENTITY_ATTACK, DeathType.PvP, null);
	//public static DeathMessage meleePvpKill3 = new DeathMessage("dun kilt %p wit der sward", DamageCause.ENTITY_ATTACK, DeathType.PvP, null);
	//public static DeathMessage meleePvpKill4 = new DeathMessage("iced", DamageCause.ENTITY_ATTACK, DeathType.PvP, null);
	public static DeathMessage meleePvpKill5 = new DeathMessage("dispatched", DamageCause.ENTITY_ATTACK, DeathType.PvP, null);
	//public static DeathMessage projectilePvpKill1 = new DeathMessage("dun pricked %p with their arrow again", DamageCause.PROJECTILE, DeathType.PvP, null);
	//public static DeathMessage projectilePvpKill2 = new DeathMessage("tickled %p with the tip of his arrow", DamageCause.PROJECTILE, DeathType.PvP, null);
	public static DeathMessage projectilePvpKill3 = new DeathMessage("scored a bullseye on", DamageCause.PROJECTILE, DeathType.PvP, null);
	public static DeathMessage fallPvpKill1 = new DeathMessage("assisted %p in their suicide", DamageCause.FALL, DeathType.PvP, null);
	public static DeathMessage fallPvpKill2 = new DeathMessage("pushed %p off a ledge", DamageCause.FALL, DeathType.PvP, null);
	public static DeathMessage fallPvpKill3 = new DeathMessage("tripped", DamageCause.FALL, DeathType.PvP, null);
	public static DeathMessage blockExplosionPvpKill1 = new DeathMessage("blew %p to smithereenes!", DamageCause.ENTITY_EXPLOSION, DeathType.PvP, null);
	public static DeathMessage blockExplosionPvpKill2 = new DeathMessage("exploded %p!", DamageCause.ENTITY_EXPLOSION, DeathType.PvP, null);
	public static DeathMessage blockExplosionPvpKill3 = new DeathMessage("tested the dynamite on", DamageCause.ENTITY_EXPLOSION, DeathType.PvP, null);
	public static DeathMessage lightningPvpKill1 = new DeathMessage("shocked %p to death", DamageCause.LIGHTNING, DeathType.PvP, null);
	public static DeathMessage lightningPvpKill2 = new DeathMessage("called the wrath of Thor down on", DamageCause.LIGHTNING, DeathType.PvP, null);
	public static DeathMessage lightningPvpKill3 = new DeathMessage("smited", DamageCause.LIGHTNING, DeathType.PvP, null);
	public static DeathMessage lightningPvpKill4 = new DeathMessage("zapped %p with invisible magic", DamageCause.LIGHTNING, DeathType.PvP, null);
	public static DeathMessage lavaPvpKill1 = new DeathMessage("sacrificed %p to the flames", DamageCause.LAVA, DeathType.PvP, null);
	public static DeathMessage lavaPvpKill2 = new DeathMessage("boiled", DamageCause.LAVA, DeathType.PvP, null);
	public static DeathMessage firePvpKill1 = new DeathMessage("burned", DamageCause.FIRE, DeathType.PvP, null);
	public static DeathMessage fireTickPvpKill1 = new DeathMessage("turned %p to ashes", DamageCause.FIRE_TICK, DeathType.PvP, null);
	public static DeathMessage voidPvpKill1 = new DeathMessage("knocked %p into the void", DamageCause.VOID, DeathType.PvP, null);
	public static DeathMessage voidPvpKill2 = new DeathMessage("pushed %p into deep space", DamageCause.VOID, DeathType.PvP, null);
	public static DeathMessage voidPvpKill3 = new DeathMessage("threw %p into the darkness of the world", DamageCause.VOID, DeathType.PvP, null);
	public static DeathMessage voidPvpKill4 = new DeathMessage("removed %p from this earth", DamageCause.VOID, DeathType.PvP, null);
	public static DeathMessage cactusPvpKill1 = new DeathMessage("shoved %p onto the spikes", DamageCause.CONTACT, DeathType.PvP, null);
	public static DeathMessage thornsPvpKill1 = new DeathMessage("spiked %p with their armor", DamageCause.THORNS, DeathType.PvP, null);
	public static DeathMessage potionPvpKill1 = new DeathMessage("splashed death on", DamageCause.MAGIC, DeathType.PvP, null);
	
	//Natural Messages
	public static DeathMessage fallNaturalKill1 = new DeathMessage("thought he was Superman", DamageCause.FALL, DeathType.Natural, null);
	public static DeathMessage fallNaturalKill2 = new DeathMessage("beleived he could fly", DamageCause.FALL, DeathType.Natural, null);
	public static DeathMessage fallNaturalKill3 = new DeathMessage("*Falls from the sky*", DamageCause.FALL, DeathType.Natural, null);
	public static DeathMessage fallNaturalKill4 = new DeathMessage("fell from a relatively low place", DamageCause.FALL, DeathType.Natural, null);
	public static DeathMessage fallNaturalKill5 = new DeathMessage("has fallen and can't get up!", DamageCause.FALL, DeathType.Natural, null);
	public static DeathMessage drownNaturalKill1 = new DeathMessage("decided he would try swimming...fail!", DamageCause.DROWNING, DeathType.Natural, null);
	public static DeathMessage drownNaturalKill2 = new DeathMessage("held their breath for too long", DamageCause.DROWNING, DeathType.Natural, null);
	public static DeathMessage blockExplosionNaturalKill1 = new DeathMessage("BOOM...%p's gone!", DamageCause.BLOCK_EXPLOSION, DeathType.Natural, null);
	public static DeathMessage blockExplosionNaturalKill2 = new DeathMessage("went to meet their maker", DamageCause.BLOCK_EXPLOSION, DeathType.Natural, null);
	public static DeathMessage blockExplosionNaturalKill3 = new DeathMessage("was blown away", DamageCause.BLOCK_EXPLOSION, DeathType.Natural, null);
	//public static DeathMessage lavaNaturalKill1 = new DeathMessage("turned the water in the bath too hot", DamageCause.LAVA, DeathType.Natural, null);
	public static DeathMessage lavaNaturalKill2 = new DeathMessage("didn't enjoy their hot rock massage", DamageCause.LAVA, DeathType.Natural, null);
	//public static DeathMessage lavaNaturalKill3 = new DeathMessage("spilt their coffee on themself", DamageCause.LAVA, DeathType.Natural, null);
	public static DeathMessage lavaNaturalKill4 = new DeathMessage("waited to long for the water to heat up", DamageCause.LAVA, DeathType.Natural, null);
	public static DeathMessage voidNaturalKill1 = new DeathMessage("fell into darkness", DamageCause.VOID, DeathType.Natural, null);
	public static DeathMessage voidNaturalKill2 = new DeathMessage("realized the void in their life", DamageCause.VOID, DeathType.Natural, null);
	public static DeathMessage fireNaturalKill1 = new DeathMessage("fo sizzle", DamageCause.FIRE, DeathType.Natural, null);
	public static DeathMessage fireNaturalKill2 = new DeathMessage("got grilled", DamageCause.FIRE_TICK, DeathType.Natural, null);
	public static DeathMessage cactusNaturalKill1 = new DeathMessage("was the idiot we had talked about", DamageCause.CONTACT, DeathType.Natural, null);
	public static DeathMessage cactusNaturalKill2 = new DeathMessage("died by cactus... now laugh at them", DamageCause.CONTACT, DeathType.Natural, null);
	
	//Map Requirement Messages
	public static DeathMessage aviatorVoidPvpKill1 = new DeathMessage("pushed %p out of the plane", DamageCause.VOID, DeathType.PvP, new DeathRequirement[]{new RequirementMap("Aviator")});
	public static DeathMessage aviatorVoidNaturalKill1 = new DeathMessage("jumped without a parachute", DamageCause.VOID, DeathType.Natural, new DeathRequirement[]{new RequirementMap("Aviator")});
	public static DeathMessage nuketownMeleePvpKill1 = new DeathMessage("nuked", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementMap("Nuketown")});
	public static DeathMessage hothEmpireMeleePvpKill1 = new DeathMessage("struck back against the Rebel", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementMap("Hoth"), new RequirementTeam(TeamDetails.Red)});
	public static DeathMessage hothRebelMeleePvpKill1 = new DeathMessage("rebelled against the Imperial scum", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementMap("Hoth"), new RequirementTeam(TeamDetails.Blue)});
	
	//Advantage Requirement Messages
	public static DeathMessage bullyMeleePvpKill1 = new DeathMessage("bullied", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementPlayerAdvantage(25)});
	public static DeathMessage stoodUpToMeleePvpKill1 = new DeathMessage("stood up to", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementPlayerDisadvantage(25)});
	
	//Weapon Requirement Messages
	public static DeathMessage goldSwordMeleePvpKill1 = new DeathMessage("buttered up", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(Material.GOLD_SWORD)});
	public static DeathMessage woodSwordMeleePvpKill1 = new DeathMessage("whacked %p with a pointy stick", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(Material.WOOD_SWORD)});
	public static DeathMessage stoneSwordMeleePvpKill1 = new DeathMessage("stoned", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(Material.STONE_SWORD)});
	public static DeathMessage ironSwordMeleePvpKill1 = new DeathMessage("fenced with %p and won", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(Material.IRON_SWORD)});
	public static DeathMessage diamondSwordMeleePvpKill1 = new DeathMessage("cleaved", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(Material.DIAMOND_SWORD)});
	public static DeathMessage cookieMeleePvpKill1 = new DeathMessage("chocolate chopped", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(Material.COOKIE)});
	public static DeathMessage melonMeleePvpKill1 = new DeathMessage("melonerated", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(Material.MELON)});
	public static DeathMessage potatoMeleePvpKill1 = new DeathMessage("mashed", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(Material.POTATO)});
	public static DeathMessage bakedPotatoMeleePvpKill1 = new DeathMessage("baked %p like their potato", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(Material.BAKED_POTATO)});
	public static DeathMessage fistMeleePvpKill1 = new DeathMessage("knocked out", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(null)});
	public static DeathMessage fishMeleePvpKill1 = new DeathMessage("slapped %p with a fish", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementWeapon(Material.RAW_FISH)});
	
	//Distance Requirement Messages
	public static DeathMessage minDistanceProjectilePvpKill1 = new DeathMessage("sniped", DamageCause.PROJECTILE, DeathType.PvP, new DeathRequirement[]{new RequirementMinDistance(30)});
	public static DeathMessage minDistanceProjectilePvpKill2 = new DeathMessage("ghost scoped", DamageCause.PROJECTILE, DeathType.PvP, new DeathRequirement[]{new RequirementMinDistance(45)});
	public static DeathMessage maxDistanceProjectilePvpKill1 = new DeathMessage("got up close and personal with", DamageCause.PROJECTILE, DeathType.PvP, new DeathRequirement[]{new RequirementMaxDistance(3)});
	public static DeathMessage maxDistanceProjectilePvpKill2 = new DeathMessage("quickscoped", DamageCause.PROJECTILE, DeathType.PvP, new DeathRequirement[]{new RequirementMaxDistance(6)});
	
	//Assist Requirement Messages
	public static DeathMessage gangedUpMeleePvpKill1 = new DeathMessage("ganged up with %a to kill", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementMinimumAssisters(4)});
	public static DeathMessage sidekickMeleePvpKill2 = new DeathMessage("killed %p with their sidekick(s), %a", DamageCause.ENTITY_ATTACK, DeathType.PvP, new DeathRequirement[]{new RequirementMinimumAssisters(2)});

	//Multiple Requirement Messages
	
	public static ArrayList<DeathMessage> getEligibleDeathMessages(DamageCause deathCause, DeathType type, PlayerProfile playerKilled, PlayerProfile killer) {
		ArrayList<DeathMessage> eligibleMessages = new ArrayList<DeathMessage>();
		for(DeathMessage dm : allMessages) {
			if(dm != null) {
				if(dm.meetsDeathCause(deathCause) && dm.meetsDeathType(type) && dm.meetsSecondaryRequirements(playerKilled, killer)) {
					eligibleMessages.add(dm);
				}
			}
		}
		return eligibleMessages;
	}
}
