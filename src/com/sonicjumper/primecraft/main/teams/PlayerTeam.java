package com.sonicjumper.primecraft.main.teams;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.SoundManager;
import com.sonicjumper.primecraft.main.gamemode.SpawnGate;
import com.sonicjumper.primecraft.main.gamemode.SpawnGate.GateType;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public class PlayerTeam {
	public enum TeamDetails {
		Red(0, ChatColor.RED, DyeColor.RED, true),
		Blue(1, ChatColor.BLUE, DyeColor.BLUE, true),
		Yellow(2, ChatColor.YELLOW, DyeColor.YELLOW, true),
		Purple(3, ChatColor.DARK_PURPLE, DyeColor.PURPLE, true),
		Neutral(4, ChatColor.GRAY, DyeColor.GRAY, false);
		
		private int teamID;
		private ChatColor chatColor;
		private DyeColor dyeColor;
		private boolean playingTeam;

		private PlayerTeam matchingTeam;
		
		TeamDetails(int ID, ChatColor cc, DyeColor dc, boolean plays) {
			teamID = ID;
			chatColor = cc;
			dyeColor = dc;
			playingTeam = plays;
		}
		
		public void setTeam(PlayerTeam team) {
			matchingTeam = team;
		}
		
		public ChatColor getChatColor() {
			return chatColor;
		}
		
		public DyeColor getDyeColor() {
			return dyeColor;
		}
		
		public PlayerTeam getTeamObject() {
			return matchingTeam;
		}
		
		public boolean isPlayingTeam() {
			return playingTeam;
		}

		public static TeamDetails getDetailsForID(int ID) {
			for(TeamDetails td : values()) {
				if(td.teamID == ID) {
					return td;
				}
			}
			return null;
		}

		public static TeamDetails getDetailsForName(String string) {
			for(TeamDetails td : values()) {
				if(td.name().equalsIgnoreCase(string)) {
					return td;
				}
			}
			return null;
		}

		public int getTeamID() {
			return teamID;
		}
	}
	private ArrayList<PlayerProfile> teamMembers = new ArrayList<PlayerProfile>();
	
	private Location teamSpawn;
	private SpawnGate gameGate;
	private SpawnGate spawnGate;
	
	private TeamDetails teamDetails;
	private int teamPoints;
	private boolean nearWinAnnounced;
	
	public PlayerTeam(TeamDetails details) {
		teamDetails = details;
		teamDetails.setTeam(this);
	}
	
	public void setupTeam(Location spawn) {
		teamMembers.clear();
		teamSpawn = spawn;
		spawnGate = new SpawnGate(teamDetails, spawn.getWorld().getName(), GateType.Respawn);
		gameGate = new SpawnGate(teamDetails, spawn.getWorld().getName(), GateType.Game);
		teamPoints = 0;
		nearWinAnnounced = false;
	}
	
	public void teleportPlayerToSpawn(PlayerProfile pp) {
		if(pp.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
			if(teamSpawn != null) {
				pp.getPlayer().teleport(teamSpawn);
			} else {
				PrimeCraftMain.instance.game.broadcastMessageToPlayers("The spawn for team " + getTeamDetails().name() + " is not set. Please do so or errors will happen.");
			}
		}
	}
	
	public void creditPoints(int pointsToCredit) {
		teamPoints += pointsToCredit;
		if(!nearWinAnnounced && teamPoints >= Math.floor(PrimeCraftMain.instance.game.currentGamemode.maxPoints * 9.0D / 10.0D)) {
			nearWinAnnounced = true;
			SoundManager.playTeamNearWin(teamDetails);
		}
	}
	
	public void addPlayer(PlayerProfile pp) {
		if(pp.getCurrentTeam() != null) {
			pp.getCurrentTeam().removePlayerFromTeam(pp);
		}
		teamMembers.add(pp);
		teleportPlayerToSpawn(pp);
		if(getTeamDetails().isPlayingTeam()) {
			pp.refreshEntireLoadout();
		} else {
			pp.getPlayer().getInventory().clear();
		}
		pp.setCurrentTeam(this);
		pp.refreshPlayerDisplayName();
		SoundManager.playTeamJoin(pp.getPlayer(), teamDetails);
		pp.getPlayer().sendMessage("You are on " + this.getTeamDetails().getChatColor() + this.getTeamDetails().name() + ChatColor.WHITE + " Team");
	}

	public void removePlayerFromTeam(PlayerProfile pp) {
		teamMembers.remove(pp);
		pp.setCurrentTeam(null);
	}

	public boolean isPlayerOnTeam(PlayerProfile pp) {
		return teamMembers.contains(pp);
	}

	public ArrayList<PlayerProfile> getPlayerList() {
		return teamMembers;
	}
	
	public int getPlayerCount() {
		return teamMembers.size();
	}
	
	public int getTeamLoadoutRating() {
		int rating = 0;
		for(PlayerProfile pp : teamMembers) {
			rating += pp.getLoadoutRating();
		}
		return rating;
	}
	
	public int getTeamPoints() {
		return teamPoints;
	}

	public TeamDetails getTeamDetails() {
		return teamDetails;
	}

	public Location getSpawn() {
		return teamSpawn;
	}

	public SpawnGate getSpawnGate() {
		return spawnGate;
	}

	public SpawnGate getGameGate() {
		return gameGate;
	}
}
