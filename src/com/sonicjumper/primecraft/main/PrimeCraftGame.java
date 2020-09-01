package com.sonicjumper.primecraft.main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sonicjumper.primecraft.main.gamemode.Gamemode;
import com.sonicjumper.primecraft.main.parkour.ParkourManager;
import com.sonicjumper.primecraft.main.player.LegacyManager;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.shop.ShopItem;
import com.sonicjumper.primecraft.main.teams.PlayerTeam;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.teams.TeamManager;
import com.sonicjumper.primecraft.main.weapons.WeaponHandler;
import com.sonicjumper.primecraft.main.world.PlayerWorld;
import com.sonicjumper.primecraft.main.world.Voter;
import com.sonicjumper.primecraft.main.world.WorldDetails;
import com.sonicjumper.primecraft.main.world.WorldManager;
import com.sonicjumper.primecraft.votifier.VoterListener;

public class PrimeCraftGame {
	//Game Variables
	public WorldManager worldManager;
	public TeamManager teamManager;
	public WeaponHandler weaponHandler;
	public BonusManager bonusManager;
	
	public WorldGuardPlugin worldGuard;
	
	private LegacyManager legacyManager;
	private ArrayList<PlayerProfile> playersInGame;
	public HashMap<PlayerProfile, TeamDetails> preferredTeamList;
	
	public ArrayList<PlayerWorld> mapsForVoteSession;
	
	public boolean isMapLoaded = false;
	
	private int countdownID;
	private int updaterTaskID;
	
	private int gameSessionID;
	
	private boolean isGamePaused;
	private boolean isGameStarted;
	
	public PlayerWorld loadedWorld;
	public Gamemode currentGamemode;
	public Block gameDetailSign;
	
	private Random rand = new Random();
	
	public final int MAPS_PER_VOTE_SESSION = 5;
	
	/**
	 * Does pre-loading before playing the game. Only ran during Server onEnable(), or by the /startgame command.
	 */
	public void initializeGame() {
		playersInGame = new ArrayList<PlayerProfile>();
		preferredTeamList = new HashMap<PlayerProfile, TeamDetails>();
		legacyManager = new LegacyManager();
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			registerPlayer(p);
		}
		
		worldGuard = (WorldGuardPlugin) Bukkit.getPluginManager().getPlugin("WorldGuard");
		
		worldManager = new WorldManager();
		worldManager.setupWorlds();
		ParkourManager.loadFromConfig();
		VoterListener.loadVotes();
		Voter.getVoter().loadStats();
		
		teamManager = new TeamManager();
		for(TeamDetails td : TeamDetails.values()) {
			teamManager.addPlayerTeam(new PlayerTeam(td));
		}
		
		if(WorldDetails.getLobbySpawn() != null) {
			teamManager.getTeamForDetails(TeamDetails.Neutral).setupTeam(WorldDetails.getLobbySpawn());
		} else {
			Bukkit.broadcastMessage("No lobby for Ascended has been set. Please set one and restart the server");
		}
		
		weaponHandler = new WeaponHandler();
		bonusManager = new BonusManager();

		gameSessionID = 0;

		if(PrimeCraftMain.instance.getGameConfig().contains("GameDetailSign")) {
			String[] gameSignBlockData = PrimeCraftMain.instance.getGameConfig().getString("GameDetailSign").split(",");
			gameDetailSign = Bukkit.getWorld(gameSignBlockData[0]).getBlockAt(Integer.valueOf(gameSignBlockData[1]), Integer.valueOf(gameSignBlockData[2]), Integer.valueOf(gameSignBlockData[3]));
		}
		
