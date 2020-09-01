package com.sonicjumper.primecraft.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sonicjumper.primecraft.main.effects.PlayerEffect;
import com.sonicjumper.primecraft.main.gamemode.CapturePoint;
import com.sonicjumper.primecraft.main.gamemode.CapturePoint.CaptureLevel;
import com.sonicjumper.primecraft.main.gamemode.Gamemode;
import com.sonicjumper.primecraft.main.gamemode.GamemodeCaptureTheFlag;
import com.sonicjumper.primecraft.main.parkour.ParkourManager;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.world.Voter;
import com.sonicjumper.primecraft.main.world.WorldDetails;

public class CommandHandler implements CommandExecutor {
	public CommandHandler() {}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Commands for every player
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("money")) {
				sender.sendMessage(ChatColor.GREEN + "Money Balance: " + ChatColor.WHITE + PrimeCraftMain.instance.game.getProfileForPlayer((Player) sender).getMoneyAmount() + " coins.");
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("score")) {
				if(PrimeCraftMain.instance.game.currentGamemode != null) {
					sender.sendMessage(PrimeCraftMain.instance.game.currentGamemode.getScore());
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("spawn")) {
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer((Player) sender);
				if(PrimeCraftMain.instance.game.isMapLoaded && !TeamDetails.Neutral.getTeamObject().isPlayerOnTeam(pp)) {
					sender.sendMessage("Moving to spawn... Stay still for 10 seconds.");
					pp.setIsRespawning(true);
					PrimeCraftMain.instance.playerRespawner.registerPlayerForRespawn(pp);
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("flag")) {
				if(PrimeCraftMain.instance.game.currentGamemode instanceof GamemodeCaptureTheFlag) {
					GamemodeCaptureTheFlag ctf = (GamemodeCaptureTheFlag) PrimeCraftMain.instance.game.currentGamemode;
					sender.sendMessage(ctf.getFlagPositions());
				} else {
					sender.sendMessage("You can only use this during a Capture The Flag game.");
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("stats")) {
				if(args.length > 0) {
					PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(args[0]);
					if(pp != null) {
						sender.sendMessage(pp.getPlayerStats().getStatsMessage());
					} else {
						sender.sendMessage("Invalid Player name");
					}
				} else {
					sender.sendMessage("Please enter Player's name");
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("parkour")) {
				if(args.length > 0) {
					if(Voter.getVoter().isVotingOpen) {
						PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
						PrimeCraftMain.instance.game.unassignPlayer(pp);
						ParkourManager.teleportPlayerToParkour(args[0], pp);
					} else {
						sender.sendMessage(ChatColor.GRAY + "You cannot go to the Parkour arenas now; wait for Intermission.");
					}
				} else {
					sender.sendMessage(ChatColor.GRAY + "Please include a course name. Available courses:");
					for(String courseName : ParkourManager.getUniqueCourseNames()) {
						sender.sendMessage(ChatColor.GRAY + courseName);
					}
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("vote")) {
				if(Voter.getVoter().isVotingOpen) {
					if(args.length > 0) {
						try {
							Integer vote = Integer.parseInt(args[0]);
							if(vote != 0 && vote < PrimeCraftMain.instance.game.MAPS_PER_VOTE_SESSION + 1) {
								if(Voter.getVoter().playerVoteForMap(sender.getName(), vote) != null) {
									sender.sendMessage("You changed your vote to map #" + vote);
								} else {
									sender.sendMessage("You voted for map #" + vote);
								}
							} else {
								sender.sendMessage("Please enter a listed map number.");
							}
						} catch(NumberFormatException e) {
							sender.sendMessage("Please enter a valid map number.");
						}
					} else {
						sender.sendMessage("Please enter a map number.");
					}
				} else {
					sender.sendMessage("Voting is not open now.");
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("team")) {
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
				sender.sendMessage("You are on " + pp.getCurrentTeam().getTeamDetails().getChatColor() + pp.getCurrentTeam().getTeamDetails().name() + ChatColor.WHITE + " team.");
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("teamchat")) {
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
				if(pp.isUsingTeamChat()) {
					pp.setUsingTeamChat(false);
					sender.sendMessage(ChatColor.GRAY + "Team chat toggled off.");
				} else {
					pp.setUsingTeamChat(true);
					sender.sendMessage(pp.getCurrentTeam().getTeamDetails().getChatColor() + "Team chat toggled on.");
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("deathmessages")) {
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
				if(pp.isUsingDeathMessages()) {
					pp.setUsingDeathMessages(false);
					sender.sendMessage(ChatColor.GRAY + "Public death messages toggled off. You will only receive death messages relating to you.");
				} else {
					pp.setUsingDeathMessages(true);
					sender.sendMessage(pp.getCurrentTeam().getTeamDetails().getChatColor() + "Death messages toggled on. You will see all death messages.");
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("togglesounds")) {
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
				if(pp.isUsingSounds()) {
					pp.setUsingSounds(false);
					sender.sendMessage(ChatColor.GRAY + "PrimeSounds toggled off. You will not hear special sound effects.");
				} else {
					pp.setUsingSounds(true);
					sender.sendMessage(ChatColor.GRAY + "PrimeSounds toggled on. You will now hear special sound effects.");
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("preferteam")) {
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
				if(args.length > 0) {
					if(pp.canPreferTeam()) {
						if(Voter.getVoter().isVotingOpen) {
							if(PrimeCraftMain.instance.game.getPlayers().size() / 2 > PrimeCraftMain.instance.game.preferredTeamList.size()) {
								TeamDetails td = TeamDetails.getDetailsForName(args[0]);
								if(td != null) {
									if(!PrimeCraftMain.instance.game.preferredTeamList.containsKey(pp)) {
										sender.sendMessage(ChatColor.GRAY + "You have successfully preferred the " + td.getChatColor() + td.name() + ChatColor.GRAY + " team.");
									} else {
										sender.sendMessage(ChatColor.GRAY + "You have changed your preference to the " + td.getChatColor() + td.name() + ChatColor.GRAY + " team.");
									}
									PrimeCraftMain.instance.game.preferredTeamList.put(pp, td);
								} else {
									sender.sendMessage(ChatColor.GRAY + "Please enter a playable team.");
								}
							} else {
								sender.sendMessage(ChatColor.GRAY + "The maximum amount of players who can prefer teams for this game has already been reached.");
							}
						} else {
							sender.sendMessage(ChatColor.GRAY + "You can only use this during Intermission.");
						}
					} else {
						sender.sendMessage(ChatColor.GRAY + "You cannot use this feature. It is a donator benefit only.");
					}
				} else {
					sender.sendMessage(ChatColor.GRAY + "Please provide a team name.");
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("toggleeffect")) {
				if(args.length > 0) {
					String effectName = "";
					for(int i = 0; i < args.length; i++) {
						effectName += args[i];
					}
					PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
					PlayerEffect pe = PlayerEffect.getEffectForName(effectName);
					if(pe != null) {
						if(pp.canUseEffect(pe)) {
							if(pp.disableEffect(pe)) {
								sender.sendMessage(ChatColor.GRAY + "You disabled " + effectName);
								pe.onDisable(pp);
							} else {
								sender.sendMessage(ChatColor.GRAY + "You enabled " + effectName);
								pe.onEnable(pp);
								pp.enableEffect(pe);
							}
						} else {
							sender.sendMessage(ChatColor.GRAY + "You cannot use that effect. Please contact an Admin if you believe this is an error.");
						}
					} else {
						sender.sendMessage(ChatColor.GRAY + "The effect, " + effectName + ", does not exist.");
					}
					return true;
				} else {
					sender.sendMessage(ChatColor.GRAY + "Provide an effect name.");
				}
			}
			if(cmd.getName().equalsIgnoreCase("listeffects")) {
				PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForPlayer(p);
				String message = "";
				for(int i = 0; i < pp.getAllowedEffects().size(); i++) {
					PlayerEffect pe = pp.getAllowedEffects().get(i);
					if(i + 1 != pp.getAllowedEffects().size()) {
						message += pe.getName() + ", ";
					} else {
						message += pe.getName();
					}
				}
				if(message.isEmpty()) {
					message = ChatColor.GRAY + "You do not have any effects. Use /donate to get some.";
				}
				p.sendMessage(ChatColor.GOLD + message);
				return true;
			}
		}
		//Op only commands
		if(sender.isOp()) {
			if(cmd.getName().equalsIgnoreCase("startgame")) {
				PrimeCraftMain.instance.game.initializeGame();
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("teamlist")) {
				if(args.length > 0) {
					if(args[0].equalsIgnoreCase("All")) {
						sender.sendMessage("All Players:" + PrimeCraftMain.instance.game.getPlayers().toString());
						return true;
					}
					TeamDetails td = TeamDetails.valueOf(args[0]);
					if(td != null) {
						sender.sendMessage(td.name() + " team:" + td.getTeamObject().getPlayerList().toString());
					} else {
						sender.sendMessage("Enter a valid team name");
					}
					return true;
				}
			}
			if(cmd.getName().equalsIgnoreCase("listWorlds")) {
				sender.sendMessage("Travelable Worlds:");
				for(String s : PrimeCraftMain.instance.game.worldManager.getTravelableWorlds()) {
					sender.sendMessage(s);
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("setplayerpreferteam")) {
				if(args.length > 1) {
					PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(args[0]);
					if(pp != null) {
						if(args[1].equals("1")) {
							pp.getPlayer().sendMessage(ChatColor.GOLD + "You may now use /preferteam <team> to pick your team during Intermission.");
							pp.setCanPreferTeam(true);
							sender.sendMessage("Allowed " + args[0] + " to prefer their team");
						} else {
							pp.getPlayer().sendMessage(ChatColor.GOLD + "You can no longer use /preferteam <team> to pick your team.");
							pp.setCanPreferTeam(false);
							sender.sendMessage("Disallowed " + args[0] + " to prefer their team");
						}
						return true;
					}
				}
			}
			if(cmd.getName().equalsIgnoreCase("givemoney")) {
				if(args.length > 0) {
					PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(args[0]);
					if(pp != null) {
						pp.giveMoney(Integer.parseInt(args[1]));
						sender.sendMessage("Gave " + args[1] + " coins to " + args[0]);
						return true;
					}
				}
			}
			if(cmd.getName().equalsIgnoreCase("settier")) {
				if(args.length > 0) {
					PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(args[0]);
					if(pp != null) {
						pp.setTier(Integer.parseInt(args[1]));
						sender.sendMessage("Set " + args[0] + " to tier " + args[1]);
						return true;
					}
				}
			}
			if(cmd.getName().equalsIgnoreCase("toggleplayereffect")) {
				if(args.length > 2) {
					PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(args[0]);
					if(pp != null) {
						PlayerEffect pe = PlayerEffect.getEffectForName(args[1]);
						if(pe != null) {
							if(args[2].equals("1")) {
								if(!pp.canUseEffect(pe)) {
									pp.addAllowedEffect(pe);
									pp.getPlayer().sendMessage(ChatColor.GOLD + "You may now use /toggleeffect " + args[1] + " to enable your effect.");
									sender.sendMessage(args[0] + " now has permission for " + args[1]);
								} else {
									sender.sendMessage(args[0] + " already has the effect, " + args[1]);
								}
							} else {
								if(pp.removeAllowedEffect(pe)) {
									pp.disableEffect(pe);
									pp.getPlayer().sendMessage(ChatColor.GOLD + "You may no longer use /toggleeffect " + args[1]);
									sender.sendMessage(args[0] + " has lost the permissions for " + args[1]);
								} else {
									sender.sendMessage(args[0] + " does not have the effect, " + args[1]);
								}
							}
						} else {
							sender.sendMessage("The effect, " + args[1] + ", does not exist.");
						}
					} else {
						sender.sendMessage("The player, " + args[0] + ", is not recognized.");
					}
					return true;
				}
			}
			if(cmd.getName().equalsIgnoreCase("resetloadout")) {
				if(args.length > 0) {
					PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(args[0]);
					if(pp != null) {
						pp.resetLoadout();
						sender.sendMessage("Reset player:" + args[0]);
						return true;
					}
				}
			}
			if(cmd.getName().equalsIgnoreCase("addScore")) {
				if(args.length > 1) {
					TeamDetails.valueOf(args[0]).getTeamObject().creditPoints(Integer.parseInt(args[1]));
					sender.sendMessage("Added " + Integer.parseInt(args[1]) + " points to team " + args[0]);
				} else {
					sender.sendMessage("Enter a valid team name and score.");
				}
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("forcesaveconfig")) {
				PrimeCraftMain.instance.saveAllConfigs();
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("enableworld")) {
				if(args.length > 4) {
					Gamemode g = Gamemode.getGamemodeForName(args[3]);
					int preferredTime = -1;
					try {
						preferredTime = Integer.parseInt(args[4]);
					} catch(NumberFormatException nfe) {
						
					}
					if(g != null) {
						PrimeCraftMain.instance.game.worldManager.enableNewWorld(args[0], args[1], args[2], g.getName(), preferredTime);
						sender.sendMessage("The world, " + args[0] + ", has been enabled.");
					} else {
						sender.sendMessage("The gamemode was not recognized. EX: CaptureTheFlag2Team");
						for(Gamemode gm : Gamemode.gamemodes) {
							sender.sendMessage(gm.getName());
						}
					}
					return true;
				}
			}
			if(cmd.getName().equalsIgnoreCase("disableworld")) {
				if(args.length > 0) {
					PrimeCraftMain.instance.game.worldManager.disableWorld(args[0]);
					sender.sendMessage("The world, " + args[0] + ", has been disabled.");
					return true;
				}
			}
			if(cmd.getName().equalsIgnoreCase("addDoubleCoinTime")) {
				if(args.length > 0) {
					int timeInSeconds = Integer.valueOf(args[0]);
					PrimeCraftMain.instance.game.bonusManager.addTimeToDoubleCoins(timeInSeconds);
					return true;
				}
			}
			if(sender instanceof Player) {
				Player playerSender = (Player) sender;
				if(cmd.getName().equalsIgnoreCase("gotoWorld")) {
					if(args.length > 0) {
						World w = Bukkit.getWorld(args[0]);
						if(w != null) {
							playerSender.teleport(w.getSpawnLocation());
							return true;
						}
					} else {
						sender.sendMessage("Enter valid name");
						return true;
					}
				}
				if(cmd.getName().equalsIgnoreCase("spawngate")) {
					if(args.length > 1) {
						if(!args[0].equalsIgnoreCase("respawn") && !args[0].equalsIgnoreCase("game")) {
							sender.sendMessage("Enter either Respawn or Game");
							return true;
						}
						ItemStack spawnGateTool = new ItemStack(Material.STICK);
						ItemMeta im = spawnGateTool.getItemMeta();
						im.setDisplayName("SpawnGate Tool " + args[0] + " " + args[1]);
						spawnGateTool.setItemMeta(im);
						((Player) sender).setItemInHand(spawnGateTool);
						sender.sendMessage("Given SpawnGate Tool" + args[0] + " " + args[1]);
						return true;
					} else {
						sender.sendMessage("Enter valid spawn gate type and team name");
						return true;
					}
				}
				if(cmd.getName().equalsIgnoreCase("capturepointtool")) {
					if(args.length > 1) {
						CaptureLevel cl = CaptureLevel.getLevelForName(args[0]);
						if(cl != null) {
							int capturePointIdentity = 0;
							try {
								capturePointIdentity = Integer.parseInt(args[1]);
							} catch(NumberFormatException e) {
								sender.sendMessage("Please enter a valid number.");
								return true;
							}
							ItemStack spawnGateTool = new ItemStack(Material.STICK);
							ItemMeta im = spawnGateTool.getItemMeta();
							im.setDisplayName("CapturePointTool " + cl.name() + " " + capturePointIdentity);
							spawnGateTool.setItemMeta(im);
							((Player) sender).setItemInHand(spawnGateTool);
							sender.sendMessage(ChatColor.GRAY + "Given CapturePointTool " + cl.name() + " " + capturePointIdentity);
						} else {
							sender.sendMessage(ChatColor.GRAY + "Enter a valid capture level(Low, Half, High, Full).");
						}
						return true;
					} else {
						sender.sendMessage(ChatColor.GRAY + "Enter valid CaptureLevel(Low, Half, High, Full) and CapturePoint ID(Zero-Based)");
					}
				}
				if(cmd.getName().equalsIgnoreCase("setcapturepointcount")) {
					if(args.length > 0) {
						WorldDetails.setCapturePointCount(((Player) sender).getLocation(), Integer.parseInt(args[0]));
						sender.sendMessage(ChatColor.GRAY + "Set Capture Point Count to " + WorldDetails.getCapturePointCount(((Player) sender).getLocation().getWorld().getName()));
						return true;
					} else {
						sender.sendMessage(ChatColor.GRAY + "Please include the number of points you would like to set for this map.");
					}
				}
				if(cmd.getName().equalsIgnoreCase("setcapturepoint")) {
					if(args.length > 1) {
						int numberOfPoints = WorldDetails.getCapturePointCount(((Player) sender).getWorld().getName());
						if(numberOfPoints > 0) {
							try {
								int pointID = Integer.parseInt(args[0]);
								if(pointID >= 0 && pointID < numberOfPoints) {
									TeamDetails team = TeamDetails.getDetailsForName(args[1]);
									if(team != null) {
										// Success
										WorldDetails.setCapturePointLocation(((Player) sender).getLocation(), pointID);
										CapturePoint cp = new CapturePoint(WorldDetails.getCapturePointLocation(((Player) sender).getLocation().getWorld().getName(), pointID), pointID);
										cp.setInitialTeam(team);
										cp.saveToConfig(((Player) sender).getLocation().getWorld().getName());
										sender.sendMessage(ChatColor.GRAY + "Set Capture Point Location to " + WorldDetails.getCapturePointLocation(((Player) sender).getLocation().getWorld().getName(), pointID) + " and initial team to " + team.name());
									} else {
										sender.sendMessage(ChatColor.GRAY + "Please use a valid team name.");
										for(TeamDetails availableTeams : TeamDetails.values()) {
											sender.sendMessage(availableTeams.name());
										}
									}
								} else {
									sender.sendMessage(ChatColor.GRAY + "That ID does not exist on this map. There are " + numberOfPoints + " Command Posts so you may use IDs 0 through " + (numberOfPoints - 1));
								}
							} catch(NumberFormatException e) {
								sender.sendMessage(ChatColor.GRAY + "Please use a number for the Command Post ID");
							}
						} else {
							sender.sendMessage(ChatColor.GRAY + "This world doesn't seem to have any Command Posts. Use /setcapturepointcount to fix this.");
						}
						return true;
					} else {
						sender.sendMessage(ChatColor.GRAY + "Please use a zero-based index and an initial team for the Command Post to start as(Neutral for no team).");
					}
				}
				if(cmd.getName().equalsIgnoreCase("setassaultpointcount")) {
					if(args.length > 0) {
						WorldDetails.setAssaultPointCount(((Player) sender).getLocation(), Integer.parseInt(args[0]));
						sender.sendMessage(ChatColor.GRAY + "Set Assault Point Count to " + WorldDetails.getAssaultPointCount(((Player) sender).getLocation().getWorld().getName()));
						return true;
					} else {
						sender.sendMessage(ChatColor.GRAY + "Please include the number of points you would like to set for this map.");
					}
				}
				if(cmd.getName().equalsIgnoreCase("setassaultpoint")) {
					if(args.length > 0) {
						int numberOfPoints = WorldDetails.getAssaultPointCount(((Player) sender).getWorld().getName());
						if(numberOfPoints > 0) {
							try {
								int pointID = Integer.parseInt(args[0]);
								if(pointID >= 0 && pointID < numberOfPoints) {
									if(playerSender.getWorld().getBlockAt(playerSender.getLocation()).getType().equals(Material.STONE_BUTTON)) {
										// Success
										WorldDetails.setAssaultPointLocation(((Player) sender).getLocation(), pointID);
										sender.sendMessage(ChatColor.GRAY + "Set Capture Point Location to " + WorldDetails.getAssaultPointLocation(((Player) sender).getLocation().getWorld().getName(), pointID));
									} else {
										sender.sendMessage(ChatColor.GRAY + "This location is not a button. Could not set Assault Point.");
									}
								} else {
									sender.sendMessage(ChatColor.GRAY + "That ID does not exist on this map. There are " + numberOfPoints + " Assault Points so you may use IDs 0 through " + (numberOfPoints - 1));
								}
							} catch(NumberFormatException e) {
								sender.sendMessage(ChatColor.GRAY + "Please use a number for the Assault Point ID");
							}
						} else {
							sender.sendMessage(ChatColor.GRAY + "This world doesn't seem to have any Assault Points. Use /setassaultpointcount to fix this.");
						}
						return true;
					} else {
						sender.sendMessage(ChatColor.GRAY + "Please use a zero-based index for the Assault Point.");
					}
				}
				if(cmd.getName().equalsIgnoreCase("setlobby")) {
					WorldDetails.setLobbySpawn(((Player) sender).getLocation());
					sender.sendMessage(ChatColor.GRAY + "Set Lobby to " + WorldDetails.getLobbySpawn().toString());
					return true;
				}
				if(cmd.getName().equalsIgnoreCase("setparkourlevel")) {
					if(args.length > 2) {
						ParkourManager.addNewParkourLevel(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), playerSender.getLocation());
						sender.sendMessage(ChatColor.GRAY + "Added parkour level " + Integer.parseInt(args[1]) + " to course " + args[0] + " with prize amount " + Integer.parseInt(args[2]) + " at location " + playerSender.getLocation());
					} else {
						sender.sendMessage(ChatColor.GRAY + "Include a Course Name, Level, and Prize Amount while standing at the beginning of the level.");
					}
					return true;
				}
				if(cmd.getName().equalsIgnoreCase("setplayerparkourlevel")) {
					if(args.length > 2) {
						PlayerProfile pp = PrimeCraftMain.instance.game.getProfileForName(args[0]);
						if(pp != null) {
							pp.setParkourLevel(args[1], Integer.parseInt(args[2]));
							sender.sendMessage(ChatColor.GRAY + "Set " + pp.getPlayer().getDisplayName() + " to Parkour Level " + Integer.parseInt(args[1]) + " in course " + args[0]);
						} else {
							sender.sendMessage(ChatColor.GRAY + "That player doesn't exist on record");
						}
					} else {
						sender.sendMessage(ChatColor.GRAY + "Include the player's name, the Course name, and the Level ID");
					}
					return true;
				}
				if(cmd.getName().equalsIgnoreCase("teamspawn")) {
					if(args.length > 0) {
						TeamDetails td = TeamDetails.valueOf(args[0]);
						if(td != null) {
							WorldDetails.setTeamSpawn(((Player) sender).getLocation(), td.getTeamID());
							sender.sendMessage(ChatColor.GRAY + "Set " + td.name() + " Team spawn to " + WorldDetails.getTeamSpawn(((Player) sender).getLocation().getWorld().getName(), td.getTeamID()).toString());
						} else {
							sender.sendMessage(ChatColor.GRAY + "Enter a valid team name");
						}
						return true;
					}
				}
				if(cmd.getName().equalsIgnoreCase("teamflag")) {
					if(args.length > 0) {
						TeamDetails td = TeamDetails.valueOf(args[0]);
						if(td != null) {
							WorldDetails.setTeamFlagSpawn(((Player) sender).getLocation(), td.getTeamID());
							sender.sendMessage("Set " + td.name() + " Team Flag spawn to " + WorldDetails.getTeamFlagSpawn(((Player) sender).getLocation().getWorld().getName(), td.getTeamID()).toString());
						} else {
							sender.sendMessage("Enter a valid team name");
						}
						return true;
					}
				}
				if(cmd.getName().equalsIgnoreCase("setgamedetailsign")) {
					Block block = playerSender.getLocation().getBlock();
					if(block.getType().equals(Material.WALL_SIGN)) {
						if(PrimeCraftMain.instance.game.setGameDetailSign(block)) {
							sender.sendMessage("Reset Game Detail Sign to " + block.getLocation());
						} else {
							sender.sendMessage("Set Game Detail Sign to " + block.getLocation());
						}
					} else {
						sender.sendMessage("This block is not a wall sign.");
					}
					return true;
				}
				if(cmd.getName().equalsIgnoreCase("setartifact")) {
					WorldDetails.setArtifactSpawn(((Player) sender).getLocation());
					sender.sendMessage("Set Artifact spawn to " + WorldDetails.getArtifactSpawn(((Player) sender).getLocation().getWorld().getName()));
					return true;
				}
			}
		}
		return false;
	}
}