package com.sonicjumper.primecraft.main.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.SoundManager;
import com.sonicjumper.primecraft.main.effects.PlayerEffect;
import com.sonicjumper.primecraft.main.gamemode.GamemodeArtifact;
import com.sonicjumper.primecraft.main.gamemode.GamemodeCaptureTheFlag;
import com.sonicjumper.primecraft.main.parkour.ParkourLevel;
import com.sonicjumper.primecraft.main.shop.ShopItem;
import com.sonicjumper.primecraft.main.shop.ShopItem.ItemType;
import com.sonicjumper.primecraft.main.shop.ShopItem.UpgradePath;
import com.sonicjumper.primecraft.main.shop.ShopItemPotion;
import com.sonicjumper.primecraft.main.teams.PlayerTeam;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public class PlayerProfile {
	private enum Echelon {
		NONE(0, ""),
		REBORN(1, ChatColor.DARK_GREEN + "Reborn"),
		ASCENDED(2, ChatColor.DARK_GREEN + "Ascended"),
		ANGELIC(3, ChatColor.DARK_GREEN + "Angelic"),
		PUTTI(4, ChatColor.DARK_GREEN + "Putti"),
		MALAKHIM(5, ChatColor.AQUA + "Malakhim"),
		ARCHANGEL(6, ChatColor.AQUA + "Archo"),
		RULER(7, ChatColor.AQUA + "Archai"),
		POWER(8, ChatColor.DARK_AQUA + "Exousia"),
		VIRTUE(9, ChatColor.DARK_AQUA + "Dynamis"),
		DOMINION(10, ChatColor.DARK_AQUA + "Dominatio"),
		LORDSHIP(11, ChatColor.DARK_AQUA + "Kyriotes"),
		ELDER(12, ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "Erelim"),
		OPHANIM(13, ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "Ophanim"),
		CHERUBIM(14, ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "Cherubim"),
		SERAPHIM(15, ChatColor.BOLD + "" + ChatColor.DARK_AQUA + "Seraphim");

		int echelonRank;
		String echelonPrefix;

		Echelon(int rank, String prefix) {
			echelonRank = rank;
			echelonPrefix = prefix;
		}

		public int getRank() {
			return echelonRank;
		}

		public String getPrefix() {
			return echelonPrefix;
		}

		public static Echelon getEchelonForId(int id) {
			for(Echelon e : values()) {
				if(e.getRank() == id) {
					return e;
				}
			}
			return Echelon.NONE;
		}
	}

	public static final int TIER_CAP = 10;
	private static Random rand = new Random();
	
	//Referenced player
	private Player player;
	private String playerName;
	private UUID playerID;

	private int money;
	private int foodAmount;
	private HashMap<ShopItem, Integer> arrowCount;
	private HashMap<ShopItem, Integer> maxArrowCount;
	private HashMap<ShopItem, Integer> offensiveCount;
	private HashMap<ShopItem, Integer> maxOffensiveCount;
	private HashMap<ShopItem, Integer> tacticalCount;
	private HashMap<ShopItem, Integer> maxTacticalCount;
	private HashMap<ShopItem, Integer> positivePotionCount;
	private HashMap<ShopItem, Integer> maxPositivePotionCount;
	private HashMap<ShopItem, Integer> negativePotionCount;
	private HashMap<ShopItem, Integer> maxNegativePotionCount;
	private HashMap<UpgradePath, ArrayList<ShopItem>> playerLoadout;
	private int tier;
	private Echelon rebirthEchelon;

	private PlayerStats playerStats;

	//Donator stuff
	private boolean canPreferTeam;
	private ArrayList<PlayerEffect> allowedEffectsList;
	private ArrayList<PlayerEffect> enabledEffectsList;

	//Player's game variables
	private PlayerTeam currentTeam;
	private ArrayList<PlayerProfile> damagers;
	private boolean isPlayerWaitingForRespawn;
	private boolean isUsingTeamChat;
	private boolean isUsingDeathMessages;
	private boolean isUsingSounds;
	private int spawnKillPointCount;
	private int lastPlayedSessionID;

	private PlayerProfile lastKilledPlayer;
	private int timeSinceInSpawn;
	private int timeSinceLastKill;
	private int multiKillCount;
	private int killStreak;
	private int frozenTicks;

	private double donationAmount;
	private String friendlyName;

	public PlayerProfile(String name, UUID id) {
		playerName = name;
		playerID = id;
		donationAmount = 0.0D;
		rebirthEchelon = Echelon.NONE;
		isUsingTeamChat = false;
		isUsingDeathMessages = true;
		isUsingSounds = false;
		playerStats = new PlayerStats(this, id);
		damagers = new ArrayList<PlayerProfile>();
		allowedEffectsList = new ArrayList<PlayerEffect>();
		enabledEffectsList = new ArrayList<PlayerEffect>();
		resetLoadout();
		loadFromConfig();
	}

	//PLAYER RESFRESHING AND RESETTING

	public void refreshPlayerObject() {
		player = Bukkit.getServer().getPlayer(getPlayerID());
		playerName = player.getName();
		frozenTicks = 0;
	}

	/**
	 * Completely resets a player's loadout
	 */
	public void resetLoadout() {
		money = 0;
		playerLoadout = new HashMap<UpgradePath, ArrayList<ShopItem>>();
		playerLoadout.put(UpgradePath.None, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Sword, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Bow, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Arrow, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Offensive, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Tactical, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Potion_Positive, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Potion_Negative, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Helmet, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Chestplate, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Leggings, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Boots, new ArrayList<ShopItem>());
		playerLoadout.put(UpgradePath.Food, new ArrayList<ShopItem>());
		setOnlyItemInLoadout(UpgradePath.None, ShopItem.noItem);
		setOnlyItemInLoadout(UpgradePath.Sword, ShopItem.goldenSword);
		setOnlyItemInLoadout(UpgradePath.Bow, ShopItem.noItem);
		setOnlyItemInLoadout(UpgradePath.Arrow, ShopItem.arrowCount);
		setOnlyItemInLoadout(UpgradePath.Offensive, ShopItem.noItem);
		setOnlyItemInLoadout(UpgradePath.Tactical, ShopItem.noItem);
		setOnlyItemInLoadout(UpgradePath.Potion_Positive, ShopItem.noItem);
		setOnlyItemInLoadout(UpgradePath.Potion_Negative, ShopItem.noItem);
		setOnlyItemInLoadout(UpgradePath.Helmet, ShopItem.leatherHelm);
		setOnlyItemInLoadout(UpgradePath.Chestplate, ShopItem.leatherChest);
		setOnlyItemInLoadout(UpgradePath.Leggings, ShopItem.leatherLegs);
		setOnlyItemInLoadout(UpgradePath.Boots, ShopItem.leatherBoots);
		setOnlyItemInLoadout(UpgradePath.Food, ShopItem.potato);
		foodAmount = 5;
		arrowCount = new HashMap<ShopItem, Integer>();
		maxArrowCount = new HashMap<ShopItem, Integer>();
		maxArrowCount.put(ShopItem.arrowCount, 0);
		offensiveCount = new HashMap<ShopItem, Integer>();
		maxOffensiveCount = new HashMap<ShopItem, Integer>();
		maxOffensiveCount.put(ShopItem.noItem, 0);
		tacticalCount = new HashMap<ShopItem, Integer>();
		maxTacticalCount = new HashMap<ShopItem, Integer>();
		maxTacticalCount.put(ShopItem.noItem, 0);
		positivePotionCount = new HashMap<ShopItem, Integer>();
		maxPositivePotionCount = new HashMap<ShopItem, Integer>();
		maxPositivePotionCount.put(ShopItem.noItem, 0);
		negativePotionCount = new HashMap<ShopItem, Integer>();
		maxNegativePotionCount = new HashMap<ShopItem, Integer>();
		maxNegativePotionCount.put(ShopItem.noItem, 0);
	}

	private void setOnlyItemInLoadout(UpgradePath path, ShopItem itemToAdd) {
		playerLoadout.get(path).clear();
		playerLoadout.get(path).add(itemToAdd);
	}

	private void addItemToLoadout(UpgradePath path, ShopItem itemToAdd) {
		if(playerLoadout.get(path).size() == 1 && (playerLoadout.get(path).get(0).equals(ShopItem.noItem))) {
			playerLoadout.get(path).clear();
		}
		if(!playerLoadout.get(path).contains(itemToAdd)) {
			playerLoadout.get(path).add(itemToAdd);
		}
	}

	/**
	 * Resets the sword and armor for a player, and gives them a tier
	 */
	public void tierPlayer() {
		if(tier < TIER_CAP) {
			if(getItemsForUpgradeType(UpgradePath.Sword).get(0).material.equals(Material.DIAMOND_SWORD) &&
					getItemsForUpgradeType(UpgradePath.Helmet).get(0).material.equals(Material.DIAMOND_HELMET) &&
					getItemsForUpgradeType(UpgradePath.Chestplate).get(0).material.equals(Material.DIAMOND_CHESTPLATE) &&
					getItemsForUpgradeType(UpgradePath.Leggings).get(0).material.equals(Material.DIAMOND_LEGGINGS) &&
					getItemsForUpgradeType(UpgradePath.Boots).get(0).material.equals(Material.DIAMOND_BOOTS)) {
				money = 0;
				setOnlyItemInLoadout(UpgradePath.Sword, ShopItem.goldenSword);
				setOnlyItemInLoadout(UpgradePath.Helmet, ShopItem.leatherHelm);
				setOnlyItemInLoadout(UpgradePath.Chestplate, ShopItem.leatherChest);
				setOnlyItemInLoadout(UpgradePath.Leggings, ShopItem.leatherLegs);
				setOnlyItemInLoadout(UpgradePath.Boots, ShopItem.leatherBoots);
				//resetLoadout();
				tier++;
				SoundManager.playPlayerTiered();
				Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.MAGIC + "^^^^^^^" + getPlayer().getDisplayName() + ChatColor.RESET + ChatColor.GREEN + " has Tiered to " + PrimeUtility.getRomanFromArabic(tier) + ChatColor.RESET + ChatColor.GOLD + "" + ChatColor.MAGIC + "^^^^^^^");
				saveToConfig();
				getPlayer().sendMessage("You have tiered! Your money, armor, and sword has been reset.");
				getPlayer().sendMessage("You may have unlocked benefits for tiered players!");
				refreshPlayerDisplayName();
			} else {
				getPlayer().sendMessage("You need to have full diamond armor and a diamond sword before tiering.");
			}
		} else {
			getPlayer().sendMessage("You have reached the maximum tier. You may now Rebirth to raise your Echelon!");
		}
	}

	public void raiseEchelonForPlayer() {
		if(tier >= TIER_CAP) {
			if(getItemsForUpgradeType(UpgradePath.Sword).get(0).material.equals(Material.DIAMOND_SWORD) &&
					getItemsForUpgradeType(UpgradePath.Helmet).get(0).material.equals(Material.DIAMOND_HELMET) &&
					getItemsForUpgradeType(UpgradePath.Chestplate).get(0).material.equals(Material.DIAMOND_CHESTPLATE) &&
					getItemsForUpgradeType(UpgradePath.Leggings).get(0).material.equals(Material.DIAMOND_LEGGINGS) &&
					getItemsForUpgradeType(UpgradePath.Boots).get(0).material.equals(Material.DIAMOND_BOOTS)) {
				if(Echelon.getEchelonForId(rebirthEchelon.getRank() + 1) != null) {
					rebirthEchelon = Echelon.getEchelonForId(rebirthEchelon.getRank() + 1);
					tier = 0;
					resetLoadout();
					SoundManager.playPlayerRebirth();
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|======================================|");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|                      |");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|                      |");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|                      |");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|                    _V_");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|           __     _ Q   __");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|<¨¨¨¨<¨¨¨<¨¨¯¯._¯¨-.[T]|\\¨¯_.¯¯¨¨>¨¨¨>¨¨¨¨>");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|  <¨¨¨<¨¨<¨¨<./u((_¥_)/u\\.>¨¨>¨¨>¨¨¨>");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|         .-´.´/U|¯¯¯|/ UUU\\`.`-.");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|                | ¥ |\\");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|               \\  / \\");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|                 `-´|  \\");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|                 `._|_.´");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "" + ChatColor.MAGIC + "iiiiiii" + ChatColor.RESET + ChatColor.BOLD + getPlayer().getDisplayName() + ChatColor.AQUA + " has Rebirthed to " + rebirthEchelon.getPrefix() + "!" + ChatColor.RESET + ChatColor.GOLD + "" + ChatColor.MAGIC + "iiiiiii");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|");
					Bukkit.getServer().broadcastMessage(ChatColor.GOLD + "|======================================|");
					getPlayer().sendMessage(ChatColor.GRAY + "You have increased your Echelon on Ascended PvP! Your legacy will forever be remembered.");
					refreshPlayerDisplayName();
				} else {
					getPlayer().sendMessage("I don't know how, but you have reached the max Echelon for this server. CONGRATZ!");
				}
			} else {
				getPlayer().sendMessage("You must have full diamond armor and a diamond sword to Rebirth");
			}
		} else {
			getPlayer().sendMessage("You must have the maximum tier(" + PrimeUtility.getRomanFromArabic(TIER_CAP) + ") and full diamond armor and a diamond sword to Rebirth");
		}
	}

	/**
	 * Exists only to restore the Player's helmet after scoring with the flag
	 */
	public void refreshHelmet() {
		getPlayer().getInventory().setHelmet(PrimeUtility.getNamedStack(playerLoadout.get(UpgradePath.Helmet).get(0).material, playerLoadout.get(UpgradePath.Helmet).get(0).itemDisplayName, 1));
	}

	@SuppressWarnings("deprecation")
	public void refreshPlayerGear() {
		if(rand.nextInt(10000) == 0) {
			ItemStack holyMackeral = PrimeUtility.getNamedStack(Material.RAW_FISH, ChatColor.AQUA + "The Holy Mackeral", 1);
			PrimeUtility.addLoreToItem(holyMackeral, ChatColor.DARK_PURPLE + "Getting slapped by a fish has got to be humiliating.");
			holyMackeral.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 10);
			holyMackeral.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
			getPlayer().getInventory().setItem(0, holyMackeral);
			ItemStack fishHelm = PrimeUtility.getNamedStack(Material.LEATHER_HELMET, ChatColor.AQUA + "--F--", 1);
			PrimeUtility.setColorOfLeatherArmor(fishHelm, Color.AQUA);
			fishHelm.addUnsafeEnchantment(Enchantment.OXYGEN, 3);
			getPlayer().getInventory().setHelmet(fishHelm);
			ItemStack fishChest = PrimeUtility.getNamedStack(Material.LEATHER_CHESTPLATE, ChatColor.AQUA + "--I--", 1);
			PrimeUtility.setColorOfLeatherArmor(fishChest, Color.AQUA);
			fishChest.addUnsafeEnchantment(Enchantment.LURE, 3);
			getPlayer().getInventory().setChestplate(fishChest);
			ItemStack fishLegs = PrimeUtility.getNamedStack(Material.LEATHER_LEGGINGS, ChatColor.AQUA + "--S--", 1);
			PrimeUtility.setColorOfLeatherArmor(fishLegs, Color.AQUA);
			fishLegs.addUnsafeEnchantment(Enchantment.LUCK, 3);
			getPlayer().getInventory().setLeggings(fishLegs);
			ItemStack fishBoots = PrimeUtility.getNamedStack(Material.LEATHER_BOOTS, ChatColor.AQUA + "--H--", 1);
			PrimeUtility.setColorOfLeatherArmor(fishBoots, Color.AQUA);
			fishBoots.addUnsafeEnchantment(Enchantment.WATER_WORKER, 3);
			getPlayer().getInventory().setBoots(fishBoots);
		} else {
			getPlayer().getInventory().setItem(0, PrimeUtility.getNamedStack(playerLoadout.get(UpgradePath.Sword).get(0).material, playerLoadout.get(UpgradePath.Sword).get(0).itemDisplayName, 1));
			getPlayer().getInventory().setItem(1, PrimeUtility.getNamedStack(playerLoadout.get(UpgradePath.Bow).get(0).material, playerLoadout.get(UpgradePath.Bow).get(0).itemDisplayName, 1));
			int inventoryRow = 0;
			for(int i = 0; i < playerLoadout.get(UpgradePath.Arrow).size(); i++) {
				ShopItem si = playerLoadout.get(UpgradePath.Arrow).get(i);
				if(getArrowCount(si) > 0) {
					getPlayer().getInventory().setItem(2 + (9 * inventoryRow), PrimeUtility.getNamedStack(si.material, si.itemDisplayName, getArrowCount(si)));
					inventoryRow++;
				}
			}
			inventoryRow = 0;
			for(int i = 0; i < playerLoadout.get(UpgradePath.Offensive).size(); i++) {
				ShopItem si = playerLoadout.get(UpgradePath.Offensive).get(i);
				if(getOffensiveCount(si) > 0) {
					getPlayer().getInventory().setItem(3 + (9 * inventoryRow), PrimeUtility.getNamedStack(si.material, si.itemDisplayName, getOffensiveCount(si)));
					inventoryRow++;
				}
			}
			inventoryRow = 0;
			for(int i = 0; i < playerLoadout.get(UpgradePath.Tactical).size(); i++) {
				ShopItem si = playerLoadout.get(UpgradePath.Tactical).get(i);
				if(getTacticalCount(si) > 0) {
					getPlayer().getInventory().setItem(4 + (9 * inventoryRow), PrimeUtility.getNamedStack(si.material, si.itemDisplayName, getTacticalCount(si)));
					inventoryRow++;
				}
			}
			inventoryRow = 0;
			for(int i = 0; i < playerLoadout.get(UpgradePath.Potion_Positive).size(); i++) {
				ShopItem si = playerLoadout.get(UpgradePath.Potion_Positive).get(i);
				if(getPositivePotionCount(si) > 0 && si instanceof ShopItemPotion) {
					ShopItemPotion sip = (ShopItemPotion) si;
					ItemStack is = PrimeUtility.getNamedStack(sip.material, sip.itemDisplayName, getPositivePotionCount(sip));
					PotionMeta meta = (PotionMeta) is.getItemMeta();
					meta.clearCustomEffects();
					for(PotionEffect pe : sip.getPotionEffectsForTier(tier)) {
						meta.addCustomEffect(pe, true);
					}
					is.setItemMeta(meta);
					Potion displayPotion = new Potion(sip.getDisplayEffect());
					is.setDurability(displayPotion.toDamageValue());
					getPlayer().getInventory().setItem(5 + (9 * inventoryRow), is);
					inventoryRow++;
				}
			}

			inventoryRow = 0;
			for(int i = 0; i < playerLoadout.get(UpgradePath.Potion_Negative).size(); i++) {
				ShopItem si = playerLoadout.get(UpgradePath.Potion_Negative).get(i);
				if(getNegativePotionCount(si) > 0 && si instanceof ShopItemPotion) {
					ShopItemPotion sip = (ShopItemPotion) si;
					ItemStack is = PrimeUtility.getNamedStack(sip.material, sip.itemDisplayName, getNegativePotionCount(sip));
					PotionMeta meta = (PotionMeta) is.getItemMeta();
					for(PotionEffect pe : sip.getPotionEffectsForTier(tier)) {
						meta.addCustomEffect(pe, true);
					}
					is.setItemMeta(meta);
					Potion displayPotion = new Potion(sip.getDisplayEffect());
					displayPotion.setSplash(true);
					is.setDurability(displayPotion.toDamageValue());
					getPlayer().getInventory().setItem(6 + (9 * inventoryRow), is);
					inventoryRow++;
				}
			}
			getPlayer().getInventory().setHelmet(PrimeUtility.getNamedStack(playerLoadout.get(UpgradePath.Helmet).get(0).material, playerLoadout.get(UpgradePath.Helmet).get(0).itemDisplayName, 1));
			getPlayer().getInventory().setChestplate(PrimeUtility.getNamedStack(playerLoadout.get(UpgradePath.Chestplate).get(0).material, playerLoadout.get(UpgradePath.Chestplate).get(0).itemDisplayName, 1));
			getPlayer().getInventory().setLeggings(PrimeUtility.getNamedStack(playerLoadout.get(UpgradePath.Leggings).get(0).material, playerLoadout.get(UpgradePath.Leggings).get(0).itemDisplayName, 1));
			getPlayer().getInventory().setBoots(PrimeUtility.getNamedStack(playerLoadout.get(UpgradePath.Boots).get(0).material, playerLoadout.get(UpgradePath.Boots).get(0).itemDisplayName, 1));
			if(foodAmount > 0) { getPlayer().getInventory().setItem(8, PrimeUtility.getNamedStack(playerLoadout.get(UpgradePath.Food).get(0).material, playerLoadout.get(UpgradePath.Food).get(0).itemDisplayName, foodAmount)); }
		}
	}

	public void refreshEntireLoadout() {
		if(PrimeCraftMain.instance.game.getCurrentGameSessionID() == lastPlayedSessionID) {
			refreshPlayerGear();
			return;
		}
		lastPlayedSessionID = PrimeCraftMain.instance.game.getCurrentGameSessionID();
		offensiveCount.clear();
		for(ShopItem si : maxOffensiveCount.keySet()) {
			offensiveCount.put(si, maxOffensiveCount.get(si));
		}
		tacticalCount.clear();
		for(ShopItem si : maxTacticalCount.keySet()) {
			tacticalCount.put(si, maxTacticalCount.get(si));
		}
		arrowCount.clear();
		for(ShopItem si : maxArrowCount.keySet()) {
			arrowCount.put(si, maxArrowCount.get(si));
		}
		positivePotionCount.clear();
		for(ShopItem si : maxPositivePotionCount.keySet()) {
			positivePotionCount.put(si, maxPositivePotionCount.get(si));
		}
		negativePotionCount.clear();
		for(ShopItem si : maxNegativePotionCount.keySet()) {
			negativePotionCount.put(si, maxNegativePotionCount.get(si));
		}
		refreshPlayerGear();
	}

	public void refreshPlayerDisplayName() {
		if(getCurrentTeam() != null) {
			String prefix = ChatColor.WHITE + "";
			friendlyName = ChatColor.WHITE + "";
			if(PrimeCraftMain.instance.getPerms() != null) {
				if(PrimeCraftMain.instance.getPerms().getGroup("moderator").getPlayers().contains(getPlayerID().toString())) {
					prefix += ChatColor.WHITE + "{" + ChatColor.LIGHT_PURPLE + "MOD" + ChatColor.WHITE + "}";
				}
			}
			if(PrimeCraftMain.instance.getPerms() != null) {
				if(PrimeCraftMain.instance.getPerms().getGroup("headmoderator").getPlayers().contains(getPlayerID().toString())) {
					prefix += ChatColor.WHITE + "{" + ChatColor.DARK_PURPLE + "MOD+" + ChatColor.WHITE + "}";
				}
			}
			if(PrimeCraftMain.instance.getPerms() != null) {
				if(PrimeCraftMain.instance.getPerms().getGroup("admin").getPlayers().contains(getPlayerID().toString())) {
					prefix += ChatColor.WHITE + "{" + ChatColor.RED + "ADMIN" + ChatColor.WHITE + "}";
				}
			}
			if(PrimeCraftMain.instance.getPerms() != null) {
				if(PrimeCraftMain.instance.getPerms().getGroup("headadmin").getPlayers().contains(getPlayerID().toString())) {
					prefix += ChatColor.WHITE + "{" + ChatColor.DARK_RED + "ADMIN+" + ChatColor.WHITE + "}";
				}
			}
			if(PrimeCraftMain.instance.getPerms() != null) {
				if(PrimeCraftMain.instance.getPerms().getGroup("builder").getPlayers().contains(getPlayerID().toString())) {
					prefix += ChatColor.WHITE + "{" + ChatColor.GOLD + "BLDR" + ChatColor.WHITE + "}";
				}
			}
			if(PrimeCraftMain.instance.getPerms() != null) {
				if(PrimeCraftMain.instance.getPerms().getGroup("headbuilder").getPlayers().contains(getPlayerID().toString())) {
					prefix += ChatColor.WHITE + "{" + ChatColor.GOLD + "BLDR+" + ChatColor.WHITE + "}";
				}
			}
			if(!rebirthEchelon.equals(Echelon.NONE)) {
				prefix += ChatColor.WHITE + "†" + rebirthEchelon.getPrefix() + ChatColor.WHITE + "†";
			}
			if(tier > 0) {
				prefix += ChatColor.WHITE + "[" + PrimeUtility.getRomanFromArabic(tier) + ChatColor.WHITE + "]";
				friendlyName += ChatColor.WHITE + "[" + PrimeUtility.getRomanFromArabic(tier) + ChatColor.WHITE + "]";
			}
			ChatColor color = getCurrentTeam().getTeamDetails().getChatColor();
			friendlyName += color + getPlayer().getName() + ChatColor.WHITE;
			getPlayer().setDisplayName(prefix + color + getPlayer().getName() + ChatColor.WHITE);
			String playerListName = getPlayer().getName();
			if(playerListName.length() > 14) {
				playerListName = getPlayer().getName().substring(0, 14);
			}
			getPlayer().setPlayerListName(color + playerListName);
			updatePlayerNameInScoreboard();
		}
	}

	public void updatePlayerNameInScoreboard() {
		String prefix = getCurrentTeam().getTeamDetails().getChatColor() + "";
		if(PrimeCraftMain.instance.game.currentGamemode instanceof GamemodeCaptureTheFlag) {
			GamemodeCaptureTheFlag ctf = (GamemodeCaptureTheFlag) PrimeCraftMain.instance.game.currentGamemode;
			if(ctf.isPlayerFlagbearer(this)) {
				prefix = ChatColor.GREEN + "";
			}
		}
		if(PrimeCraftMain.instance.game.currentGamemode instanceof GamemodeArtifact) {
			GamemodeArtifact gamemodeArtifact = (GamemodeArtifact) PrimeCraftMain.instance.game.currentGamemode;
			if(gamemodeArtifact.isPlayerArtifactHolder(this)) {
				prefix = ChatColor.GREEN + "";
			}
		}
		PrimeUtility.setPlayerPrefix(getPlayer(), prefix);
	}

	//PLAYER EVENTS

	public void playerKilled(PlayerProfile playerKilled) {
		for(PlayerProfile pp : playerKilled.damagers) {
			if(!pp.equals(this)) {
				pp.playerAssistKilled(this, playerKilled, playerKilled.damagers.size());
			}
		}
		if(playerKilled.getCurrentTeam().getTeamDetails().equals(this.getCurrentTeam().getTeamDetails())) {
			playerKilled.playerDied(this);
			return;
		}
		int moneyGiven = 10;
		int disadvantage = playerKilled.getLoadoutRating() - this.getLoadoutRating();
		if(disadvantage > 0) {
			moneyGiven += (disadvantage / 3);
		} else {
			moneyGiven += (disadvantage / 5);
		}
		moneyGiven = Math.max(5, moneyGiven);
		String killedIndentity = playerKilled.getFriendlyName();
		if(PrimeCraftMain.instance.game.currentGamemode instanceof GamemodeCaptureTheFlag) {
			GamemodeCaptureTheFlag ctf = (GamemodeCaptureTheFlag) PrimeCraftMain.instance.game.currentGamemode;
			if(ctf.isPlayerFlagbearer(playerKilled)) {
				moneyGiven = moneyGiven * 2;
				killedIndentity = "the flagbearer";
			}
		}
		if(PrimeCraftMain.instance.game.currentGamemode instanceof GamemodeArtifact) {
			GamemodeArtifact artifact = (GamemodeArtifact) PrimeCraftMain.instance.game.currentGamemode;
			if(artifact.isPlayerArtifactHolder(playerKilled)) {
				moneyGiven = moneyGiven * 2;
				killedIndentity = "the Artifact carrier";
			}
		}
		changeMoneyAmount(moneyGiven, true, false);
		//Multikill and Killstreaks
		if(playerKilled.lastKilledPlayer != null) {
			if(playerKilled.lastKilledPlayer.getCurrentTeam().equals(getCurrentTeam()) && playerKilled.timeSinceLastKill < 75) {
				avenged(playerKilled.lastKilledPlayer);
			}
		}
		updateKillStreak();
		if(timeSinceLastKill < 25) {
			updateMultikills();
		} else {
			multiKillCount = 0;
		}
		timeSinceLastKill = 0;
		if(playerKilled.timeSinceInSpawn < 30) {
			addSpawnKillPoint();
			addSpawnKillPoint();
			addSpawnKillPoint();
		}
		lastKilledPlayer = playerKilled;
		playerKilled.playerDied(this);
		sendEarningMessage(ChatColor.GRAY + "You killed " + killedIndentity + " and earned %m coins", moneyGiven);
	}

	private void avenged(PlayerProfile avengedPlayer) {
		int moneyGiven = 5;
		changeMoneyAmount(moneyGiven, true, false);
		sendEarningMessage(ChatColor.GRAY + "You earned %m coins for avenging " + avengedPlayer.getFriendlyName(), moneyGiven);
	}

	public void playerAssistKilled(PlayerProfile realKiller, PlayerProfile playerKilled, int numberOfAssisters) {
		int moneyGiven = 10;
		int disadvantage = playerKilled.getLoadoutRating() - this.getLoadoutRating();
		if(disadvantage > 0) {
			moneyGiven += (disadvantage / 2);
		} else {
			moneyGiven += (disadvantage / 5);
		}
		moneyGiven = Math.max(5, moneyGiven) / numberOfAssisters;
		changeMoneyAmount(moneyGiven, true, false);
		String killerName = " ";
		if(realKiller != null) {
			killerName = " " + realKiller.getPlayer().getDisplayName() + " ";
		}
		sendEarningMessage(ChatColor.GRAY + "You assisted" + killerName + ChatColor.GRAY + "in killing " + playerKilled.getFriendlyName() + ChatColor.GRAY + " and earned %m coins.", moneyGiven);
	}

	public void playerDied(PlayerProfile killer) {
		int moneyTaken = 10;
		int advantage = this.getLoadoutRating() - killer.getLoadoutRating();
		if(advantage > 0) {
			moneyTaken += advantage;
		} else {
			moneyTaken += (advantage / 5);
		}
		moneyTaken = Math.min(5, (moneyTaken / 4) + 1);
		changeMoneyAmount(-moneyTaken, true, false);
		//Multikill and Killstreaks
		killStreak = 0;
		multiKillCount = 0;
		lastKilledPlayer = null;
		damagers.clear();
		if(killer.equals(this)) {
			getPlayer().sendMessage(ChatColor.GRAY + "You suicided and lost " + moneyTaken * 2 + " coins");
		} else {
			getPlayer().sendMessage(ChatColor.GRAY + "You were killed by " + killer.getFriendlyName() + " and lost " + moneyTaken + " coins");
		}
	}

	public void wonPlace(int place) {
		if(place == 1) {
			playerStats.addWin();
		} else {
			playerStats.addLoss();
		}
		int moneyGiven = 100;
		for(int i = 1; i < place; i++) {
			moneyGiven = moneyGiven / 2;
		}
		if(place == PrimeCraftMain.instance.game.currentGamemode.numberOfTeams) {
			moneyGiven = 0;
		}
		changeMoneyAmount(moneyGiven, true, false);
		if(moneyGiven > 0) {
			sendEarningMessage(ChatColor.GRAY + "You earned %m coins for getting " + PrimeUtility.getOrdinalFromCardinal(place) + " place.", moneyGiven);
		}
	}

	public void capturedFlag() {
		playerStats.addCapture();
		int moneyGiven = 20;
		changeMoneyAmount(moneyGiven, true, false);
		sendEarningMessage(ChatColor.GRAY + "You earned %m coins for capturing the flag.", moneyGiven);
	}

	public void carriedArtifact(int artifactPotency) {
		//playerStats.addCarry();
		int moneyGiven = 2 * artifactPotency;
		changeMoneyAmount(moneyGiven, true, false);
		sendEarningMessage(ChatColor.GRAY + "You earned %m coins for holding the Artifact.", moneyGiven);
	}

	public void capturePointProcess() {
		//Add CapturePointProcess stat
		int moneyGiven = 2;
		changeMoneyAmount(moneyGiven, true, false);
		sendEarningMessage(ChatColor.GRAY + "You earned %m coins for helping to capture a Command Post.", moneyGiven);
	}

	public void capturedPoint() {
		//Add CapturePoint stat
		int moneyGiven = 10;
		changeMoneyAmount(moneyGiven, true, false);
		sendEarningMessage(ChatColor.GRAY + "You earned %m coins for fully capturing a Command Post.", moneyGiven);
	}

	public void explodedAssaultPoint() {
		// Add Exploded stat
		int moneyGiven = 50;
		changeMoneyAmount(moneyGiven, true, false);
		sendEarningMessage(ChatColor.GRAY + "You earned %m coins for blowing up an Assault Point.", moneyGiven);
	}

	public void pickupMoneyItem(Material type) {
		int moneyGiven = MoneyItem.getValueForMaterial(type);
		if(moneyGiven > 0) {
			changeMoneyAmount(moneyGiven, true, false);
			sendEarningMessage(ChatColor.GRAY + "You earned %m coins for picking up " + type.name() + ".", moneyGiven);
		}
	}

	public void completedParkourLevel(String courseName, ParkourLevel pl) {
		if(pl.getLevelNumber() + 1 > getParkourLevel(courseName)) {
			int moneyGiven = pl.getLevelPrize();
			changeMoneyAmount(moneyGiven, true, true);
			getPlayer().sendMessage(ChatColor.GRAY + "You earned " + moneyGiven + " coins for completing this Parkour Level.");
			setParkourLevel(courseName, pl.getLevelNumber() + 1);
			playerStats.setParkourLevel(courseName, pl.getLevelNumber());
		} else {
			getPlayer().sendMessage(ChatColor.GRAY + "You already completed this Parkour Level.");
		}
	}

	private void updateKillStreak() {
		killStreak++;
		if(killStreak % 5 == 0) {
			int moneyGiven = killStreak * 4;
			changeMoneyAmount(moneyGiven, true, false);
			if(killStreak >= 10) {
				SoundManager.playKillStreak(getPlayer(), killStreak);
				Bukkit.getServer().broadcastMessage(getPlayer().getDisplayName() + " has a " + killStreak + "-kill streak.");
				PrimeUtility.spawnFireworks(getLocation(), getCurrentTeam().getTeamDetails().getDyeColor().getColor(), 4, 2.0F);
				PrimeUtility.spawnFireworks(getLocation(), Color.ORANGE, 3, 2.5F);
			}
			sendEarningMessage(ChatColor.GRAY + "You earned %m coins for getting a " + killStreak + "-kill streak.", moneyGiven);
		}
	}

	private void updateMultikills() {
		multiKillCount++;
		int moneyGiven = multiKillCount * 5;
		SoundManager.playKillStreak(getPlayer(), multiKillCount + 1);
		if(multiKillCount == 1) {
			changeMoneyAmount(moneyGiven, true, false);
			sendEarningMessage(ChatColor.GRAY + "Double Kill! Bonus: %m", moneyGiven);
		}
		if(multiKillCount == 2) {
			changeMoneyAmount(moneyGiven, true, false);
			sendEarningMessage(ChatColor.GRAY + "Triple Kill! Bonus: %m", moneyGiven);
		}
		if(multiKillCount >= 3) {
			changeMoneyAmount(moneyGiven, true, false);
			Bukkit.getServer().broadcastMessage(getPlayer().getDisplayName() + " got a " + (multiKillCount + 1) + "-Kill!");
			sendEarningMessage(ChatColor.GRAY + "" + (multiKillCount + 1) + "-Kill! Bonus: %m", moneyGiven);
			PrimeUtility.spawnFireworks(getLocation(), getCurrentTeam().getTeamDetails().getDyeColor().getColor(), 4, 2.0F);
			PrimeUtility.spawnFireworks(getLocation(), Color.ORANGE, 3, 2.5F);
		}
	}

	public void updatePlayer() {
		playerStats.addTimePlayed();
		timeSinceLastKill++;
		if(frozenTicks > 0) {
			frozenTicks--;
		}
		if(PrimeCraftMain.instance.specialEffectsHandler.getWorldTicker() % 200 == 0) {
			if(spawnKillPointCount > 0) { spawnKillPointCount--; }
		}
		if(PrimeCraftMain.instance.game.isLocationInSpawnRegion(getPlayer().getLocation())) {
			timeSinceInSpawn = 0;
		} else {
			timeSinceInSpawn++;
		}
	}

	public void onLogout() {
		killStreak = 0;
		multiKillCount = 0;
		clearDamagers(true);
	}

	//MONEY OPERATIONS

	/**
	 * Used with /giveMoney, does not check for money cap. Gives exact amount.
	 */
	public void giveMoney(Integer moneyGiven) {
		changeMoneyAmount(moneyGiven, false, true);
		if(getPlayer() != null) {
			getPlayer().sendMessage(ChatColor.GRAY + "You earned " + moneyGiven + " coins.");
			/*if(money > getLoadoutMaxMoney()) {
				getPlayer().sendMessage(ChatColor.GRAY + "You have reached the money cap for your current loadout. Upgrade your loadout to increase this limit.");
			}*/
		}
	}

	/**
	 * Use this to give or take money from a player. Prevents negative money and/or money over the limit. Also factors in tier benefits and double coins bonus.
	 * @param moneyGiven
	 * @param checkMoneyLimit
	 */
	private void changeMoneyAmount(int moneyGiven, boolean checkMoneyLimit, boolean giveExact) {
		int newMoneyAmount = money;
		if(moneyGiven > 0 && !giveExact) {
			newMoneyAmount += getMoneyWithMultipliers(moneyGiven);
		} else {
			newMoneyAmount += moneyGiven;
		}
		if((newMoneyAmount > getLoadoutMaxMoney()) && checkMoneyLimit) {
			if(money < getLoadoutMaxMoney()) {
				money = getLoadoutMaxMoney();
			}
			getPlayer().sendMessage(ChatColor.GRAY + "You have reached the money cap for your current loadout. Upgrade your loadout to increase this limit.");
		} else {
			money = newMoneyAmount;
		}
		money = Math.max(0, money);
	}

	public int getMoneyWithMultipliers(int initialMoney) {
		if(PrimeCraftMain.instance.game.bonusManager.isDoubleCoinsActive()) {
			initialMoney = initialMoney * 2;
		}
		if(tier > 0) {
			initialMoney = (int) (initialMoney * (1.0D + tier * 0.01D));
		}
		return initialMoney;
	}

	/**
	 * Sends a message to the player with the money the earned and the money they would have earned without their multipliers.
	 * NOTE: Use this only when NOT giving exact money.
	 * @param message The message to send with %m as the money amount.
	 * @param initialMoney The money earned without multipliers
	 */
	public void sendEarningMessage(String message, int initialMoney) {
		int multiplierMoney = getMoneyWithMultipliers(initialMoney);
		if(initialMoney != multiplierMoney) {
			double multiplier = Math.round(((double)multiplierMoney/(double)initialMoney) * 100.0) / 100.0;
			message = message.replace("%m", multiplierMoney + "(" + initialMoney + "x" + multiplier + ")");
		} else {
			message = message.replace("%m", initialMoney + "");
		}
		getPlayer().sendMessage(message);
	}

	/**
	 * Used when buying things from the shop
	 * @param moneyRemoved
	 */
	public void redeemMoney(int moneyRemoved) {
		changeMoneyAmount(-moneyRemoved, false, true);
	}

	//GAME FUNCTIONS

	public void cycleItemInHand() {
		int columnItemCount = 0;
		ArrayList<ItemStack> columnStackList = new ArrayList<ItemStack>();
		for(int i = 0; i < 4; i++) {
			ItemStack possibleStack = getPlayer().getInventory().getItem(getPlayer().getInventory().getHeldItemSlot() + 9 * i);
			if(possibleStack != null) {
				columnItemCount++;
				columnStackList.add(possibleStack);
				getPlayer().getInventory().setItem(getPlayer().getInventory().getHeldItemSlot() + 9 * i, null);
			}
		}
		for(int i = 0; i < columnItemCount; i++) {
			ItemStack stackToSet = columnStackList.get((i + 1) % columnItemCount);
			int itemSlot = (getPlayer().getInventory().getHeldItemSlot() + i * 9) % 36;
			getPlayer().getInventory().setItem(itemSlot, stackToSet);
		}
	}

	public void incrementMaxOffensiveCount(ShopItem offensive) {
		maxOffensiveCount.put(offensive, getMaxOffensiveCount(offensive) + 1);
	}

	public void incrementMaxTacticalCount(ShopItem tactical) {
		maxTacticalCount.put(tactical, getMaxTacticalCount(tactical) + 1);
	}

	public void incrementMaxArrowCount(ShopItem arrow) {
		maxArrowCount.put(arrow, getMaxArrowCount(arrow) + 1);
	}

	public void incrementMaxPositivePotionCount(ShopItem positivePotion) {
		maxPositivePotionCount.put(positivePotion, getMaxPositivePotionCount(positivePotion) + 1);
	}

	public void incrementMaxNegativePotionCount(ShopItem negativePotion) {
		maxNegativePotionCount.put(negativePotion, getMaxNegativePotionCount(negativePotion) + 1);
	}

	public void decrementOffensiveCount(ShopItem offensive) {
		offensiveCount.put(offensive, getOffensiveCount(offensive) - 1);
	}

	public void decrementTacticalCount(ShopItem tactical) {
		tacticalCount.put(tactical, getTacticalCount(tactical) - 1);
	}

	public void decrementArrowCount(ShopItem arrow) {
		arrowCount.put(arrow, getArrowCount(arrow) - 1);
	}

	public void decrementPositivePotionCount(ShopItem positivePotion) {
		positivePotionCount.put(positivePotion, getPositivePotionCount(positivePotion) - 1);
	}

	public void decrementNegativePotionCount(ShopItem negativePotion) {
		negativePotionCount.put(negativePotion, getNegativePotionCount(negativePotion) - 1);
	}

	public void setFoodAmount(int amount) {
		foodAmount = amount;
	}

	public void setMaxArrowCount(ShopItem arrow, int count) {
		maxArrowCount.put(arrow, count);
		if(arrow.equals(ShopItem.arrowCount)) {
			arrowCount.put(arrow, count);
		}
	}

	public void setUsingTeamChat(boolean useTeamChat) {
		isUsingTeamChat = useTeamChat;
	}

	public void setUsingDeathMessages(boolean useDeathMessages) {
		isUsingDeathMessages = useDeathMessages;
	}

	public void setUsingSounds(boolean useSounds) {
		isUsingSounds = useSounds;
	}

	public void setFrozenTicks(int ticks) {
		getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, frozenTicks * 2, 9));
		frozenTicks = ticks;
	}

	public boolean canUseEffect(PlayerEffect effect) {
		return allowedEffectsList.contains(effect);
	}

	public boolean enableEffect(PlayerEffect effect) {
		return enabledEffectsList.add(effect);
	}

	public boolean disableEffect(PlayerEffect effect) {
		return enabledEffectsList.remove(effect);
	}

	public void addAllowedEffect(PlayerEffect effect) {
		allowedEffectsList.add(effect);
	}

	public boolean removeAllowedEffect(PlayerEffect effect) {
		return allowedEffectsList.remove(effect);
	}

	public void addSpawnKillPoint() {
		spawnKillPointCount++;
		if(spawnKillPointCount >= 15) {
			spawnKillPointCount = 0;
			//getPlayer().kickPlayer(ChatColor.RED + "You have been kicked for Spawn Killing");
		}
	}

	public void addDamager(PlayerProfile damager) {
		if(!damagers.contains(damager)) {
			damagers.add(damager);
		}
	}

	public void clearDamagers(boolean forceClear) {
		if(damagers.size() > 1) {
			for(int i = 1; i < damagers.size(); i++) {
				damagers.remove(i);
			}
		}
		if(forceClear) {
			damagers.clear();
		}
	}

	public boolean isRespawning() {
		return isPlayerWaitingForRespawn;
	}

	public boolean isUsingTeamChat() {
		return isUsingTeamChat;
	}

	public boolean isUsingDeathMessages() {
		return isUsingDeathMessages;
	}

	public boolean isUsingSounds() {
		return isUsingSounds;
	}

	public boolean canPreferTeam() {
		return canPreferTeam;
	}

	public boolean isFrozen() {
		return frozenTicks > 0;
	}

	public void setItemInLoadout(ShopItem itemBought) {
		itemBought.onItemBought(this);
		if(itemBought.itemType.equals(ItemType.Upgrade)) {
			setOnlyItemInLoadout(itemBought.upgradeType, itemBought);
		} else if(itemBought.itemType.equals(ItemType.AddToLoadout)) {
			addItemToLoadout(itemBought.upgradeType, itemBought);
		}
	}

	public void setCanPreferTeam(boolean preferTeam) {
		canPreferTeam = preferTeam;
	}

	public void setTier(int newTier) {
		tier = newTier;
	}

	public void setCurrentTeam(PlayerTeam pt) {
		currentTeam = pt;
	}

	public void setIsRespawning(boolean b) {
		isPlayerWaitingForRespawn = b;
	}

	/**
	 * @param level The Parkour level the player should be set to.
	 */
	public void setParkourLevel(String course, int level) {
		playerStats.setParkourLevel(course, level - 1);
	}

	public void setLastPlayerSessionID(int id) {
		lastPlayedSessionID = id;
	}

	private int getLoadoutMaxMoney() {
		return (int) ((getLoadoutRating() * 60.0D) * (1.0D + (tier * 0.025)));
	}

	/**
	 * @return A number representing how "progressed" this player is in upgrades. Does not count tiered items or tiered status.
	 */
	public int getLoadoutRating() {
		int rating = 0;
		rating += MaterialDictionary.getValueForMaterial(playerLoadout.get(UpgradePath.Sword).get(0).material);
		rating += MaterialDictionary.getValueForMaterial(playerLoadout.get(UpgradePath.Helmet).get(0).material);
		rating += MaterialDictionary.getValueForMaterial(playerLoadout.get(UpgradePath.Chestplate).get(0).material);
		rating += MaterialDictionary.getValueForMaterial(playerLoadout.get(UpgradePath.Leggings).get(0).material);
		rating += MaterialDictionary.getValueForMaterial(playerLoadout.get(UpgradePath.Boots).get(0).material);
		rating += MaterialDictionary.getValueForMaterial(playerLoadout.get(UpgradePath.Bow).get(0).material);
		rating += MaterialDictionary.getValueForMaterial(playerLoadout.get(UpgradePath.Food).get(0).material);
		return rating;
	}

	public Player getPlayer() {
		return player;
	}

	public UUID getPlayerID() {
		return playerID;
	}

	public String getPlayerName() {
		return playerName;
	}

	public PlayerTeam getCurrentTeam() {
		return currentTeam != null ? currentTeam : TeamDetails.Neutral.getTeamObject();
	}

	public Location getLocation() {
		return new Location(getPlayer().getWorld(), getPlayer().getLocation().getX(), getPlayer().getLocation().getY(), getPlayer().getLocation().getZ());
	}

	public int getMoneyAmount() {
		return money;
	}

	public int getFoodAmount() {
		return foodAmount;
	}

	public Integer getArrowCount(ShopItem arrow) {
		if(!arrowCount.containsKey(arrow)) {
			arrowCount.put(arrow, 0);
		}
		return arrowCount.get(arrow);
	}

	public Integer getOffensiveCount(ShopItem offensive) {
		if(!offensiveCount.containsKey(offensive)) {
			offensiveCount.put(offensive, 0);
		}
		return offensiveCount.get(offensive);
	}

	public Integer getTacticalCount(ShopItem tactical) {
		if(!tacticalCount.containsKey(tactical)) {
			tacticalCount.put(tactical, 0);
		}
		return tacticalCount.get(tactical);
	}

	public Integer getPositivePotionCount(ShopItem positivePotion) {
		if(!positivePotionCount.containsKey(positivePotion)) {
			positivePotionCount.put(positivePotion, 0);
		}
		return positivePotionCount.get(positivePotion);
	}

	public Integer getNegativePotionCount(ShopItem negativePotion) {
		if(!negativePotionCount.containsKey(negativePotion)) {
			negativePotionCount.put(negativePotion, 0);
		}
		return negativePotionCount.get(negativePotion);
	}

	public Integer getMaxArrowCount(ShopItem arrow) {
		if(!maxArrowCount.containsKey(arrow)) {
			maxArrowCount.put(arrow, 0);
		}
		return maxArrowCount.get(arrow);
	}

	public Integer getMaxOffensiveCount(ShopItem offensive) {
		if(!maxOffensiveCount.containsKey(offensive)) {
			maxOffensiveCount.put(offensive, 0);
		}
		return maxOffensiveCount.get(offensive);
	}

	public Integer getMaxTacticalCount(ShopItem tactical) {
		if(!maxTacticalCount.containsKey(tactical)) {
			maxTacticalCount.put(tactical, 0);
		}
		return maxTacticalCount.get(tactical);
	}

	public Integer getMaxPositivePotionCount(ShopItem positivePotion) {
		if(!maxPositivePotionCount.containsKey(positivePotion)) {
			maxPositivePotionCount.put(positivePotion, 0);
		}
		return maxPositivePotionCount.get(positivePotion);
	}

	public Integer getMaxNegativePotionCount(ShopItem negativePotion) {
		if(!maxNegativePotionCount.containsKey(negativePotion)) {
			maxNegativePotionCount.put(negativePotion, 0);
		}
		return maxNegativePotionCount.get(negativePotion);
	}

	public ArrayList<PlayerEffect> getEnabledEffects() {
		return enabledEffectsList;
	}

	public ArrayList<PlayerEffect> getAllowedEffects() {
		return allowedEffectsList;
	}

	public int getTimeSinceInSpawn() {
		return timeSinceInSpawn;
	}

	public int getFrozenTicks() {
		return frozenTicks;
	}

	public int getTierLevel() {
		return tier;
	}

	public PlayerStats getPlayerStats() {
		return playerStats;
	}

	public ArrayList<ShopItem> getItemsForUpgradeType(UpgradePath path) {
		return playerLoadout.get(path);
	}

	public ArrayList<PlayerProfile> getDamagersIgnore(PlayerProfile ignoredPlayer) {
		ArrayList<PlayerProfile> playerList = new ArrayList<PlayerProfile>();
		for(PlayerProfile pp : damagers) {
			if(pp != ignoredPlayer) {
				playerList.add(pp);
			}
		}
		return playerList;
	}

	public int getDamagerCount() {
		return damagers.size();
	}

	public PlayerProfile getDamager(int i) {
		return damagers.get(i);
	}

	/**
	 * @return The Parkour Level the player is currently on
	 */
	public int getParkourLevel(String course) {
		return playerStats.getParkourLevel(course) + 1;
	}
	
	public String getFriendlyName() {
		return friendlyName;
	}

	@Override
	public String toString() {
		return getPlayerName();
	}

	public void loadFromConfig() {
		if(PrimeCraftMain.instance.getPlayerDataConfig().contains("Player." + getPlayerID().toString())) {
			money = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".Money");
			setOnlyItemInLoadout(UpgradePath.Sword, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".Sword")));
			setOnlyItemInLoadout(UpgradePath.Bow, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".Bow")));
			List<String> listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerID().toString() + ".Arrow");
			for(String s : listToLoad) {
				String[] parsedString = s.split(",");
				ShopItem arrow = ShopItem.getShopItemForId(Integer.parseInt(parsedString[0]));
				addItemToLoadout(UpgradePath.Arrow, arrow);
				maxArrowCount.put(arrow, Integer.parseInt(parsedString[1]));
			}
			listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerID().toString() + ".Offensive");
			for(String s : listToLoad) {
				String[] parsedString = s.split(",");
				ShopItem offensive = ShopItem.getShopItemForId(Integer.parseInt(parsedString[0]));
				addItemToLoadout(UpgradePath.Offensive, offensive);
				maxOffensiveCount.put(offensive, Integer.parseInt(parsedString[1]));
			}
			listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerID().toString() + ".Tactical");
			for(String s : listToLoad) {
				String[] parsedString = s.split(",");
				ShopItem tactical = ShopItem.getShopItemForId(Integer.parseInt(parsedString[0]));
				addItemToLoadout(UpgradePath.Tactical, tactical);
				maxTacticalCount.put(tactical, Integer.parseInt(parsedString[1]));
			}
			listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerID().toString() + ".PositivePotion");
			for(String s : listToLoad) {
				String[] parsedString = s.split(",");
				ShopItem positivePotion = ShopItem.getShopItemForId(Integer.parseInt(parsedString[0]));
				addItemToLoadout(UpgradePath.Potion_Positive, positivePotion);
				maxPositivePotionCount.put(positivePotion, Integer.parseInt(parsedString[1]));
			}
			listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerID().toString() + ".NegativePotion");
			for(String s : listToLoad) {
				String[] parsedString = s.split(",");
				ShopItem negativePotion = ShopItem.getShopItemForId(Integer.parseInt(parsedString[0]));
				addItemToLoadout(UpgradePath.Potion_Negative, negativePotion);
				maxNegativePotionCount.put(negativePotion, Integer.parseInt(parsedString[1]));
			}
			listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerID().toString() + ".Effects");
			for(String s : listToLoad) {
				PlayerEffect pe = PlayerEffect.getEffectForId(Integer.parseInt(s));
				allowedEffectsList.add(pe);
			}
			setOnlyItemInLoadout(UpgradePath.Helmet, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".Head")));
			setOnlyItemInLoadout(UpgradePath.Chestplate, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".Body")));
			setOnlyItemInLoadout(UpgradePath.Leggings, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".Legs")));
			setOnlyItemInLoadout(UpgradePath.Boots, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".Feet")));
			setOnlyItemInLoadout(UpgradePath.Food, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".Food")));
			foodAmount = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".FoodAmount");
			tier = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".Tier");
			rebirthEchelon = Echelon.getEchelonForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerID().toString() + ".RebirthEchelon"));
			canPreferTeam = PrimeCraftMain.instance.getPlayerDataConfig().getBoolean("Player." + getPlayerID().toString() + ".CanPreferTeam");

			donationAmount = PrimeCraftMain.instance.getPlayerDataConfig().getDouble("Player." + getPlayerID().toString() + ".DonationAmount");

			playerStats.loadFromConfig();
		} else if(PrimeCraftMain.instance.getPlayerDataConfig().contains("Player." + getPlayerName())) {
			money = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".Money");
			setOnlyItemInLoadout(UpgradePath.Sword, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".Sword")));
			setOnlyItemInLoadout(UpgradePath.Bow, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".Bow")));
			List<String> listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerName() + ".Arrow");
			for(String s : listToLoad) {
				String[] parsedString = s.split(",");
				ShopItem arrow = ShopItem.getShopItemForId(Integer.parseInt(parsedString[0]));
				addItemToLoadout(UpgradePath.Arrow, arrow);
				maxArrowCount.put(arrow, Integer.parseInt(parsedString[1]));
			}
			listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerName() + ".Offensive");
			for(String s : listToLoad) {
				String[] parsedString = s.split(",");
				ShopItem offensive = ShopItem.getShopItemForId(Integer.parseInt(parsedString[0]));
				addItemToLoadout(UpgradePath.Offensive, offensive);
				maxOffensiveCount.put(offensive, Integer.parseInt(parsedString[1]));
			}
			listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerName() + ".Tactical");
			for(String s : listToLoad) {
				String[] parsedString = s.split(",");
				ShopItem tactical = ShopItem.getShopItemForId(Integer.parseInt(parsedString[0]));
				addItemToLoadout(UpgradePath.Tactical, tactical);
				maxTacticalCount.put(tactical, Integer.parseInt(parsedString[1]));
			}
			listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerName() + ".PositivePotion");
			for(String s : listToLoad) {
				String[] parsedString = s.split(",");
				ShopItem positivePotion = ShopItem.getShopItemForId(Integer.parseInt(parsedString[0]));
				addItemToLoadout(UpgradePath.Potion_Positive, positivePotion);
				maxPositivePotionCount.put(positivePotion, Integer.parseInt(parsedString[1]));
			}
			listToLoad = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + getPlayerName() + ".NegativePotion");
			for(String s : listToLoad) {
				String[] parsedString = s.split(",");
				ShopItem negativePotion = ShopItem.getShopItemForId(Integer.parseInt(parsedString[0]));
				addItemToLoadout(UpgradePath.Potion_Negative, negativePotion);
				maxNegativePotionCount.put(negativePotion, Integer.parseInt(parsedString[1]));
			}
			setOnlyItemInLoadout(UpgradePath.Helmet, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".Head")));
			setOnlyItemInLoadout(UpgradePath.Chestplate, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".Body")));
			setOnlyItemInLoadout(UpgradePath.Leggings, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".Legs")));
			setOnlyItemInLoadout(UpgradePath.Boots, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".Feet")));
			setOnlyItemInLoadout(UpgradePath.Food, ShopItem.getShopItemForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".Food")));
			foodAmount = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".FoodAmount");
			tier = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".Tier");
			rebirthEchelon = Echelon.getEchelonForId(PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + getPlayerName() + ".RebirthEchelon"));
			canPreferTeam = PrimeCraftMain.instance.getPlayerDataConfig().getBoolean("Player." + getPlayerName() + ".CanPreferTeam");

			donationAmount = PrimeCraftMain.instance.getPlayerDataConfig().getDouble("Player." + getPlayerName() + ".DonationAmount");

			playerStats.loadFromConfig();
		}
	}

	public void saveToConfig() {
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Money", money);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Sword", playerLoadout.get(UpgradePath.Sword).get(0).id);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Bow", playerLoadout.get(UpgradePath.Bow).get(0).id);
		ArrayList<String> arrowLoadout = new ArrayList<String>();
		for(ShopItem si : playerLoadout.get(UpgradePath.Arrow)) {
			arrowLoadout.add(si.id + "," + getMaxArrowCount(si));
		}
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Arrow", arrowLoadout);
		ArrayList<String> offensiveLoadout = new ArrayList<String>();
		for(ShopItem si : playerLoadout.get(UpgradePath.Offensive)) {
			offensiveLoadout.add(si.id + "," + getMaxOffensiveCount(si));
		}
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Offensive", offensiveLoadout);
		ArrayList<String> tacticalLoadout = new ArrayList<String>();
		for(ShopItem si : playerLoadout.get(UpgradePath.Tactical)) {
			tacticalLoadout.add(si.id + "," + getMaxTacticalCount(si));
		}
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Tactical", tacticalLoadout);
		ArrayList<String> positivePotionLoadout = new ArrayList<String>();
		for(ShopItem si : playerLoadout.get(UpgradePath.Potion_Positive)) {
			positivePotionLoadout.add(si.id + "," + getMaxPositivePotionCount(si));
		}
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".PositivePotion", positivePotionLoadout);
		ArrayList<String> negativePotionLoadout = new ArrayList<String>();
		for(ShopItem si : playerLoadout.get(UpgradePath.Potion_Negative)) {
			negativePotionLoadout.add(si.id + "," + getMaxNegativePotionCount(si));
		}
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".NegativePotion", negativePotionLoadout);
		ArrayList<String> effectsStringList = new ArrayList<String>();
		for(PlayerEffect pe : allowedEffectsList) {
			effectsStringList.add(pe.getId() + "");
		}
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Effects", effectsStringList);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Head", playerLoadout.get(UpgradePath.Helmet).get(0).id);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Body", playerLoadout.get(UpgradePath.Chestplate).get(0).id);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Legs", playerLoadout.get(UpgradePath.Leggings).get(0).id);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Feet", playerLoadout.get(UpgradePath.Boots).get(0).id);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Food", playerLoadout.get(UpgradePath.Food).get(0).id);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".FoodAmount", foodAmount);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".Tier", tier);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".RebirthEchelon", rebirthEchelon.getRank());
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".CanPreferTeam", canPreferTeam);

		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + getPlayerID().toString() + ".DonationAmount", donationAmount);

		playerStats.saveToConfig();
	}
}
