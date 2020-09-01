package com.sonicjumper.primecraft.main.deathmessages;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class DeathMessage {
	public enum DeathType {
		PvP,
		Natural;
	}
	private String message;
	
	private DamageCause requiredCause;
	private DeathType deathType;
	private DeathRequirement[] secondaryRequirements;
	
	private static int indexCode = 0;
	
	/**
	 * Constructor for a death message
	 * @param index
	 * @param message Message to be returned. Can use %p in place of killed player's name when type == PvP
	 * @param cause Required death cause to fire message.
	 * @param suicide Type of player death.
	 * @param secondaryRequirement Second requirement to check. May be null
	 */
	public DeathMessage(String msg, DamageCause cause, DeathType playerDeath, DeathRequirement[] requirement) {
		message = msg;
		requiredCause = cause;
		deathType = playerDeath;
		secondaryRequirements = requirement;
		
		DeathMessageHandler.allMessages[indexCode++] = this;
	}
	
	public boolean hasSecondaryRequirement() {
		return secondaryRequirements != null;
	}

	public boolean meetsDeathCause(DamageCause deathCause) {
		return deathCause.equals(requiredCause);
	}

	public boolean meetsDeathType(DeathType type) {
		return type.equals(deathType);
	}

	public boolean meetsSecondaryRequirements(PlayerProfile playerKilled, PlayerProfile killer) {
		if(hasSecondaryRequirement()) {
			for(DeathRequirement dr : secondaryRequirements) {
				if(!dr.meetsRequirement(playerKilled, killer)) {
					return false;
				}
			}
		}
		return true;
	}

	public String getMessage() {
		return message;
	}
}
