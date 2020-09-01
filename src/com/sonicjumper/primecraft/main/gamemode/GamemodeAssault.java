package com.sonicjumper.primecraft.main.gamemode;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.util.PrimeUtility;
import com.sonicjumper.primecraft.main.world.WorldDetails;

public class GamemodeAssault extends Gamemode {
	public enum AssaultPhase {
		PreGame,
		RoundOne,
		Intermission,
		RoundTwo
	}
	
	public TeamDetails assaultTeam;
	public TeamDetails defendTeam;
	
	public ArrayList<AssaultPoint> assaultPoints;
	private AssaultPhase gamePhase;
	
	private int assaultLivesMax;
	private int assaultLivesRemaining;
	private int intermissionTime;
	private final int MAX_INTERMISSION_TIME = 300;
	
	public GamemodeAssault(int index, String name, String shortName, int number) {
		super(index, name, shortName, number);
	}
	
	@Override
	public void preGame() {
		super.preGame();
		
		assaultPoints = new ArrayList<AssaultPoint>();
		
		assaultTeam = TeamDetails.Red;
		defendTeam = TeamDetails.Blue;
		
		assaultLivesMax = Math.min(PrimeCraftMain.instance.game.getPlayers().size() * 5, 80);
		assaultLivesRemaining = assaultLivesMax;
		
		String worldName = PrimeCraftMain.instance.game.loadedWorld.getWorldName();
		for(int i = 0; i < WorldDetails.getAssaultPointCount(worldName); i++) {
			assaultPoints.add(new AssaultPoint(WorldDetails.getAssaultPointLocation(worldName, i), i, defendTeam));
		}
	}
	
	@Override
	public void startGame() {
		super.startGame();
		gamePhase = AssaultPhase.RoundOne;
	}
	
	@Override
	public void updateGame() {
		super.updateGame();
		if(gamePhase == AssaultPhase.Intermission) {
			intermissionTime++;
			if(intermissionTime > MAX_INTERMISSION_TIME - 50 && intermissionTime % 10 == 0) {
				int secondsTilStart = (MAX_INTERMISSION_TIME - intermissionTime) / 10;
				Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Game will start in " + secondsTilStart + " seconds...");
			}
			if(intermissionTime >= MAX_INTERMISSION_TIME) {
				intermissionTime = 0;
				gamePhase = AssaultPhase.RoundTwo;
				Bukkit.getServer().broadcastMessage(ChatColor.DARK_GRAY + "Game start!");
			}
		}
		if(gamePhase == AssaultPhase.RoundOne || gamePhase == AssaultPhase.RoundTwo) {
			// Update game normally
			for(AssaultPoint ap : assaultPoints) {
				ap.updatePoint();
			}
		}
	}

	@Override
	public void pauseGame() {
		super.pauseGame();
	}
	
	@Override
	public void endGame() {
		super.endGame();
	}

	@Override
	public void onPlayerDeath(Player player, Player killer) {
		PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(player);
		if(pp.getCurrentTeam().getTeamDetails().equals(assaultTeam)) {
			assaultLivesRemaining--;
		}
	}

	@Override
	public boolean onPlayerTeleport(Player player, TeleportCause cause) { return false; }
	
