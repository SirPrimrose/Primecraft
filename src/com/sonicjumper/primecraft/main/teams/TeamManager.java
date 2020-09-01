package com.sonicjumper.primecraft.main.teams;

import java.util.ArrayList;

import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;

public class TeamManager {
	private ArrayList<PlayerTeam> playerTeams;
	
	public TeamManager() {
		playerTeams = new ArrayList<PlayerTeam>();
	}
	
	public void addPlayerTeam(PlayerTeam team) {
		playerTeams.add(team);
	}
	
	public PlayerTeam getTeamForDetails(TeamDetails details) {
		for(PlayerTeam pt : playerTeams) {
			if(pt.getTeamDetails().equals(details)) {
				return pt;
			}
		}
		return null;
	}

	public ArrayList<PlayerTeam> getPlayingTeams() {
		ArrayList<PlayerTeam> playingTeams = new ArrayList<PlayerTeam>();
		for(PlayerTeam pt : playerTeams) {
			if(pt.getTeamDetails().isPlayingTeam()) {
				playingTeams.add(pt);
			}
		}
		return playingTeams;
	}
	
	public PlayerTeam getHighestScoringTeamIgnoring(ArrayList<PlayerTeam> ignoreTeams) {
		int highScore = -1;
		PlayerTeam highestScoringTeam = null;
		for(PlayerTeam pt : playerTeams) {
			if(!ignoreTeams.contains(pt) && pt.getTeamDetails().isPlayingTeam()) {
				if(pt.getTeamPoints() > highScore) {
					highScore = pt.getTeamPoints();
					highestScoringTeam = pt;
				}
			}
		}
		return highestScoringTeam;
	}
	
	public int getTeamCount() {
		return playerTeams.size();
	}
}
