package com.sonicjumper.primecraft.main.gamemode;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.SoundManager;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.util.PrimeUtility;
import com.sonicjumper.primecraft.main.world.WorldDetails;

public class GamemodeCommandPost extends Gamemode {
	public GamemodeCommandPost(int index, String name, String shortName, int number) {
		super(index, name, shortName, number);
	}
	
	private int TAKEOVER_WIN_TIME = 600;

	private int pointCaptureRange;
	
	private TeamDetails currentTakeoverTeam;
	private int takeoverTime;
	
	public ArrayList<CapturePoint> capturePoints;
	
	public HashMap<PlayerProfile, Integer> playerToCaptureTime;
	
	@Override
	public void preGame() {
		super.preGame();
		
		playerToCaptureTime = new HashMap<PlayerProfile, Integer>();
		
		pointCaptureRange = 6;
		
		capturePoints = new ArrayList<CapturePoint>();
		String worldName = PrimeCraftMain.instance.game.loadedWorld.getWorldName();
		for(int i = 0; i < WorldDetails.getCapturePointCount(worldName); i++) {
			capturePoints.add(new CapturePoint(WorldDetails.getCapturePointLocation(worldName, i), i));
		}
	}
	
	@Override
	public void startGame() {
		super.startGame();
	}
	
	@Override
	public void updateGame() {
		super.updateGame();
		
		for(CapturePoint cp : capturePoints) {
			// Update CP Logic
			updateCapturePoint(cp);
			
			// Check for takeover win condition
			for(TeamDetails team : TeamDetails.values()) {
				if(team.isPlayingTeam()) {
					if(getCommandPostsCountForTeam(team.getTeamObject()) == getNumberOfCommandPosts()) {
						if(!team.equals(currentTakeoverTeam)) {
							currentTakeoverTeam = team;
							takeoverTime = 0;
							PrimeCraftMain.instance.game.broadcastMessageToPlayers(team.getChatColor() + "" + team.name() + " team has captured all CP's. They will win if not stopped.");
						} else {
							takeoverTime++;
						}
					}
				}
			}
			
			//Check for scoring conditions
			if(gameTicker % 100 == 0 && cp.getAlliance().isPlayingTeam()) {
				cp.getAlliance().getTeamObject().creditPoints(cp.getCapturePointScoreValue());
			}
		}
	}
	
	@Override
	public void pauseGame() {
		super.pauseGame();
		
		for(CapturePoint cp : capturePoints) {
			cp.resetPoint();
		}
	}
	
	@Override
	public void endGame() {
		super.endGame();
		
		for(CapturePoint cp : capturePoints) {
			cp.resetPoint();
		}
	}

	private void updateCapturePoint(CapturePoint cp) {
		cp.updateCapturePoint();
		
		int captureCountPerTick = 0;
		boolean teamConflict = false;
		TeamDetails capturingTeam = null;
		// Handle capturing this Command Post
		for(PlayerProfile pp : PrimeCraftMain.instance.game.getPlayers()) {
			if(!pp.getPlayer().isDead() && !pp.getCurrentTeam().getTeamDetails().equals(TeamDetails.Neutral)) {
				if(cp.getLocation().getWorld().equals(pp.getPlayer().getWorld())) {
					double distanceToPlayer = cp.getLocation().distance(pp.getPlayer().getLocation());
					if(distanceToPlayer < pointCaptureRange) {
						if(pp.getPlayer().isSneaking()) {
							if(cp.addTeamAlliance(pp.getCurrentTeam().getTeamDetails(), 2))
								captureCountPerTick++;
						} else {
							if(cp.addTeamAlliance(pp.getCurrentTeam().getTeamDetails(), 1))
								captureCountPerTick++;
						}
						if(cp.isBeingCaptured()) {
							checkIfPlayerShouldGetCoins(pp);
							captureCountPerTick++;
						}
						if(!pp.getCurrentTeam().getTeamDetails().equals(capturingTeam)) {
							if(capturingTeam == null) {
								capturingTeam = pp.getCurrentTeam().getTeamDetails();
							} else {
								teamConflict = true;
							}
						} else {
							capturingTeam = pp.getCurrentTeam().getTeamDetails();
						}
					}
				} else {
					pp.getPlayer().sendMessage(ChatColor.DARK_RED + "You must be in neutral team to leave the playing world!");
				}
			}
		}
		// If no one is near this CP
		if(captureCountPerTick == 0) {
			if(cp.getAlliance().equals(TeamDetails.Neutral)) {
				cp.addTeamAlliance(TeamDetails.Neutral);
			} else {
				// If halfway captured or more
				if(cp.getCapturePointScoreValue() >= 2 && !cp.isFullyCaptured()) {
					cp.addTeamAlliance(cp.getAlliance());
				}
			}
		}

		if(cp.isFullyCaptured() && captureCountPerTick > 0 && !teamConflict) {
			// If was captured this tick and is now fully captured and is not being fought over
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "Command Post " + (cp.getID() + 1) + " has been captured by the " + capturingTeam.getChatColor() + capturingTeam.name() + ChatColor.GOLD + " team.");
			PrimeUtility.spawnFireworks(cp.getLocation(), cp.getAlliance().getDyeColor().getColor(), 12, 5.0F);
			SoundManager.playCommandPostTaken(cp.getID());
			for(PlayerProfile pp : PrimeCraftMain.instance.game.getPlayers()) {
				if(!pp.getPlayer().isDead() && cp.getAlliance().equals(pp.getCurrentTeam().getTeamDetails())) {
					double distanceToPlayer = cp.getLocation().distance(pp.getPlayer().getLocation());
					if(distanceToPlayer < pointCaptureRange) {
						pp.capturedPoint();
					}
				}
			}
		}
		
