package com.sonicjumper.primecraft.main.listeners;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.deathmessages.DeathMessage;
import com.sonicjumper.primecraft.main.deathmessages.DeathMessage.DeathType;
import com.sonicjumper.primecraft.main.deathmessages.DeathMessageHandler;
import com.sonicjumper.primecraft.main.eventbag.RandomEvent;
import com.sonicjumper.primecraft.main.gamemode.CapturePoint;
import com.sonicjumper.primecraft.main.gamemode.CapturePoint.CaptureLevel;
import com.sonicjumper.primecraft.main.gamemode.GamemodeArtifact;
import com.sonicjumper.primecraft.main.gamemode.GamemodeCommandPost;
import com.sonicjumper.primecraft.main.gamemode.SpawnGate;
import com.sonicjumper.primecraft.main.gamemode.SpawnGate.GateType;
import com.sonicjumper.primecraft.main.parkour.ParkourManager;
import com.sonicjumper.primecraft.main.player.MoneyItem;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.player.Prize;
import com.sonicjumper.primecraft.main.shop.ShopItem;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.util.PrimeUtility;
import com.sonicjumper.primecraft.main.weapons.CriticalArrow;
import com.sonicjumper.primecraft.main.weapons.Mine;
import com.sonicjumper.primecraft.main.world.PlayerWorld;
import com.sonicjumper.primecraft.main.world.Voter;
import com.sonicjumper.primecraft.main.world.WorldDetails;

public class PlayerActionListener implements Listener {
	private Random rand = new Random();

