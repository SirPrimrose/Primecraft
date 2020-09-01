package com.sonicjumper.primecraft.main.deathmessages;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RequirementMinimumAssisters extends DeathRequirement {
	private int minimumAssisters;
	
	public RequirementMinimumAssisters(int assisters) {
		minimumAssisters = assisters;
	}
	
	@Override
	public boolean meetsRequirement(PlayerProfile playerKilled, PlayerProfile killer) {
		return playerKilled.getDamagerCount() >= minimumAssisters;
	}

}
