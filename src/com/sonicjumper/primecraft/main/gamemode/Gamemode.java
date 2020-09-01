package com.sonicjumper.primecraft.main.gamemode;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.eventbag.RandomEvent;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public abstract class Gamemode {
	public static Gamemode[] gamemodes = new Gamemode[16];
	
	public static Gamemode ctf2 = new GamemodeCaptureTheFlag(0, "CaptureTheFlag2Team", "CTF", 2);
	public static Gamemode ctf3 = new GamemodeCaptureTheFlag(1, "CaptureTheFlag3Team", "CTF", 3);
	public static Gamemode ctf4 = new GamemodeCaptureTheFlag(2, "CaptureTheFlag4Team", "CTF", 4);
	public static Gamemode tdm2 = new GamemodeTeamDeathmatch(3, "TeamDeathmatch2Team", "TDM", 2);
	public static Gamemode tdm3 = new GamemodeTeamDeathmatch(4, "TeamDeathmatch3Team", "TDM", 3);
	public static Gamemode tdm4 = new GamemodeTeamDeathmatch(5, "TeamDeathmatch4Team", "TDM", 4);
	public static Gamemode art2 = new GamemodeArtifact(6, "Artifact2Team", "ART", 2);
	public static Gamemode art3 = new GamemodeArtifact(7, "Artifact3Team", "ART", 3);
	public static Gamemode art4 = new GamemodeArtifact(8, "Artifact4Team", "ART", 4);
	public static Gamemode kng2 = new GamemodeCommandPost(9, "CommandPoint2Team", "CP", 2);
	public static Gamemode kng3 = new GamemodeCommandPost(10, "CommandPoint3Team", "CP", 3);
	public static Gamemode kng4 = new GamemodeCommandPost(11, "CommandPoint4Team", "CP", 4);
	public static Gamemode asu2 = new GamemodeAssault(12, "Assault2Team", "ASU", 2);
	//public static Gamemode asu3 = new GamemodeAssault(13, "Assault3Team", "ASU", 3);
	//public static Gamemode asu4 = new GamemodeAssault(14, "Assault4Team", "ASU", 4);
	
	protected Random rand = new Random();
	
	protected static final int MAX_GAME_TIME_IN_MINUTES = 15;
	protected int gameTicker;
	
	public int maxPoints;
	public int numberOfTeams;
	
	private static final int RANDOM_EVENT_BUFFER_TIME_MAX = 600;
	private int randomEventBufferTime;
	
	private String gamemodeName;
	private String shorthandName;
	
	public Gamemode(int index, String name, String shortName, int number) {
		gamemodeName = name;
		shorthandName = shortName;
		numberOfTeams = number;
		
		gamemodes[index] = this;
	}
	
	/**
	 * Things to do during the countdown.
	 */
	public void preGame() {
		for(PlayerTeam pt : PrimeCraftMain.instance.game.teamManager.getPlayingTeams()) {
			pt.getSpawnGate().openGate();
		}

		gameTicker = 0;
		maxPoints = getMaxPoints();
		
		PrimeCraftMain.instance.game.broadcastMessageToPlayers(ChatColor.GOLD + "The selected gamemode is " + gamemodeName);
		PrimeCraftMain.instance.game.broadcastMessageToPlayers(ChatColor.GOLD + "Get " + maxPoints + " to win!");
	}

	public void startGame() {
		randomEventBufferTime = RANDOM_EVENT_BUFFER_TIME_MAX + rand.nextInt(RANDOM_EVENT_BUFFER_TIME_MAX);
		
		for(PlayerTeam pt : PrimeCraftMain.instance.game.teamManager.getPlayingTeams()) {
			pt.getGameGate().openGate();
		}
	}
	
	public void updateGame() {
		if(gameTicker % 1200 == 0) {
			broadcastTeamScores();
		}
		
		if(gameTicker >= (MAX_GAME_TIME_IN_MINUTES * 400) && gameTicker % 600 == 0) {
			Bukkit.getServer().broadcastMessage(ChatColor.BOLD + "" + ChatColor.AQUA + (MAX_GAME_TIME_IN_MINUTES - (gameTicker / 600)) + " minutes left until game ends.");
		}
		
		if(gameTicker % 10 == 0) {
			PrimeCraftMain.instance.playerRespawner.updateRespawner();
		}
		
		if(gameTicker % 50 == 0) {
			for(PlayerTeam pt : PrimeCraftMain.instance.game.teamManager.getPlayingTeams()) {
				pt.getSpawnGate().openGate();
			}
		}
		
		if(gameTicker % 50 - 20 == 0) {
			for(PlayerTeam pt : PrimeCraftMain.instance.game.teamManager.getPlayingTeams()) {
				pt.getSpawnGate().closeGate();
			}
		}
		
		if(gameTicker % 200 == 0) {
			for(PlayerProfile pp : PrimeCraftMain.instance.game.getPlayers()) {
				pp.clearDamagers(false);
			}
		}
		
		for(PlayerProfile pp : PrimeCraftMain.instance.game.getPlayers()) {
			pp.updatePlayer();
		}
		
		if(randomEventBufferTime > 0) {
			randomEventBufferTime--;
		} else {
			RandomEvent event = RandomEvent.getRandomEvent();
			RandomEvent.activateEvent(event);
			randomEventBufferTime = RANDOM_EVENT_BUFFER_TIME_MAX + rand.nextInt(RANDOM_EVENT_BUFFER_TIME_MAX);
		}
		
		gameTicker++;
	}
	
	public void pauseGame() {
		for(PlayerTeam pt : PrimeCraftMain.instance.game.teamManager.getPlayingTeams()) {
			pt.getSpawnGate().openGate();
			pt.getGameGate().closeGate();
		}
	}
	
	public void unPauseGame() {
		for(PlayerTeam pt : PrimeCraftMain.instance.game.teamManager.getPlayingTeams()) {
			pt.getGameGate().openGate();
		}
	}

	public void endGame() {
		for(PlayerTeam pt : PrimeCraftMain.instance.game.teamManager.getPlayingTeams()) {
			pt.getSpawnGate().openGate();
			pt.getGameGate().closeGate();
		}
		
		for(PlayerProfile pp : PrimeCraftMain.instance.game.getPlayers()) {
			if(pp.getCurrentTeam().getTeamDetails().isPlayingTeam()) {
				pp.clearDamagers(true);
			}
		}
		
		gameTicker = 0;
		
		PrimeCraftMain.instance.game.weaponHandler.clearWeapons();
	}
	
	protected void broadcastTeamScores() {
		PrimeCraftMain.instance.getServer().broadcastMessage(getScore());
	}
	
	/**
	 * Handle game logic when a player dies. Killer may be null
	 */
	public abstract void onPlayerDeath(Player player, Player killer);

	/**
	 * Handle game logic when a player teleports. Return false to cancel teleport.
	 */
	public abstract boolean onPlayerTeleport(Player player, TeleportCause cause);

	/**
	 * Handle mines exploding
	 * @return True if Mine exploded. False if otherwise.
	 */
	public boolean onPressurePlate(Block clickedBlock, PlayerProfile playerStepping) {
		return PrimeCraftMain.instance.game.weaponHandler.onPressurePlate(clickedBlock, playerStepping);
	}

	public abstract void onButtonPress(Block pressedBlock, PlayerProfile playerPressing);
	
	public boolean doesWinnerExist() {
		ArrayList<PlayerTeam> teams = PrimeCraftMain.instance.game.teamManager.getPlayingTeams();
		for(PlayerTeam pt : teams) {
			if(pt.getTeamPoints() >= maxPoints) {
				return true;
			}
		}
		if(gameTicker > MAX_GAME_TIME_IN_MINUTES * 600) {
			return true;
		}
		return false;
	}

	public boolean isGameStarted() {
		return gameTicker != 0;
	}
	
	protected abstract int getMaxPoints();
	
	/**
	 * Call this after confirming that the game has been won with doesWinnerExist().
	 * @return Winning teams if a team in order from first to last has won. Null if game is still going.
	 */
	public ArrayList<PlayerTeam> getWinningTeams() {
		ArrayList<PlayerTeam> winningTeams = new ArrayList<PlayerTeam>();
		do {
			winningTeams.add(PrimeCraftMain.instance.game.teamManager.getHighestScoringTeamIgnoring(winningTeams));
		} while(winningTeams.size() < numberOfTeams);
		return winningTeams;
	}

	public String getName() {
		return gamemodeName;
	}
	
	public String getShortName() {
		return shorthandName;
	}
	
	public static Gamemode getGamemodeForName(String name) {
		for(Gamemode g : gamemodes) {
			if(g != null) {
				if(g.gamemodeName.equalsIgnoreCase(name)) {
					return g;
				}
			}
		}
		return null;
	}

	public String getScore() {
		String scoreMessage = "";
		for(int i = 0; i < numberOfTeams; i++) {
			PlayerTeam pt = PrimeCraftMain.instance.game.teamManager.getPlayingTeams().get(i);
			if(i != 0) {
				scoreMessage = scoreMessage.concat(ChatColor.WHITE + " and ");
			}
			scoreMessage = scoreMessage.concat(pt.getTeamDetails().getChatColor() + pt.getTeamDetails().name() + ": " + pt.getTeamDetails().getTeamObject().getTeamPoints());
		}
		
		PrimeUtility.updateGameDataScoreboard();
		
		return scoreMessage;
	}
}