		if(cp.didAllianceChange() && !teamConflict && capturingTeam != null) {
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "The " + capturingTeam.getChatColor() + capturingTeam.name() + ChatColor.GOLD + " team is capturing Command Post " + (cp.getID() + 1));
		}

		for(int i = 0; i < captureCountPerTick * 2; i++) {
			cp.getLocation().getWorld().playEffect(cp.getLocation().add(4.0D - (rand.nextDouble() * 8.0D), 2.0D - (rand.nextDouble() * 4.0D), 4.0D - (rand.nextDouble() * 8.0D)), Effect.HAPPY_VILLAGER, 0);
		}

		cp.updateCapturePoint();
	}
	
	// Override this to take into account takeovers(where a team can win by capturing all points)
	@Override
	public boolean doesWinnerExist() {
		boolean doesWinnerExist =  super.doesWinnerExist();
		
		return doesWinnerExist || (takeoverTime >= TAKEOVER_WIN_TIME);
	}
	
	// Override this to take into account if a team won by a takeover, in which case their points don't matter, they always get first place
	@Override
	public ArrayList<PlayerTeam> getWinningTeams() {
		ArrayList<PlayerTeam> winningTeams = new ArrayList<PlayerTeam>();
		if(takeoverTime >= TAKEOVER_WIN_TIME) {
			winningTeams.add(currentTakeoverTeam.getTeamObject());
		}
		do {
			winningTeams.add(PrimeCraftMain.instance.game.teamManager.getHighestScoringTeamIgnoring(winningTeams));
		} while(winningTeams.size() < numberOfTeams);
		return winningTeams;
	}

	private int getCommandPostsCountForTeam(PlayerTeam pt) {
		int pointCount = 0;
		for(CapturePoint cp : capturePoints) {
			if(cp.getAlliance().equals(pt.getTeamDetails()) && cp.isFullyCaptured()) {
				pointCount++;
			}
		}
		return pointCount;
	}
	
	private int getNumberOfCommandPosts() {
		return capturePoints.size();
	}
	
	private void checkIfPlayerShouldGetCoins(PlayerProfile pp) {
		if(!playerToCaptureTime.containsKey(pp)) {
			playerToCaptureTime.put(pp, 0);
		}
		if(playerToCaptureTime.get(pp) >= 60) {
			pp.capturePointProcess();
			playerToCaptureTime.put(pp, 0);
		} else {
			playerToCaptureTime.put(pp, playerToCaptureTime.get(pp) + 1);
		}
	}

	@Override
	public void onPlayerDeath(Player player, Player killer) {
	}

	@Override
	public boolean onPlayerTeleport(Player player, TeleportCause cause) {
		return true;
	}

	@Override
	public void onButtonPress(Block pressedBlock, PlayerProfile playerPressing) {
	}

	@Override
	protected int getMaxPoints() {
		return 400;//return numberOfTeams * 100;
	}
	
	@Override
	public String getScore() {
		String scoreMessage = ChatColor.WHITE + "";
		for(int i = 0; i < numberOfTeams; i++) {
			PlayerTeam pt = PrimeCraftMain.instance.game.teamManager.getPlayingTeams().get(i);
			if(i != 0) {
				scoreMessage = scoreMessage.concat(ChatColor.WHITE + " and ");
			}
			scoreMessage = scoreMessage.concat(pt.getTeamDetails().getChatColor() + pt.getTeamDetails().name() + "(" + getCommandPostsCountForTeam(pt) + ")" + ": " + (pt.getTeamDetails().getTeamObject().getTeamPoints()));
		}
		
		PrimeUtility.updateGameDataScoreboard();
		return scoreMessage;
	}
}
