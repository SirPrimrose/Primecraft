package com.sonicjumper.primecraft.main.deathmessages;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RequirementPlayerDisadvantage extends DeathRequirement {
	private int requiredDisadvantage;
	
	/**
	 * Fires when the killer has at least the given disadvantage over the killed player.
	 * @param disadvantage Amount of disadvantage a player needs to have to meet the requirement.
	 */
	public RequirementPlayerDisadvantage(int disadvantage) {
		requiredDisadvantage = disadvantage;
	}

	@Override
	public boolean meetsRequirement(PlayerProfile playerKilled, PlayerProfile killer) {
		if(killer != null) {
			if(killer.getLoadoutRating() - playerKilled.getLoadoutRating() < -1 * requiredDisadvantage) {
				return true;
			}
		}
		return false;
	}
}
