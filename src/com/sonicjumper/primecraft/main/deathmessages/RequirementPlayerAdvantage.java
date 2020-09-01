package com.sonicjumper.primecraft.main.deathmessages;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RequirementPlayerAdvantage extends DeathRequirement {
	private int requiredAdvantage;
	
	/**
	 * Fires when the killer has at least the given advantage over the killed player.
	 * @param advantage Amount of advantage a player needs to have to meet the requirement. Can be negative for opposite effect(disadvantage)
	 */
	public RequirementPlayerAdvantage(int advantage) {
		requiredAdvantage = advantage;
	}

	@Override
	public boolean meetsRequirement(PlayerProfile playerKilled, PlayerProfile killer) {
		if(killer != null) {
			if(killer.getLoadoutRating() - playerKilled.getLoadoutRating() > requiredAdvantage) {
				return true;
			}
		}
		return false;
	}
}