		restartGame();
	}
	
	/**
	 * Restarts game for the server, starts voting
	 */
	public void restartGame() {
		PrimeCraftMain.instance.saveAllConfigs();
		teleportToSpawns();
		
		for(PlayerProfile pp : playersInGame) {
			if(!pp.getPlayer().isDead()) {
				pp.getPlayer().getInventory().clear();
				pp.getPlayer().getInventory().setArmorContents(null);
				pp.getPlayer().setHealth(20.0D);
				pp.getPlayer().setFoodLevel(20);
			}
			pp.saveToConfig();
		}
		
		PrimeCraftMain.instance.motd = ChatColor.GOLD + "Ascended PvP! Voting for map";
		if(loadedWorld != null) {
			updateGameDetailSign("Voting", "Use /v to vote", "Last played:", loadedWorld.getWorldName());
		} else {
			updateGameDetailSign("Voting", "Use /v to vote", "", "");
		}
		
		gameSessionID++;
		
		Voter.getVoter().startVoting();
		isMapLoaded = false;
		isGameStarted = false;
		broadcastMessageToPlayers(ChatColor.LIGHT_PURPLE + "Voting started. Vote for maps with /vote #");
		mapsForVoteSession = worldManager.getRandomPlayableMaps(MAPS_PER_VOTE_SESSION, loadedWorld);
		if(mapsForVoteSession.size() != 0) {
			for(int i = 0; i < mapsForVoteSession.size(); i++) {
				PlayerWorld pw = mapsForVoteSession.get(i);
				broadcastMessageToPlayers(ChatColor.GREEN + "#" + (i + 1) + " " + ChatColor.AQUA + pw.getWorldName() + ": " + pw.getDescription());
			}
			for(PlayerProfile pp : playersInGame) {
				if(pp.canPreferTeam()) {
					pp.getPlayer().sendMessage(ChatColor.GOLD + "Don't forget to /preferteam <team> for the next map");
				}
			}
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(PrimeCraftMain.instance, new Runnable() {
				@Override
				public void run() {
					broadcastMessageToPlayers(ChatColor.LIGHT_PURPLE + "Voting finished");
					int winningMapID = Math.min(mapsForVoteSession.size(), Voter.getVoter().getWinnngMap()) - 1;
					loadedWorld = mapsForVoteSession.get(winningMapID);
					isMapLoaded = true;
					broadcastMessageToPlayers(ChatColor.GREEN + "Loaded map " + loadedWorld.getWorldName() + " submitted by " + loadedWorld.getAuthor());
					preloadMap();
				}
			}
			, 1200L);
		} else {
			broadcastMessageToPlayers("It looks like there aren't any maps loaded... Use /enableworld to make some.");
		}
	}

	/**
	 * Loads the map and sets up the teams.
	 */
	private void preloadMap() {
		//Need to get spawns for specific world
		for(PlayerTeam pt : teamManager.getPlayingTeams()) {
			pt.setupTeam(WorldDetails.getTeamSpawn(loadedWorld.getWorldName(), pt.getTeamDetails().getTeamID()));
		}

		//Get game mode for the map we are playing on
		currentGamemode = loadedWorld.getGamemode();

		currentGamemode.preGame();

		PrimeCraftMain.instance.motd = ChatColor.GOLD + "Ascended PvP! Playing " + loadedWorld.getWorldName();
		updateGameDetailSign(
				ChatColor.DARK_RED + "<*" + loadedWorld.getWorldName() + "*>",
				ChatColor.BLUE + loadedWorld.getGamemode().getShortName(),
				"",
				"");
		
		for(PlayerProfile pp : playersInGame) {
			TeamDetails.Neutral.getTeamObject().addPlayer(pp);
		}
		
		for(PlayerProfile pp : preferredTeamList.keySet()) {
			TeamDetails td = preferredTeamList.get(pp);
			if(td.getTeamID() + 1 > loadedWorld.getGamemode().numberOfTeams) {
				pp.getPlayer().sendMessage(ChatColor.GRAY + "Your preferred team is unavailable this game. Try again next time ;)");
			} else {
				preferredTeamList.get(pp).getTeamObject().addPlayer(pp);
				pp.getPlayer().sendMessage(ChatColor.GRAY + "You have been added to your preferred team.");
			}
		}
		
		preferredTeamList.clear();

		Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Game will start in 60 seconds");
		countdownID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PrimeCraftMain.instance, new Runnable() {
			private int timer = 60;

			@Override
			public void run() {
				if(timer > 0) {
					if(timer <= 5) {
						Bukkit.getServer().broadcastMessage(ChatColor.GRAY + "Game will start in " + timer + " seconds...");
					}
					timer--;
				} else {
					countdownFinished();
					Bukkit.getServer().getScheduler().cancelTask(countdownID);
				}
			}
		}
		, 0L, 20L);
	}

	private void countdownFinished() {
		Bukkit.getServer().broadcastMessage(ChatColor.DARK_GRAY + "Game start!");
		isGameStarted = true;
		
		currentGamemode.startGame();
		//Start game updater
		updaterTaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(PrimeCraftMain.instance, new Runnable() {
			@Override
			public void run() {
				updateGame();
			}
		}
		, 0L, 2L);
	}

	private void updateGame() {
		if(!isGamePaused) {
			currentGamemode.updateGame();
			
			weaponHandler.updateWeapons();
			
			bonusManager.updateBonuses();
		}
		
		if(!do2TeamsHavePlayers()) {
			if(!isGamePaused) {
				pauseGame();
			}
		} else if(isGamePaused) {
			unPauseGame();
		}
		
		//Set time to loadWorld preferred time
		if(loadedWorld.hasPreferredTime() && (Bukkit.getWorld(loadedWorld.getWorldName()).getTime() > loadedWorld.getPreferredTime() + 1000 || Bukkit.getWorld(loadedWorld.getWorldName()).getTime() < loadedWorld.getPreferredTime() - 1000)) {
			Bukkit.getWorld(loadedWorld.getWorldName()).setTime(loadedWorld.getPreferredTime());
		}
		
		if(currentGamemode.doesWinnerExist()) {
			ArrayList<PlayerTeam> winningTeams = currentGamemode.getWinningTeams();
			Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + winningTeams.get(0).getTeamDetails().name() + " Wins!");
			for(int i = 0; i < winningTeams.size(); i++) {
				PlayerTeam pt = winningTeams.get(i);
				for(PlayerProfile pp : pt.getPlayerList()) {
					pp.wonPlace(i + 1);
				}
			}
			endGame();
		}
	}

	private void pauseGame() {
		isGamePaused = true;
		
		teleportToSpawns();
		
		currentGamemode.pauseGame();
		
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "The game has paused because there are not enough players to play.");
	}
	
	private void unPauseGame() {
		isGamePaused = false;
		
		teleportToSpawns();
		
		currentGamemode.unPauseGame();
		
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "The game has resumed play.");
	}

	//At the end of the game, readying to play the next game
	private void endGame() {
		Bukkit.getServer().getScheduler().cancelTask(updaterTaskID);
		
		currentGamemode.endGame();
		
		restartGame();
	}

	public void onDisable() {
		legacyManager.saveToConfig();
		
		if(playersInGame != null) {
			for(PlayerProfile pp : playersInGame) {
				pp.saveToConfig();
			}
		}
		
		if(currentGamemode != null) {
			currentGamemode.endGame();
		}
		
		ParkourManager.saveToConfig();
		VoterListener.saveVotes();
		worldManager.saveWorlds();
		Voter.getVoter().saveStats();

		Bukkit.getServer().getScheduler().cancelTask(countdownID);
		Bukkit.getServer().getScheduler().cancelTask(updaterTaskID);
		
		if(gameDetailSign != null) {
			PrimeCraftMain.instance.getGameConfig().set("GameDetailSign", gameDetailSign.getWorld().getName() + "," + gameDetailSign.getX() + "," + gameDetailSign.getY() + "," + gameDetailSign.getZ());
		}
	}
	
	/**
	 * @param block The block to set. Must be a wall sign to work.
	 * @return True if replaced. False if set.
	 */
	public boolean setGameDetailSign(Block block) {
		if(gameDetailSign == null) {
			gameDetailSign = block;
			return false;
		} else {
			gameDetailSign = block;
			return true;
		}
	}

	private void updateGameDetailSign(String line1, String line2, String line3, String line4) {
		try {
			if(gameDetailSign != null) {
				if(gameDetailSign.getState() instanceof Sign) {
					Sign signData = (Sign) gameDetailSign.getState();
					signData.setLine(0, line1);
					signData.setLine(1, line2);
					signData.setLine(2, line3);
					signData.setLine(3, line4);
					signData.update();
				} else {
					gameDetailSign.setType(Material.WALL_SIGN);
				}
			}
		} catch(Exception e) {
			
		}
	}

	private boolean do2TeamsHavePlayers() {
		int teamsWithPlayers = 0;
		for(int i = 0 ; i < currentGamemode.numberOfTeams; i++) {
			TeamDetails td = TeamDetails.getDetailsForID(i);
			if(td.getTeamObject().getPlayerList().size() != 0) {
				teamsWithPlayers++;
			}
		}
		return teamsWithPlayers > 1;
	}

	public void assignPlayer(PlayerProfile pp) {
		PlayerTeam lowestTeam = null;
		int lowestRating = 100000;
		for(int i = 0; i < currentGamemode.numberOfTeams; i++) {
			PlayerTeam pt = teamManager.getPlayingTeams().get(i);
			if(pt.getTeamLoadoutRating() < lowestRating) {
				lowestRating = pt.getTeamLoadoutRating();
				lowestTeam = pt;
			}
		}
		lowestTeam.addPlayer(pp);
	}
	
	public void unassignPlayer(PlayerProfile pp) {
		pp.getCurrentTeam().removePlayerFromTeam(pp);
		if(pp.getPlayer().isOnline()) {
			teamManager.getTeamForDetails(TeamDetails.Neutral).addPlayer(pp);
		}
	}

	private void teleportToSpawns() {
		for(PlayerProfile pp : playersInGame) {
			teleportToSpawn(pp);
		}
	}
	
	/**
	 * Convenience method for sending the player to spawn
	 * @param pp
	 */
	public void teleportToSpawn(PlayerProfile pp) {
		if(pp.getCurrentTeam().getTeamDetails().isPlayingTeam()) {
			pp.getCurrentTeam().teleportPlayerToSpawn(pp);
		}
	}
	
	/**
	 * All Players need to be registered through this method before being able to play the game!
	 */
	public void registerPlayer(Player player) {
		PlayerProfile pp = legacyManager.getLegacyProfile(player);
		boolean newPlayer = false;
		if(pp == null) {
			newPlayer = true;
			pp = new PlayerProfile(player.getName(), player.getUniqueId());
			legacyManager.addNewPlayer(pp);
			Bukkit.getServer().broadcastMessage(ChatColor.DARK_PURPLE + "Welcome " + pp.getPlayerName() + " to Ascended PvP!");
		}
		pp.refreshPlayerObject();
		playersInGame.add(pp);
		TeamDetails.Neutral.getTeamObject().addPlayer(pp);
		//New Player login message
		if(newPlayer) {
			player.teleport(player.getWorld().getSpawnLocation(), TeleportCause.PLUGIN);
			player.setGameMode(GameMode.ADVENTURE);
			player.sendMessage(ChatColor.GRAY + "This server is an upgradable PvP game type. As you play, you will earn coins, which you can use to upgrade your armor and weapons.");
			player.sendMessage(ChatColor.GRAY + "During play, you can click on the [Join Game] sign in the lobby to join the game.");
			player.sendMessage(ChatColor.GRAY + "When the game ends, either by time or score, a voting session will begin.");
			player.sendMessage(ChatColor.GRAY + "Vote for the map you wish to play, and after 60 seconds, a new game will begin.");
			player.sendMessage(ChatColor.GRAY + "After you get through the tutorial region, you may buy items from the shop and play the game.");
			player.sendMessage(ChatColor.GRAY + "Here are some starting coins to help you out.");
			player.sendMessage(ChatColor.GRAY + "^^^ READ THIS BEFORE ASKING QUESTIONS");
			pp.giveMoney(1000);
			pp.setItemInLoadout(ShopItem.goldHelm);
			pp.setItemInLoadout(ShopItem.goldChest);
			pp.setItemInLoadout(ShopItem.goldLegs);
			pp.setItemInLoadout(ShopItem.goldBoots);
			player.sendMessage(ChatColor.GRAY + "Good luck fighting!");
		}
		//All other messages
		if(bonusManager.isDoubleCoinsActive()) {
			bonusManager.sendMessageDoubleCoinTime(player);
		}
	}

	public void unregisterPlayer(PlayerProfile pp) {
		playersInGame.remove(pp);
		if(TeamDetails.Neutral.getTeamObject().isPlayerOnTeam(pp)) {
			pp.getCurrentTeam().removePlayerFromTeam(pp);
		}
	}
	
	public void broadcastMessageToPlayers(String message) {
		for(PlayerProfile pp : getPlayers()) {
			pp.getPlayer().sendMessage(message);
		}
	}

	public void broadcastMessageToTeam(PlayerProfile sendingPlayer, String message) {
		for(PlayerProfile pp : sendingPlayer.getCurrentTeam().getPlayerList()) {
			pp.getPlayer().sendMessage(ChatColor.GREEN + "[TC]" + ChatColor.WHITE + sendingPlayer.getPlayer().getDisplayName() + ": " + message);
		}
	}

	public void broadcastDeathMessage(PlayerProfile killer, PlayerProfile killedPlayer, String deathMessage) {
		for(PlayerProfile pp : getPlayers()) {
			if(pp.isUsingDeathMessages() || pp.equals(killer) || pp.equals(killedPlayer)) {
				pp.getPlayer().sendMessage(deathMessage);
			}
		}
	}
	
	public boolean isLocationInSpawnRegion(Location loc) {
		for(ProtectedRegion pr : worldGuard.getRegionManager(loc.getWorld()).getApplicableRegions(loc)) {
			if(pr.getId().contains("spawn")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isPaused() {
		return isGamePaused;
	}
	
	public boolean isStarted() {
		return isGameStarted;
	}

	public PlayerProfile getProfileForPlayer(Player p) {
		return legacyManager.getLegacyProfile(p);
	}

	public PlayerProfile getProfileForUniqueID(UUID id) {
		return legacyManager.getLegacyProfile(id);
	}

	public PlayerProfile getProfileForName(String name) {
		return legacyManager.getLegacyProfile(name);
	}

	public ArrayList<PlayerProfile> getPlayers() {
		return playersInGame;
	}

	public int getCurrentGameSessionID() {
		return gameSessionID;
	}

	public PlayerProfile getRandomPlayer(boolean returnPlayerInGame) {
		if(returnPlayerInGame) {
			ArrayList<PlayerProfile> playerList = new ArrayList<PlayerProfile>();
			for(PlayerProfile pp : playersInGame) {
				if(pp.getCurrentTeam().getTeamDetails().isPlayingTeam() && !isLocationInSpawnRegion(pp.getPlayer().getLocation()) && !pp.getPlayer().isDead()) {
					playerList.add(pp);
				}
			}
			if(playerList.size() > 0) {
				return playerList.get(rand.nextInt(playerList.size()));
			}
		} else {
			if(playersInGame.size() > 0) {
				return playersInGame.get(rand.nextInt(playersInGame.size()));
			}
		}
		return null;
	}
}
