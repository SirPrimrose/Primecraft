package com.sonicjumper.primecraft.main.gamemode;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.SoundManager;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.util.PrimeUtility;
import com.sonicjumper.primecraft.main.world.WorldDetails;

public class GamemodeArtifact extends Gamemode {
	public GamemodeArtifact(int index, String name, String shortName, int number) {
		super(index, name, shortName, number);
	}

	private int artifactRespawnTime;
	private int artifactCaptureRange;
	private int artifactPotency;
	private HashMap<TeamDetails, Integer> artifactRespawnPointCount;
	
	private IronGolem guardian;
	private int guardianRespawnTime;
	private int MAX_GUARDIAN_RESPAWN_TIME = 300;

	public Artifact artifact;

	@Override
	public void preGame() {
		super.preGame();

		maxPoints = getMaxPoints();
		artifactRespawnTime = 30;
		artifactCaptureRange = 4;
		artifactRespawnPointCount = new HashMap<TeamDetails, Integer>();

		artifact = new Artifact(WorldDetails.getArtifactSpawn(PrimeCraftMain.instance.game.loadedWorld.getWorldName()).getBlock());
	}

	@Override
	public void startGame() {
		super.startGame();
	}
	
	@Override
	public void updateGame() {
		super.updateGame();
		
		if(guardian == null || guardian.isDead()) {
			guardianRespawnTime++;
			if(guardianRespawnTime > MAX_GUARDIAN_RESPAWN_TIME) {
				guardianRespawnTime = 0;
				Location loc = new Location(artifact.getStartPosition().getWorld(), artifact.getStartPosition().getX(), artifact.getStartPosition().getY() + 2.0D, artifact.getStartPosition().getZ());
				guardian = (IronGolem) artifact.getStartPosition().getWorld().spawnEntity(loc, EntityType.IRON_GOLEM);
				guardian.setCustomName(ChatColor.BOLD + "" + ChatColor.GOLD + "Guardian");
				guardian.setCustomNameVisible(true);
				guardian.setMaxHealth(200.0D);
				guardian.setHealth(200.0D);
				guardian.setFireTicks(10000);
				guardian.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10000, 4, true), true);
				guardian.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 10000, 0, true), true);
				guardian.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 10000, 3, true), true);
				guardian.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 10000, 0, true), true);
				guardian.setRemoveWhenFarAway(false);
			}
		} else {
			if(artifact.isBeingCarried() && !artifact.getArtifactHolder().getPlayer().equals(guardian.getTarget())) {
				guardian.setTarget(artifact.getArtifactHolder().getPlayer());
			}
			/*if(guardian.getTarget() == null) {
				// If there is no active target for this entity
				List<Entity> list = guardian.getNearbyEntities(14.0D, 4.0D, 14.0D);
				if(artifact.isBeingCarried() && list.contains(artifact.getArtifactHolder().getPlayer())) {
					// If Artifact holder is in range, target them
					guardian.setTarget(artifact.getArtifactHolder().getPlayer());
				} else {
					// If not, then target anyone on the team of the Artifact holder
					if(artifact.isBeingCarried()) {
						ArrayList<Player> playerList = new ArrayList<Player>();
						for(Entity e : list) {
							if(e instanceof Player) {
								PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer((Player) e);
								if(pp.getCurrentTeam().equals(artifact.getArtifactHolder().getCurrentTeam())) {
									playerList.add((Player) e);
								}
							}
						}
						if(playerList.size() > 0) {
							guardian.setTarget(playerList.get(rand.nextInt(playerList.size())));
						} else {
							// If there are no players in range on the team of the Artifact holder
							if(guardian.getLocation().distanceSquared(artifact.getStartPosition()) > 16.0D) {
								guardian.teleport(artifact.getStartPosition().add(0.0D, 2.0D, 0.0D));
							}
						}
					} else {
						// If there are no players in range on the team of the Artifact holder
						if(guardian.getLocation().distanceSquared(artifact.getStartPosition()) > 16.0D) {
							guardian.teleport(artifact.getStartPosition().add(0.0D, 2.0D, 0.0D));
						}
					}
				}
			}*/
		}
		
		//Update Artifact Logic
		if(artifact.isOnGround()) {
			ArrayList<PlayerProfile> players = PrimeCraftMain.instance.game.getPlayers();
			Player closestPlayer = null;
			double shortestDistance = artifactCaptureRange;
			for(PlayerProfile pp : players) {
				if(!pp.getPlayer().isDead() && !pp.getCurrentTeam().getTeamDetails().equals(TeamDetails.Neutral) && !isPlayerArtifactHolder(pp)) {
					if(artifact.getBlock().getWorld().equals(pp.getPlayer().getWorld())) {
						double distanceToPlayer = artifact.getBlock().getLocation().distance(pp.getPlayer().getLocation());
						if(distanceToPlayer < artifactCaptureRange) {
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
				artifact.pickupArtifact(pp);
				Bukkit.getServer().broadcastMessage(pp.getFriendlyName() + ChatColor.GOLD + " has taken the Artifact.");
				PrimeUtility.spawnFireworks(pp.getLocation(), Color.SILVER, 4, 4.0F);
				PrimeUtility.spawnFireworks(pp.getLocation(), Color.ORANGE, 4, 4.0F);
			}
		}
		
		if(artifact.isBeingCarried()) {
			artifact.timeOnGround = -1;
			if(!artifact.getArtifactHolder().getPlayer().isOnline() || artifact.getArtifactHolder().getPlayer().isDead()) {
				artifact.dropArtifact();
			}
		}
		
		if(artifact.isBeingCarried()) {
			if(gameTicker % 3 == 0) {
				artifact.getArtifactHolder().getPlayer().getWorld().playEffect(artifact.getArtifactHolder().getPlayer().getLocation().add(0.0D, 1.0D, 0.0D), Effect.HAPPY_VILLAGER, 0);
			}
			if(artifact.getCurrentLocation().distanceSquared(artifact.getStartPosition()) > 625.0D) {
				if(artifactPotency != 0) {
					artifact.getArtifactHolder().getPlayer().sendMessage(ChatColor.DARK_RED + "The Artifact's power is nearly gone, your soul is bound to the center.");
					artifactPotency = 0;
				}
			} else if(artifact.getCurrentLocation().distanceSquared(artifact.getStartPosition()) > 225.0D) {
				if(artifactPotency != 1) {
					artifact.getArtifactHolder().getPlayer().sendMessage(ChatColor.DARK_RED + "You feel the Artifact's power lessen as you leave the center.");
					artifactPotency = 1;
				}
			} else {
				if(artifactPotency != 2) {
					artifact.getArtifactHolder().getPlayer().sendMessage(ChatColor.DARK_RED + "You feel the Artifact's power increase as you approach the center.");
					artifactPotency = 2;
				}
			}
		}
		
		if(artifact.timeOnGround / 10 == artifactRespawnTime) {
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "The Artifact has been reset.");
			SoundManager.playTeamFlagReset(TeamDetails.Neutral);
			artifact.resetArtifact();
		}
		
		if(artifact.timeOnGround % 10 == 0) {
			int timeLeft = (artifactRespawnTime - (artifact.timeOnGround / 10));
			if(timeLeft % 5 == 0 || timeLeft < 5) {
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + timeLeft + " seconds until the Artifact resets.");
			}
		}
		
		if(artifact.isOnGround() && !artifact.isAtBase()) {
			artifact.timeOnGround++;
		}
		
		artifact.updateLocation();
		
		//Check for scoring conditions
		if(gameTicker % 25 == 0) {
			if(artifact.isBeingCarried()) {
				artifact.getArtifactHolder().getCurrentTeam().creditPoints(artifactPotency);
				incrementArtifactPointCount(artifact.getArtifactHolder().getCurrentTeam().getTeamDetails(), artifactPotency);
				if(artifactPotency > 0) {
					artifact.getArtifactHolder().carriedArtifact(artifactPotency);
				} else {
					artifact.getArtifactHolder().getPlayer().sendMessage(ChatColor.GRAY + "The Artifact is too weak to gain points...");
				}
				if(artifact.getArtifactHolder().getCurrentTeam().getTeamPoints() % 10 == 0) {
					broadcastTeamScores();
				}
				if(artifactRespawnPointCount.get(artifact.getArtifactHolder().getCurrentTeam().getTeamDetails()) % 25 == 0 && artifact.getArtifactHolder().getCurrentTeam().getTeamPoints() != maxPoints) {
					artifact.resetArtifact();
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "The Artifact's power is diminished, it must reset.");
					SoundManager.playTeamFlagReset(TeamDetails.Neutral);
				}
			}
		}
	}

	private void incrementArtifactPointCount(TeamDetails teamDetails, int incrementBy) {
		int points = 0;
		if(artifactRespawnPointCount.containsKey(teamDetails)) {
			points = artifactRespawnPointCount.get(teamDetails);
		}
		artifactRespawnPointCount.put(teamDetails, points + incrementBy);
	}

	public boolean isPlayerArtifactHolder(PlayerProfile pp) {
		return pp.equals(artifact.getArtifactHolder());
	}
	
	@Override
	public void pauseGame() {
		super.pauseGame();
		
		artifact.resetArtifact();
	}

	@Override
	public void endGame() {
		super.endGame();

		if(guardian != null && !guardian.isDead()) {
			guardian.setHealth(0.0D);
		}

		artifact.resetArtifact();
	}

	@Override
	public void onPlayerDeath(Player player, Player killer) {
		if(guardian != null && !guardian.isDead() && player.equals(guardian.getTarget())) {
			guardian.setTarget(null);
		}
	}
	
	@Override
	public boolean onPlayerTeleport(Player player, TeleportCause cause) {
		if(!cause.equals(TeleportCause.UNKNOWN)) {
			if(PrimeCraftMain.instance.game.getProfileForPlayer(player).equals(artifact.getArtifactHolder())) {
				artifact.dropArtifact();
			}
		}
		return true;
	}

	@Override
	public void onButtonPress(Block pressedBlock, PlayerProfile playerPressing) {
	}
	
	@Override
	protected int getMaxPoints() {
		return 200;
	}
}
