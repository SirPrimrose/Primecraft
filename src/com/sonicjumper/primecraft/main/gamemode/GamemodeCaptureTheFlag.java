package com.sonicjumper.primecraft.main.gamemode;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.SoundManager;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.util.PrimeUtility;
import com.sonicjumper.primecraft.main.world.WorldDetails;

public class GamemodeCaptureTheFlag extends Gamemode {
	public GamemodeCaptureTheFlag(int index, String name, String shortName, int number) {
		super(index, name, shortName, number);
	}
	
	private int flagRespawnTime;
	
	private int flagCaptureRange;
	
	public ArrayList<Flag> teamFlags;
	
	@Override
	public void preGame() {
		super.preGame();
		
		flagRespawnTime = 15;
		flagCaptureRange = 4;
		
		teamFlags = new ArrayList<Flag>();
		for(int i = 0; i < numberOfTeams; i++) {
			teamFlags.add(new Flag(WorldDetails.getTeamFlagSpawn(PrimeCraftMain.instance.game.loadedWorld.getWorldName(), i).getBlock(), TeamDetails.getDetailsForID(i)));
		}
	}

	@Override
	public void startGame() {
		super.startGame();
	}
	
	@Override
	public void updateGame() {
		super.updateGame();
		
		//Look for player near each flag
		for(Flag f : teamFlags) {
			updateFlag(f);
		}
		
		//Check for scoring conditions
		for(Flag flag : teamFlags) {
			for(Flag enemyFlag : teamFlags) {
				if(!flag.equals(enemyFlag) && flag.isBeingCarried() && flag.getFlagbearer().getCurrentTeam().equals(enemyFlag.getTeamDetails().getTeamObject())) {
					if(flag.getCurrentLocation().distance(enemyFlag.getStartPosition()) < flagCaptureRange) {
						flagScore(flag);
						broadcastTeamScores();
					}
				}
			}
		}
	}

	private void flagScore(Flag scoringFlag) {
		if(scoringFlag.getFlagbearer() != null) {
			scoringFlag.getFlagbearer().capturedFlag();
			scoringFlag.getFlagbearer().getCurrentTeam().creditPoints(1);
			PrimeCraftMain.instance.getServer().broadcastMessage(scoringFlag.getFlagbearer().getFriendlyName() + ChatColor.GOLD + " has scored for the " + scoringFlag.getFlagbearer().getCurrentTeam().getTeamDetails().getChatColor() + scoringFlag.getFlagbearer().getCurrentTeam().getTeamDetails().name() + ChatColor.GOLD + " team.");
			PlayerProfile pp = scoringFlag.getFlagbearer();
			PrimeUtility.spawnFireworks(pp.getLocation(), pp.getCurrentTeam().getTeamDetails().getDyeColor().getColor(), 8, 4.0F);
		}
		if(scoringFlag.getTeamDetails().getTeamObject().getTeamPoints() < maxPoints) {
			SoundManager.playTeamScore(scoringFlag.getTeamDetails());
		}
		scoringFlag.resetFlag();
	}
	
	@Override
	public void pauseGame() {
		super.pauseGame();
		
		for(Flag f : teamFlags) {
			f.resetFlag();
		}
	}

	@Override
	public void endGame() {
		super.endGame();
		
		for(Flag f : teamFlags) {
			f.resetFlag();
		}
	}
	