	public PlayerActionListener() {}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDroppedExp(0);
		event.getDrops().clear();
		PlayerProfile killer = PrimeCraftMain.instance.game.getProfileForPlayer(event.getEntity().getKiller());
		PlayerProfile playerKilled = PrimeCraftMain.instance.game.getProfileForPlayer(event.getEntity());
		playerKilled.getPlayerStats().addDeath();
		if(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
			if(e.getDamager() instanceof TNTPrimed) {
				TNTPrimed tnt = (TNTPrimed) e.getDamager();
				Mine mine = PrimeCraftMain.instance.game.weaponHandler.getOwnerOfExplosion(tnt);
				if(mine != null) {
					killer = mine.getOwner();
				}
			}
			if(e.getDamager() instanceof LightningStrike) {
				LightningStrike ls = (LightningStrike) e.getDamager();
				killer = PrimeCraftMain.instance.game.weaponHandler.getOwnerOfLightning(ls);
			}
		}
		if(killer == null && playerKilled.getDamagerCount() == 1) {
			killer = playerKilled.getDamager(0);
		}
		if(killer != null) {
			killer.getPlayerStats().addKill();
			String msg = "killed";
			ArrayList<DeathMessage> messages = DeathMessageHandler.getEligibleDeathMessages(event.getEntity().getLastDamageCause().getCause(), DeathType.PvP, playerKilled, killer);
			if(!messages.isEmpty()) {
				msg = messages.get(rand.nextInt(messages.size())).getMessage();
			}
			if(msg.contains("%a")) {
				String assisterString = "";
				ArrayList<PlayerProfile> assistList = playerKilled.getDamagersIgnore(killer);
				if(assistList.size() > 1) {
					for(int i = 0; i < assistList.size(); i++) {
						PlayerProfile pp = assistList.get(i);
						if(i + 1 != assistList.size()) {
							assisterString = assisterString + pp.getFriendlyName() + ", ";
						} else {
							assisterString = assisterString + "and " + pp.getFriendlyName();
						}
					}
				} else {
					assisterString = assistList.get(0).getFriendlyName();
				}
				msg = msg.replace("%a", assisterString);
			}
			if(msg.contains("%p")) {
				msg = msg.replace("%p", playerKilled.getFriendlyName());
			} else {
				msg = msg + " " + playerKilled.getFriendlyName();
			}
			event.setDeathMessage(killer.getFriendlyName() + " " + msg);
			killer.playerKilled(PrimeCraftMain.instance.game.getProfileForPlayer(event.getEntity()));
			PrimeCraftMain.instance.game.currentGamemode.onPlayerDeath(playerKilled.getPlayer(), killer.getPlayer());
		} else {
			String msg = "died";
			ArrayList<DeathMessage> messages = DeathMessageHandler.getEligibleDeathMessages(event.getEntity().getLastDamageCause().getCause(), DeathType.Natural, playerKilled, killer);
			if(!messages.isEmpty()) {
				msg = messages.get(rand.nextInt(messages.size())).getMessage();
			}
			event.setDeathMessage(playerKilled.getFriendlyName() + ChatColor.WHITE + " " + msg);
			for(int i = 0; i < playerKilled.getDamagerCount(); i++) {
				PlayerProfile assister = playerKilled.getDamager(i);
				assister.playerAssistKilled(null, playerKilled, playerKilled.getDamagerCount());
			}
			PrimeCraftMain.instance.game.currentGamemode.onPlayerDeath(playerKilled.getPlayer(), null);
			playerKilled.playerDied(playerKilled);
		}
		PrimeCraftMain.instance.game.broadcastDeathMessage(killer, playerKilled, event.getDeathMessage());
		event.setDeathMessage("");
	}
	
	@EventHandler
	public void onPlayerShootArrow(EntityShootBowEvent event) {
		if(event.getBow().getItemMeta().hasDisplayName() && event.getBow().getItemMeta().getDisplayName().contains("Artemis Bow") && event.getEntity() instanceof Player) {
			CriticalArrow arrow = new CriticalArrow((Arrow) event.getProjectile(), PrimeCraftMain.instance.game.getProfileForPlayer((Player) event.getEntity()));
			PrimeCraftMain.instance.game.weaponHandler.addSpecialArrow(arrow);
		}
	}

	@EventHandler
	public void onPlayerSneak(PlayerToggleSneakEvent event) {
		PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
		if(event.isSneaking()) {
			if(PrimeCraftMain.instance.game.currentGamemode instanceof GamemodeArtifact) {
				GamemodeArtifact gamemodeArtifact = (GamemodeArtifact) PrimeCraftMain.instance.game.currentGamemode;
				if(gamemodeArtifact.isPlayerArtifactHolder(pp)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamagedByEntity(EntityDamageByEntityEvent event) {
		if(event.getEntity() instanceof Player) {
			PlayerProfile damagedPlayer = PrimeCraftMain.instance.game.getProfileForPlayer((Player) event.getEntity());
			if(event.getDamager() instanceof Player) {
				PlayerProfile damager = PrimeCraftMain.instance.game.getProfileForPlayer((Player) event.getDamager());
				if(!damager.getCurrentTeam().getTeamDetails().isPlayingTeam()) {
					event.setCancelled(true);
					damager.getPlayer().sendMessage("You cannot damage players outside of the game.");
				} else if(damagedPlayer.getCurrentTeam() != null && damagedPlayer.getCurrentTeam().isPlayerOnTeam(damager))  {
					event.setCancelled(true);
					damager.getPlayer().sendMessage("That player is on your team.");
				}
				if(!event.isCancelled() && damager != null) {
					/*if(damagedPlayer.getPlayer().isBlocking()) {
						ItemStack sword = damagedPlayer.getPlayer().getItemInHand();
						sword.setDurability((short) (sword.getDurability() + (sword.getType().getMaxDurability() / 4)));
						if(sword.getDurability() > sword.getType().getMaxDurability()) {
							damagedPlayer.getPlayer().setItemInHand(null);
							damagedPlayer.getPlayer().getWorld().playEffect(damagedPlayer.getPlayer().getLocation(), Effect.ITEM_BREAK, sword.getTypeId());
						}
					}*/
					if(damagedPlayer.getTimeSinceInSpawn() <= 13) {
						damager.addSpawnKillPoint();
						damager.addSpawnKillPoint();
					}
					damagedPlayer.addDamager(damager);
					damager.getPlayerStats().addDamageDealt(event.getDamage());
				}
			}
			if(event.getDamager() instanceof Arrow) {
				Arrow a = ((Arrow) event.getDamager());
				if(RandomEvent.isArrowInstaKill(a)) {
					event.setDamage(200.0D);
				}
				if(a.getShooter() instanceof Player) {
					PlayerProfile damager = PrimeCraftMain.instance.game.getProfileForPlayer((Player) a.getShooter());
					if(damagedPlayer.getCurrentTeam() != null && damagedPlayer.getCurrentTeam().isPlayerOnTeam(damager))  {
						event.setCancelled(true);
						damager.getPlayer().sendMessage(ChatColor.GRAY + "That player is on your team.");
					} else {
						// Check for special arrows and headshots
						if(PrimeCraftMain.instance.game.weaponHandler.isSpecialArrow(a)) {
							PrimeCraftMain.instance.game.weaponHandler.getSpecialArrow(a).onHitEntity(damagedPlayer.getPlayer());
						}
						if(Math.abs(damagedPlayer.getPlayer().getEyeLocation().getY() - a.getLocation().getY()) < 0.5D) {
							damager.getPlayer().sendMessage(ChatColor.DARK_GRAY + "You got a headshot and dealt double damage!");
							event.setDamage(event.getDamage() * 2.0D);
						}
						damagedPlayer.addDamager(damager);
					}
					if(!event.isCancelled() && damager != null) {
						damager.getPlayerStats().addDamageDealt(event.getDamage());
					}
				}
			}
			if(event.getDamager() instanceof TNTPrimed) {
				TNTPrimed tnt = (TNTPrimed) event.getDamager();
				Mine mine = PrimeCraftMain.instance.game.weaponHandler.getOwnerOfExplosion(tnt);
				if(mine != null) {
					if(mine.getAlliance().getTeamObject().isPlayerOnTeam(damagedPlayer) && !mine.getOwner().equals(damagedPlayer)) {
						event.setCancelled(true);
					} else {
						event.setDamage(event.getDamage() * 7.5D);
						if(!mine.getOwner().equals(damagedPlayer)) {
							damagedPlayer.addDamager(mine.getOwner());
						}
					}
					if(!event.isCancelled()) {
						mine.getOwner().getPlayerStats().addDamageDealt(event.getDamage());
					}
				}
			}
			if(event.getDamager() instanceof LightningStrike) {
				LightningStrike ls = (LightningStrike) event.getDamager();
				PlayerProfile lightningOwner = PrimeCraftMain.instance.game.weaponHandler.getOwnerOfLightning(ls);
				if(lightningOwner != null) {
					if(lightningOwner.getCurrentTeam().isPlayerOnTeam(damagedPlayer) && !lightningOwner.equals(damagedPlayer)) {
						event.setCancelled(true);
					} else {
						event.setDamage(event.getDamage() * 2.5D);
						if(damagedPlayer.getPlayer().isBlocking()) {
							event.setDamage(event.getDamage() / 2.0D);
						}
						if(!lightningOwner.equals(damagedPlayer)) {
							damagedPlayer.addDamager(lightningOwner);
						}
					}
					if(!event.isCancelled()) {
						lightningOwner.getPlayerStats().addDamageDealt(event.getDamage());
					}
				} else {
					damagedPlayer.getPlayer().setHealth(1.0D);
					event.setDamage(0.0D);
				}
			}
			if(event.getDamager() instanceof EnderPearl) {
				event.setDamage(event.getDamage() / 5.0D);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamagedByExplosion(EntityDamageByBlockEvent event) {
		if(event.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDamageInLobby(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer((Player) event.getEntity());
			if(!pp.getCurrentTeam().getTeamDetails().isPlayingTeam()) {
				event.setCancelled(true);
			}
			if(!event.isCancelled() && (event.getCause().equals(DamageCause.ENTITY_ATTACK) || event.getCause().equals(DamageCause.PROJECTILE))) {
				pp.getPlayerStats().addDamageTaken(event.getDamage());
			}
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(PrimeCraftMain.instance.game.isMapLoaded) {
			PrimeCraftMain.instance.game.currentGamemode.onPlayerTeleport(event.getPlayer(), event.getCause());
		}
	}

	@EventHandler
	public void onFoodDecayInLobby(FoodLevelChangeEvent event) {
		if(event.getEntity() instanceof Player) {
			PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer((Player) event.getEntity());
			if(!pp.getCurrentTeam().getTeamDetails().isPlayingTeam()) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
		if(pp.isRespawning()) {
			pp.setIsRespawning(false);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
		event.setRespawnLocation(pp.getCurrentTeam().getSpawn());
		pp.refreshPlayerGear();
	}

	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		PrimeCraftMain.instance.game.registerPlayer(player);
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20.0D);
		player.setFoodLevel(20);
		final PlayerProfile playerProf = PrimeCraftMain.instance.game.getProfileForPlayer(player);
		Bukkit.getScheduler().scheduleSyncDelayedTask(PrimeCraftMain.instance, new Runnable() {
			PlayerProfile pp = playerProf;
			
			@Override
			public void run() {
				if(Voter.getVoter().isVotingOpen && PrimeCraftMain.instance.game.mapsForVoteSession != null) {
					pp.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "Voting started. Vote for maps with /vote #");
					for(int i = 0; i < PrimeCraftMain.instance.game.mapsForVoteSession.size(); i++) {
						PlayerWorld pw = PrimeCraftMain.instance.game.mapsForVoteSession.get(i);
						pp.getPlayer().sendMessage(ChatColor.GREEN + "#" + (i + 1) + " " + ChatColor.AQUA + pw.getWorldName() + ": " + pw.getDescription());
					}
				}
				if(TeamDetails.Neutral.getTeamObject().getSpawn() != null) {
					if(!pp.getPlayer().getWorld().getName().equalsIgnoreCase(TeamDetails.Neutral.getTeamObject().getSpawn().getWorld().getName())) {
						TeamDetails.Neutral.getTeamObject().teleportPlayerToSpawn(pp);
						pp.getPlayer().sendMessage(ChatColor.GRAY + "Oops, you logged in to the wrong world! No need to worry, I'm fixing it now.");
					}
				}
			}
		}, 30);
	}

	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent event) {
		PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(event.getPlayer().getName());
		pp.saveToConfig();
		pp.onLogout();
		PrimeCraftMain.instance.game.unassignPlayer(pp);
		PrimeCraftMain.instance.game.unregisterPlayer(pp);
	}
	
	@EventHandler
	public void onPlayerPunchFire(PlayerInteractEvent event) {
		if(event.getClickedBlock() != null && (event.getClickedBlock().getRelative(BlockFace.UP).getType().equals(Material.FIRE) || event.getClickedBlock().getRelative(BlockFace.NORTH).getType().equals(Material.FIRE) || event.getClickedBlock().getRelative(BlockFace.SOUTH).getType().equals(Material.FIRE) || event.getClickedBlock().getRelative(BlockFace.WEST).getType().equals(Material.FIRE) || event.getClickedBlock().getRelative(BlockFace.EAST).getType().equals(Material.FIRE)) && !event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerCycleItems(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
			pp.cycleItemInHand();
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
			if(event.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
				if(PrimeCraftMain.instance.game.isMapLoaded) {
					if(sign.getLine(0) != null && sign.getLine(0).equalsIgnoreCase("[Join Game]")) {
						if(pp.getPlayer().getGameMode().equals(GameMode.ADVENTURE)) {
							pp.getPlayer().setGameMode(GameMode.SURVIVAL);
						}
						PrimeCraftMain.instance.game.assignPlayer(pp);
					}
				}
				if(sign.getLine(0) != null && sign.getLine(0).equalsIgnoreCase("[Tier]")) {
					pp.tierPlayer();
				}
				if(sign.getLine(0) != null && sign.getLine(0).equalsIgnoreCase("[Rebirth]")) {
					pp.raiseEchelonForPlayer();
				}
				if(sign.getLine(0) != null && sign.getLine(0).contains("[CP Teleport")) {
					if(PrimeCraftMain.instance.game.currentGamemode instanceof GamemodeCommandPost) {
						GamemodeCommandPost gamemode = (GamemodeCommandPost) PrimeCraftMain.instance.game.currentGamemode;
						int pointID = 0;
						try {
							String number = sign.getLine(0).replaceAll("\\D+", "");
							pointID = Integer.parseInt(number) - 1;
						} catch(NumberFormatException e) {
							PrimeCraftMain.instance.getLogger().info("Had an issue with reading a TP sign... " + sign.getWorld().getName() + "/" + sign.getX() + "/" + sign.getY() + "/" + sign.getZ());
						}
						CapturePoint cp = gamemode.capturePoints.get(pointID);
						if(cp.getAlliance().equals(pp.getCurrentTeam().getTeamDetails()) && cp.getCapturePointScoreValue() >= 3 && !PrimeCraftMain.instance.game.isPaused() && PrimeCraftMain.instance.game.isStarted()) {
							Location tpLocation = PrimeUtility.getRandomNearbyBlock(cp.getLocation(), Material.AIR, 4, true).getLocation();
							if(tpLocation != null) {
								pp.getPlayer().teleport(tpLocation);
								pp.getPlayer().sendMessage(ChatColor.DARK_RED + "You have been teleported to Command Post " + (pointID + 1));
							} else {
								pp.getPlayer().sendMessage(ChatColor.DARK_RED + "The Command Post seems to be blocked by something...");
							}
						} else {
							if(PrimeCraftMain.instance.game.isPaused()) {
								pp.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot teleport to that Command Post, the game is paused.");
							} else if(!cp.getAlliance().equals(pp.getCurrentTeam().getTeamDetails())) {
								pp.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot teleport to that Command Post, your team does not own it.");
							} else if(cp.getCapturePointScoreValue() < 3) {
								pp.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot teleport to that Command Post, it is not fully captured.");
							} else if(!PrimeCraftMain.instance.game.isStarted()) {
								pp.getPlayer().sendMessage(ChatColor.DARK_RED + "You cannot teleport to that Command Post, the game hasn't started.");
							}
						}
					}
				}
			}
		}
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(event.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
				Sign sign = (Sign) event.getClickedBlock().getState();
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
				if(sign.getLine(0) != null && sign.getLine(0).contains("[Prize")) {
					try {
						Integer prizeAmount = Integer.parseInt(sign.getLine(0).substring(sign.getLine(0).indexOf(":") + 1, sign.getLine(0).indexOf("]")));
						Integer prizeId = Prize.getPrizeIdForString(sign.getLine(1));
						if(!pp.getPlayerStats().doesHavePrize(prizeId)) {
							pp.getPlayerStats().addPrize(prizeId);
							pp.giveMoney(prizeAmount);
						} else {
							pp.getPlayer().sendMessage(ChatColor.GRAY + "You have already earned this prize.");
						}
					} catch(Exception e) {
						PrimeCraftMain.instance.getLogger().warning("There was a problem giving a player a prize: " + pp.getPlayer().getDisplayName() + "," + sign.getLine(0) + "," + sign.getLine(1));
						e.printStackTrace();
					}
				}
				if(sign.getLine(0) != null && sign.getLine(0).contains("Finished Level")) {
					String delims[] = sign.getLine(1).split(":");
					String courseName = delims[0];
					int levelID = Integer.parseInt(delims[1]);
					pp.completedParkourLevel(courseName, ParkourManager.getParkourLevelForNumAndCourse(courseName, levelID));
					ParkourManager.teleportPlayerToParkour(courseName, pp);
				}
				if(sign.getLine(0) != null) {
					ShopItem si = ShopItem.getShopItemForName(sign.getLine(0));
					if(si != null) {
						PrimeCraftMain.instance.getShopHandler().handleShopSign(si, pp);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteractCapturePoint(PlayerInteractEvent event) {
		if(event.getItem() != null && event.getItem().getItemMeta().hasDisplayName()) {
			CapturePoint cp = null;
			String[] itemData = event.getItem().getItemMeta().getDisplayName().split(" ");
			if(itemData.length == 3 && itemData[0].equalsIgnoreCase("capturepointtool")) {
				CaptureLevel cl = CaptureLevel.getLevelForName(itemData[1]);
				int cpID = -1;
				try {
					cpID = Integer.parseInt(itemData[2]);
				} catch(NumberFormatException e) {
					
				}
				if(cl != null && cpID != -1) {
					cp = new CapturePoint(WorldDetails.getCapturePointLocation(event.getPlayer().getWorld().getName(), cpID), cpID);
					if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
						if(event.getClickedBlock().getType().equals(Material.WALL_SIGN)) {
							if(event.getPlayer().isSneaking()) {
								TeamDetails teamOfLocation = null;
								for(ProtectedRegion pr : PrimeCraftMain.instance.game.worldGuard.getRegionManager(event.getPlayer().getWorld()).getApplicableRegions(event.getPlayer().getLocation())) {
									for(TeamDetails team : TeamDetails.values()) {
										if(pr.getId().toLowerCase().contains(team.name().toLowerCase())) {
											teamOfLocation = team;
										}
									}
								}
								if(teamOfLocation != null) {
									cp.setTeleportingSign(teamOfLocation, event.getClickedBlock());
									event.getPlayer().sendMessage(event.getClickedBlock().getType().name() + " was set to the Command Post teleporting sign for " + teamOfLocation.name() + " team.");
								} else {
									event.getPlayer().sendMessage("You are not in any spawn regions, so the team of this teleport sign cannot be determined.");
								}
							} else {
								cp.setInfoSign(event.getClickedBlock());
								event.getPlayer().sendMessage(event.getClickedBlock().getType().name() + " was set to the Command Post info sign.");
							}
						} else {
							if(cp.addBlockToList(event.getClickedBlock(), cl.levelInt)) {
								event.getPlayer().sendMessage(event.getClickedBlock().getType().name() + " was added to Command Post " + cpID + " with strength " + cl + ".");
							}
						}
					}
					if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
						if(cp.removeBlockFromList(event.getClickedBlock())) {
							event.getPlayer().sendMessage(event.getClickedBlock().getType().name() + " was removed from Command Post " + cpID + ".");
						}
					}
					cp.saveToConfig(event.getPlayer().getWorld().getName());
				} else {
					event.getPlayer().sendMessage("Something went wrong:");
					event.getPlayer().sendMessage(String.valueOf(cl));
					event.getPlayer().sendMessage(String.valueOf(cpID));
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractSpawnGate(PlayerInteractEvent event) {
		if(event.getItem() != null && event.getItem().getItemMeta().hasDisplayName()) {
			SpawnGate sg = null;
			if(event.getItem().getItemMeta().getDisplayName().contains("Respawn")) {
				if(event.getItem().getItemMeta().getDisplayName().contains("Blue")) {
					sg = new SpawnGate(TeamDetails.Blue, event.getPlayer().getWorld().getName(), GateType.Respawn);
				}
				if(event.getItem().getItemMeta().getDisplayName().contains("Red")) {
					sg = new SpawnGate(TeamDetails.Red, event.getPlayer().getWorld().getName(), GateType.Respawn);
				}
				if(event.getItem().getItemMeta().getDisplayName().contains("Yellow")) {
					sg = new SpawnGate(TeamDetails.Yellow, event.getPlayer().getWorld().getName(), GateType.Respawn);
				}
				if(event.getItem().getItemMeta().getDisplayName().contains("Purple")) {
					sg = new SpawnGate(TeamDetails.Purple, event.getPlayer().getWorld().getName(), GateType.Respawn);
				}
			}
			if(event.getItem().getItemMeta().getDisplayName().contains("Game")) {
				if(event.getItem().getItemMeta().getDisplayName().contains("Blue")) {
					sg = new SpawnGate(TeamDetails.Blue, event.getPlayer().getWorld().getName(), GateType.Game);
				}
				if(event.getItem().getItemMeta().getDisplayName().contains("Red")) {
					sg = new SpawnGate(TeamDetails.Red, event.getPlayer().getWorld().getName(), GateType.Game);
				}
				if(event.getItem().getItemMeta().getDisplayName().contains("Yellow")) {
					sg = new SpawnGate(TeamDetails.Yellow, event.getPlayer().getWorld().getName(), GateType.Game);
				}
				if(event.getItem().getItemMeta().getDisplayName().contains("Purple")) {
					sg = new SpawnGate(TeamDetails.Purple, event.getPlayer().getWorld().getName(), GateType.Game);
				}
			}
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if(sg != null && sg.addBlockToList(event.getClickedBlock())) {
					sg.saveToConfig(event.getPlayer().getWorld().getName());
					event.getPlayer().sendMessage(event.getClickedBlock().getType().name() + " was added to " + sg.getTeamDetails().name() + " " + sg.getGateType().name() + " Gate list");
				}
			}
			if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
				if(sg != null && sg.removeBlockFromList(event.getClickedBlock())) {
					sg.saveToConfig(event.getPlayer().getWorld().getName());
					event.getPlayer().sendMessage(event.getClickedBlock().getType().name() + " was removed from " + sg.getTeamDetails().name() + " " + sg.getGateType().name() + " Gate list");
				}
			}
		}
	}

	@EventHandler
	public void onPlayerChangeInventory(InventoryClickEvent event) {
		if(event.getWhoClicked().getInventory().equals(event.getClickedInventory()) && !event.getWhoClicked().isOp() && !event.getWhoClicked().getGameMode().equals(GameMode.CREATIVE)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(!event.getPlayer().isOp()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(event.getItem().getItemStack().getType().equals(Material.ARROW)) {
			ItemStack playerArrowStack = null;
			for(ItemStack is : event.getPlayer().getInventory().getContents()) {
				if(is != null) {
					if(is.getType().equals(Material.ARROW) && ((is.getItemMeta().hasDisplayName() && is.getItemMeta().getDisplayName().equalsIgnoreCase("Arrow")) || !is.getItemMeta().hasDisplayName())) {
						playerArrowStack = is;
					}
				}
			}
			if(playerArrowStack != null) {
				playerArrowStack.setAmount(playerArrowStack.getAmount() + event.getItem().getItemStack().getAmount());
			} else {
				if(event.getPlayer().getInventory().getItem(2) == null) {
					event.getPlayer().getInventory().setItem(2, event.getItem().getItemStack());
				} else if(event.getPlayer().getInventory().getItem(11) == null) {
					event.getPlayer().getInventory().setItem(11, event.getItem().getItemStack());
				} else if(event.getPlayer().getInventory().getItem(20) == null) {
					event.getPlayer().getInventory().setItem(20, event.getItem().getItemStack());
				} else if(event.getPlayer().getInventory().getItem(29) == null) {
					event.getPlayer().getInventory().setItem(29, event.getItem().getItemStack());
				}
			}
			event.getItem().remove();
			event.setCancelled(true);
		}
		if(MoneyItem.doesHaveValue(event.getItem().getItemStack().getType())) {
			PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(event.getPlayer());
			pp.pickupMoneyItem(event.getItem().getItemStack().getType());
			event.getItem().remove();
			event.setCancelled(true);
		}
	}
}
