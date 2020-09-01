package com.sonicjumper.primecraft.main.deathmessages;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RequirementMinDistance extends DeathRequirement {
	private int minimumDistance;
	
	public RequirementMinDistance(int distance) {
		minimumDistance = distance;
	}

	@Override
	public boolean meetsRequirement(PlayerProfile playerKilled, PlayerProfile killer) {
		return killer.getPlayer().getLocation().distance(playerKilled.getPlayer().getLocation()) > minimumDistance;
	}
}