	public void updateFlag(Flag flag) {
		if(flag.isOnGround()) {
			ArrayList<PlayerProfile> players = PrimeCraftMain.instance.game.getPlayers();
			Player closestPlayer = null;
			double shortestDistance = flagCaptureRange;
			for(PlayerProfile pp : players) {
				if(!pp.getPlayer().isDead() && !pp.getCurrentTeam().getTeamDetails().equals(flag.getTeamDetails()) && !pp.getCurrentTeam().getTeamDetails().equals(TeamDetails.Neutral) && !isPlayerFlagbearer(pp)) {
					if(flag.getBlock().getWorld().equals(pp.getPlayer().getWorld())) {
						double distanceToPlayer = flag.getBlock().getLocation().distance(pp.getPlayer().getLocation());
						if(distanceToPlayer < flagCaptureRange) {
							if(distanceToPlayer < shortestDistance) {
								shortestDistance = distanceToPlayer;
								closestPlayer = pp.getPlayer();
							}
						}
					} else {
						pp.getPlayer().sendMessage(ChatColor.DARK_RED + "You must be in neutral team to leave the playing world!");
					}
				}
			}
			if(closestPlayer != null) {
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(closestPlayer);
				flag.pickupFlag(pp);
				Bukkit.getServer().broadcastMessage(pp.getFriendlyName() + ChatColor.GOLD + " has taken the " + flag.getTeamDetails().getChatColor() + flag.getTeamDetails().name() + ChatColor.GOLD + " flag.");
			}
		}
		
		if(flag.isBeingCarried()) {
			flag.timeOnGround = -1;
			if(!flag.getFlagbearer().getPlayer().isOnline() || flag.getFlagbearer().getPlayer().isDead()) {
				flag.dropFlag();
			}
		}
		
		if(flag.isBeingCarried()) {
			if(gameTicker % 2 == 0) {
				flag.getFlagbearer().getPlayer().getWorld().playEffect(flag.getFlagbearer().getPlayer().getEyeLocation().add(1.0D - (rand.nextDouble() * 2.0D), 1.0D - (rand.nextDouble() * 2.0D), 1.0D - (rand.nextDouble() * 2.0D)), Effect.HAPPY_VILLAGER, 0);
			}
		}
		
		if(flag.timeOnGround / 10 == flagRespawnTime) {
			Bukkit.getServer().broadcastMessage(flag.getTeamDetails().getChatColor() + "The " + flag.getTeamDetails().name() + " Flag has been reset.");
			SoundManager.playTeamFlagReset(flag.getTeamDetails());
			flag.resetFlag();
		}
		
		if(flag.timeOnGround % 10 == 0) {
			int timeLeft = (flagRespawnTime - (flag.timeOnGround / 10));
			if(timeLeft % 5 == 0 || timeLeft <= 3) {
				Bukkit.getServer().broadcastMessage(flag.getTeamDetails().getChatColor() + "" + timeLeft + " seconds until " + flag.getTeamDetails().name() + " Flag reset.");
			}
		}
		
		if(flag.isOnGround() && !flag.isAtBase()) {
			flag.timeOnGround++;
		}
		
		flag.updateLocation();
	}
	
	@Override
	public void onPlayerDeath(Player player, Player killer) {
		//Not putting check to drop flags here because some players log off or teleport to drop flag
	}
	
	@Override
	public boolean onPlayerTeleport(Player player, TeleportCause cause) {
		if(cause.equals(TeleportCause.UNKNOWN)) {
			return true;
		}
		Flag carriedFlag = getFlagForFlagbearer(PrimeCraftMain.instance.game.getProfileForPlayer(player));
		if(carriedFlag != null) {
			carriedFlag.dropFlag();
		}
		return true;
	}

	@Override
	public void onButtonPress(Block pressedBlock, PlayerProfile playerPressing) {
	}
	
	public boolean isPlayerFlagbearer(PlayerProfile playerProfile) {
		for(Flag f : teamFlags) {
			if(playerProfile.equals(f.getFlagbearer())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected int getMaxPoints() {
		return (int) (Math.pow(numberOfTeams, 2) + 1);
	}
	
	public Flag getFlagForFlagbearer(PlayerProfile playerProfile) {
		for(Flag f : teamFlags) {
			if(playerProfile.equals(f.getFlagbearer())) {
				return f;
			}
		}
		return null;
	}

	public String[] getFlagPositions() {
		String[] messages = new String[teamFlags.size()];
		for(int i = 0; i < teamFlags.size(); i++) {
			messages[i] = "The " + teamFlags.get(i).getTeamDetails().getChatColor() + teamFlags.get(i).getTeamDetails().name() + ChatColor.WHITE + " flag is " + teamFlags.get(i).getLocationString();
		}
		return messages;
	}
}
