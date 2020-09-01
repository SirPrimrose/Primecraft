package com.sonicjumper.primecraft.main.deathmessages;

import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;

public class RequirementTeam extends DeathRequirement {
	private TeamDetails requiredTeam;
	
	public RequirementTeam(TeamDetails team) {
		requiredTeam = team;
	}
	
	@Override
	public boolean meetsRequirement(PlayerProfile playerKilled, PlayerProfile killer) {
		return killer.getCurrentTeam().getTeamDetails().equals(requiredTeam);
	}
}
