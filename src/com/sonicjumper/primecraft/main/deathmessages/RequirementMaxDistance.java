package com.sonicjumper.primecraft.main.deathmessages;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RequirementMaxDistance extends DeathRequirement {
private int maximumDistance;
	
	public RequirementMaxDistance(int distance) {
		maximumDistance = distance;
	}

	@Override
	public boolean meetsRequirement(PlayerProfile playerKilled, PlayerProfile killer) {
		return killer.getPlayer().getLocation().distance(playerKilled.getPlayer().getLocation()) < maximumDistance;
	}
}
