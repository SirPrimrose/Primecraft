package com.sonicjumper.primecraft.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.platymuus.bukkit.permissions.PermissionsPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sonicjumper.primecraft.main.effects.SpecialEffectsHandler;
import com.sonicjumper.primecraft.main.listeners.BlockListener;
import com.sonicjumper.primecraft.main.listeners.PlayerActionListener;
import com.sonicjumper.primecraft.main.listeners.SpawnGuardListener;
import com.sonicjumper.primecraft.main.listeners.TechnicalListener;
import com.sonicjumper.primecraft.main.listeners.TieredItemsListener;
import com.sonicjumper.primecraft.main.player.PlayerRespawner;
import com.sonicjumper.primecraft.main.regionevents.RegionEventsListener;
import com.sonicjumper.primecraft.main.shop.ShopHandler;
import com.sonicjumper.primecraft.main.util.ConfigUtility;
import com.sonicjumper.primecraft.votifier.VoterListener;

public class PrimeCraftMain extends JavaPlugin {
	//TODO List
	//Make a /objecive command to show current objective
	//Fix all de bugs

	//Future Possible TODO List
	//"Magic Rounds" = randomize special set of maps, map name in Magic ChatColor
	//Shop in Inventory
	//Gamemodes
	//- Infected
	//- Headhunter
	//Change color of Tiers in chat

	//ChatColor reference
	//Red - Red Team actions or player on team
	//Blue - Blue Team actions or player on team
	//Yellow - Yellow Team actions or player on team
	//Purple - Purple Team actions or player on team
	//Dark Red - Private server messages to player(EX. "Picked up flag!") or Negative Random Events
	//Gray - Money exchange
	//Light_Purple - Server info or Positive Random Events
	//Gold - Game events
	//Green - Map ID's
	//Aqua - Map descriptions
	public static PrimeCraftMain instance;

	public CommandHandler commandHandler = new CommandHandler();

	private PlayerActionListener playerActionListener = new PlayerActionListener();
	private BlockListener blockListener = new BlockListener();
	private TechnicalListener technicalListener = new TechnicalListener();
	private SpawnGuardListener spawnGuardListener = new SpawnGuardListener();
	private TieredItemsListener tieredItemsListener = new TieredItemsListener();
	private VoterListener voterListener = new VoterListener();
	private ShopHandler shopHandler;
	private ConfigUtility customConfigUtility;

	private RegionEventsListener regionListener;
	private WorldGuardPlugin wgPlugin;
	private PermissionsPlugin permissionsPlugin;
	
	private String GAME_CONFIG = "gameConfig";
	private String PLAYER_CONFIG = "playerData";
	private String WORLD_CONFIG = "worldConfig";
	private String PARKOUR_CONFIG = "parkourConfig";
	
	public SpecialEffectsHandler specialEffectsHandler;
	public PlayerRespawner playerRespawner = new PlayerRespawner();

	public String motd;

	public PrimeCraftGame game;