	@Override
	public void onButtonPress(Block pressedBlock, PlayerProfile playerPressing) {
		for(AssaultPoint ap : assaultPoints) {
			Block button = ap.getArmingButton();
			if(pressedBlock.equals(button)) {
				if(ap.isDestroyed()) {
					playerPressing.getPlayer().sendMessage(ChatColor.GRAY + "This bomb is already destroyed.");
				} else if(playerPressing.getCurrentTeam().getTeamDetails().equals(ap.getAlliance())) {
					if(!ap.isDisarming() && ap.isArmed()) {
						ap.setDisarming(playerPressing);
						playerPressing.getPlayer().sendMessage(ChatColor.GRAY + "Disarming the bomb in 5 seconds...");
					} else if(ap.isArming()) {
						ap.cancelArming(playerPressing);
						playerPressing.getPlayer().sendMessage(ChatColor.GRAY + "Cancelled arming the bomb.");
					} else if(!ap.isArmed()) {
						playerPressing.getPlayer().sendMessage(ChatColor.GRAY + "The bomb is not armed.");
					} else if(ap.isDisarming()) {
						playerPressing.getPlayer().sendMessage(ChatColor.GRAY + "The bomb is already disarming.");
					}
				} else { // Player is not on the same team
					if(!ap.isArming() && !ap.isArmed()) {
						ap.setArming(playerPressing);
						playerPressing.getPlayer().sendMessage(ChatColor.GRAY + "Arming the bomb in 5 seconds...");
					} else if(ap.isDisarming()) {
						ap.cancelDisarming(playerPressing);
						playerPressing.getPlayer().sendMessage(ChatColor.GRAY + "Cancelled disarming the bomb.");
					} else if(ap.isArmed()) {
						playerPressing.getPlayer().sendMessage(ChatColor.GRAY + "The bomb is already armed.");
					} else if(ap.isArming()) {
						playerPressing.getPlayer().sendMessage(ChatColor.GRAY + "The bomb is already arming.");
					}
				}
			}
		}
	}

	@Override
	protected int getMaxPoints() {
		String worldName = PrimeCraftMain.instance.game.loadedWorld.getWorldName();
		return WorldDetails.getAssaultPointCount(worldName);
	}
	
	@Override
	public ArrayList<PlayerTeam> getWinningTeams() {
		ArrayList<PlayerTeam> winningTeams = new ArrayList<PlayerTeam>();
		if(assaultTeam.getTeamObject().getTeamPoints() == maxPoints) {
			winningTeams.add(assaultTeam.getTeamObject());
			winningTeams.add(defendTeam.getTeamObject());
		} else {
			winningTeams.add(defendTeam.getTeamObject());
			winningTeams.add(assaultTeam.getTeamObject());
		}
		return winningTeams;
	}
	
	@Override
	public boolean doesWinnerExist() {
		boolean winnerExists =  super.doesWinnerExist() || (assaultLivesRemaining <= 0);
		if(winnerExists && gamePhase == AssaultPhase.RoundOne) {
			ArrayList<PlayerTeam> winningTeams = getWinningTeams();
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + winningTeams.get(0).getTeamDetails().name() + " Wins!");
			for(int i = 0; i < winningTeams.size(); i++) {
				PlayerTeam pt = winningTeams.get(i);
				for(PlayerProfile pp : pt.getPlayerList()) {
					pp.wonPlace(i + 1);
				}
			}
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "Switching teams, beginning Intermission. Game will start in 30 seconds...");
			switchTeamPlayers();
			gameTicker = 0;
			assaultLivesRemaining = assaultLivesMax;
			gamePhase = AssaultPhase.Intermission;
		}
		if(winnerExists && gamePhase == AssaultPhase.RoundTwo) {
			return true;
		}
		
		return false;
	}
	
	private void switchTeamPlayers() {
		ArrayList<PlayerProfile> assaultPlayers = assaultTeam.getTeamObject().getPlayerList();
		ArrayList<PlayerProfile> defendPlayers = defendTeam.getTeamObject().getPlayerList();
		for(PlayerProfile pp : assaultPlayers) {
			defendTeam.getTeamObject().addPlayer(pp);
		}
		for(PlayerProfile pp : defendPlayers) {
			assaultTeam.getTeamObject().addPlayer(pp);
		}
	}
	
	@Override
	public String getScore() {
		String scoreMessage = assaultTeam.getChatColor() + "Lives: " + assaultLivesRemaining + "; ";
		for(int i = 0; i < assaultPoints.size(); i++) {
			scoreMessage = scoreMessage.concat(ChatColor.WHITE + "" + PrimeUtility.getLetterFromNumber(i) + ": " + assaultPoints.get(i).getStatusString() + ChatColor.WHITE + "; ");
		}
		
		PrimeUtility.updateGameDataScoreboard();
		return scoreMessage;
	}
}
