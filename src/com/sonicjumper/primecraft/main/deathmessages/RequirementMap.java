package com.sonicjumper.primecraft.main.deathmessages;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RequirementMap extends DeathRequirement {
	private String requiredMapName;
	
	public RequirementMap(String mapName) {
		requiredMapName = mapName;
	}
	
	@Override
	public boolean meetsRequirement(PlayerProfile playerKilled, PlayerProfile killer) {
		return requiredMapName.equals(playerKilled.getPlayer().getWorld().getName());
	}
}