	@Override
	public void onEnable() {
		super.onEnable();

		motd = ChatColor.GOLD + "PrimeCraft Setting Up";

		setupConfig();
		setupListeners();
		setupCommands();

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			long usedMem;
			long totalMem;
			long freeMem;

			int timer = 0;
			int interval = 600;

			@Override
			public void run() {
				Runtime runtime = Runtime.getRuntime();
				usedMem += (runtime.totalMemory() - runtime.freeMemory());
				totalMem += runtime.totalMemory();
				freeMem += runtime.freeMemory();

				if(timer % interval == 0) {
					Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + " Averages over " + interval + " seconds: [Used / Total / Free] " + ChatColor.BLUE + (usedMem / interval) / 1048576L + " MB / " + (totalMem / interval) / 1048576L + " MB / " + (freeMem / interval) / 1048576L + " MB");
					usedMem = 0;
					totalMem = 0;
					freeMem = 0;
				}

				timer++;
			}
		}, 0L, 20L);

		specialEffectsHandler = new SpecialEffectsHandler();

		getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				specialEffectsHandler.updateEffects();
			}
		}, 0L, 1L);

		instance = this;

		shopHandler = new ShopHandler();

		game = new PrimeCraftGame();
		game.initializeGame();
	}

	@Override
	public void onDisable() {
		super.onDisable();

		game.onDisable();

		saveAllConfigs();
	}

	public void setupConfig() {
		customConfigUtility = new ConfigUtility(this);

		saveAllConfigs();
	}
	
	public void saveAllConfigs() {
		customConfigUtility.saveCustomConfig(GAME_CONFIG);
		customConfigUtility.saveCustomConfig(PLAYER_CONFIG);
		customConfigUtility.saveCustomConfig(WORLD_CONFIG);
		customConfigUtility.saveCustomConfig(PARKOUR_CONFIG);
	}

	private void setupListeners() {
		getServer().getPluginManager().registerEvents(playerActionListener, this);
		getServer().getPluginManager().registerEvents(blockListener, this);
		getServer().getPluginManager().registerEvents(technicalListener, this);
		getServer().getPluginManager().registerEvents(spawnGuardListener, this);
		getServer().getPluginManager().registerEvents(tieredItemsListener, this);
		getServer().getPluginManager().registerEvents(voterListener, this);

		this.wgPlugin = getWGPlugin();
		if(this.wgPlugin == null) {
			getLogger().warning("Could not find World Guard. Disabling spawn gate guarding.");
		} else {
			this.regionListener = new RegionEventsListener(this.wgPlugin);
			getServer().getPluginManager().registerEvents(this.regionListener, this.wgPlugin);
		}
		
		this.permissionsPlugin = getPermissionsPlugin();
		if(this.permissionsPlugin == null) {
			getLogger().warning("Could not find Permissions. Disabling moderator prefixes.");
		}
	}

	private void setupCommands() {
		getCommand("money").setExecutor(commandHandler);
		getCommand("score").setExecutor(commandHandler);
		getCommand("spawn").setExecutor(commandHandler);
		getCommand("stats").setExecutor(commandHandler);
		getCommand("flag").setExecutor(commandHandler);
		getCommand("parkour").setExecutor(commandHandler);
		getCommand("vote").setExecutor(commandHandler);
		getCommand("team").setExecutor(commandHandler);
		getCommand("teamchat").setExecutor(commandHandler);
		getCommand("deathmessages").setExecutor(commandHandler);
		getCommand("togglesounds").setExecutor(commandHandler);
		getCommand("preferteam").setExecutor(commandHandler);
		getCommand("toggleeffect").setExecutor(commandHandler);
		getCommand("listeffects").setExecutor(commandHandler);
		getCommand("startgame").setExecutor(commandHandler);
		getCommand("teamlist").setExecutor(commandHandler);
		getCommand("givemoney").setExecutor(commandHandler);
		getCommand("settier").setExecutor(commandHandler);
		getCommand("resetloadout").setExecutor(commandHandler);
		getCommand("toggleplayereffect").setExecutor(commandHandler);
		getCommand("setlobby").setExecutor(commandHandler);
		getCommand("teamspawn").setExecutor(commandHandler);
		getCommand("teamflag").setExecutor(commandHandler);
		getCommand("setartifact").setExecutor(commandHandler);
		getCommand("setplayerpreferteam").setExecutor(commandHandler);
		getCommand("setgamedetailsign").setExecutor(commandHandler);
		getCommand("spawngate").setExecutor(commandHandler);
		getCommand("capturepointtool").setExecutor(commandHandler);
		getCommand("setcapturepointcount").setExecutor(commandHandler);
		getCommand("setcapturepoint").setExecutor(commandHandler);
		getCommand("setassaultpointcount").setExecutor(commandHandler);
		getCommand("setassaultpoint").setExecutor(commandHandler);
		getCommand("setparkourlevel").setExecutor(commandHandler);
		getCommand("setplayerparkourlevel").setExecutor(commandHandler);
		getCommand("gotoWorld").setExecutor(commandHandler);
		getCommand("listWorlds").setExecutor(commandHandler);
		getCommand("enableWorld").setExecutor(commandHandler);
		getCommand("disableWorld").setExecutor(commandHandler);
		getCommand("addscore").setExecutor(commandHandler);
		getCommand("addDoubleCoinTime").setExecutor(commandHandler);
		getCommand("forcesaveconfig").setExecutor(commandHandler);
	}
	
	public FileConfiguration getGameConfig() {
		return customConfigUtility.getCustomConfig(GAME_CONFIG);
	}
	
	public FileConfiguration getPlayerDataConfig() {
		return customConfigUtility.getCustomConfig(PLAYER_CONFIG);
	}
	
	public FileConfiguration getWorldConfig() {
		return customConfigUtility.getCustomConfig(WORLD_CONFIG);
	}
	
	public FileConfiguration getParkourConfig() {
		return customConfigUtility.getCustomConfig(PARKOUR_CONFIG);
	}

	private WorldGuardPlugin getWGPlugin() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {
			return null;
		}
		return (WorldGuardPlugin)plugin;
	}
	
	private PermissionsPlugin getPermissionsPlugin() {
		Plugin plugin = getServer().getPluginManager().getPlugin("PermissionsBukkit");
		if ((plugin == null) || (!(plugin instanceof PermissionsPlugin))) {
			return null;
		}
		return (PermissionsPlugin)plugin;
	}
	
	public PermissionsPlugin getPerms() {
		return permissionsPlugin;
	}

	public ShopHandler getShopHandler() {
		return shopHandler;
	}
}
