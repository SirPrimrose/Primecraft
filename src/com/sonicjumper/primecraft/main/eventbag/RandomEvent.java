package com.sonicjumper.primecraft.main.eventbag;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;

public abstract class RandomEvent {
	protected enum RandomEventType {
		Negative,
		Positive;
	}
	
	protected enum RandomEventRarity {
		Common(20),
		Uncommon(15),
		Rare(10),
		SuperRare(5),
		UltraRare(3),
		Legendary(1);
		
		private int numberOfEntries;
		
		private RandomEventRarity(int entries) {
			numberOfEntries = entries;
		}
		
		public int getNumberOfEntries() {
			return numberOfEntries;
		}
	}
	
	protected static ArrayList<Arrow> instaKillArrows = new ArrayList<Arrow>();
	
	public static ArrayList<RandomEvent> randomEventList = new ArrayList<RandomEvent>();
	
	//Ideas for Random Events
	// - Players turn into a block for a couple seconds
	// - Player drops their sword for a set time
	// - Player loses armor for a set time
	
	public static RandomEvent combustPlayerEvent = new RandomEventCombustPlayer(RandomEventType.Negative, RandomEventRarity.Common, 1, 3);
	public static RandomEvent healPlayerEvent = new RandomEventHealPlayer(RandomEventType.Positive, RandomEventRarity.Common, 1, 3);
	public static RandomEvent gainSpeedEvent = new RandomEventGainSpeed(RandomEventType.Positive, RandomEventRarity.Common, 1, 5);
	public static RandomEvent lightningStrikeEvent = new RandomEventLightningStrike(RandomEventType.Negative, RandomEventRarity.Uncommon, 1, 1);
	public static RandomEvent smallHordeAttackEvent = new RandomEventHordeAttack(RandomEventType.Negative, RandomEventRarity.Uncommon, 2, 4);
	public static RandomEvent seniorCitizenEvent = new RandomEventSeniorCitizen(RandomEventType.Negative, RandomEventRarity.Uncommon, 1, 1);
	public static RandomEvent arrowStormEvent = new RandomEventArrowStorm(RandomEventType.Positive, RandomEventRarity.Uncommon, 1, 2);
	public static RandomEvent arrowBarrageEvent = new RandomEventArrowBarrage(RandomEventType.Positive, RandomEventRarity.Uncommon, 1, 2);
	public static RandomEvent singleGoldEvent = new RandomEventDropCoins(RandomEventType.Positive, RandomEventRarity.Rare, 1, 1);
	//public static RandomEvent anvilFallEvent = new RandomEventAnvilFall(RandomEventType.Negative, RandomEventRarity.Rare, 1, 1);
	public static RandomEvent combustGroupEvent = new RandomEventCombustPlayer(RandomEventType.Negative, RandomEventRarity.Rare, 5, 10);
	//public static RandomEvent explodePlayerEvent = new RandomEventExplodePlayer(RandomEventType.Negative, RandomEventRarity.Rare, 1, 1);
	public static RandomEvent armorThornsEvent = new RandomEventAddArmorEnchant(RandomEventType.Positive, RandomEventRarity.SuperRare, 1, 3, Enchantment.THORNS, " had their armor sharpened.");
	public static RandomEvent armorProtectionEvent = new RandomEventAddArmorEnchant(RandomEventType.Positive, RandomEventRarity.SuperRare, 1, 3, Enchantment.PROTECTION_ENVIRONMENTAL, " had their armor fortified.");
	public static RandomEvent lightningStormEvent = new RandomEventLightningStorm(RandomEventType.Negative, RandomEventRarity.SuperRare, 5, 10);
	public static RandomEvent heroEvent = new RandomEventHero(RandomEventType.Positive, RandomEventRarity.SuperRare, 1, 1);
	public static RandomEvent artemisBowEvent = new RandomEventGiveArtemisBow(RandomEventType.Positive, RandomEventRarity.SuperRare, 1, 1);
	public static RandomEvent plagueEvent = new RandomEventPlague(RandomEventType.Negative, RandomEventRarity.UltraRare, 5, 10);
	public static RandomEvent hordeAttackEvent = new RandomEventHordeAttack(RandomEventType.Negative, RandomEventRarity.UltraRare, 10, 20);
	public static RandomEvent weaponSharpnessEvent = new RandomEventAddWeaponEnchant(RandomEventType.Positive, RandomEventRarity.UltraRare, 1, 3, Enchantment.DAMAGE_ALL, " sharpened their sword.");
	public static RandomEvent weaponKnockbackEvent = new RandomEventAddWeaponEnchant(RandomEventType.Positive, RandomEventRarity.UltraRare, 1, 3, Enchantment.KNOCKBACK, " got some batting lessons.");
	public static RandomEvent weaponFireAspectEvent = new RandomEventAddWeaponEnchant(RandomEventType.Positive, RandomEventRarity.UltraRare, 1, 3, Enchantment.FIRE_ASPECT, " gained the power of fire.");
	public static RandomEvent potOfGoldEvent = new RandomEventDropCoins(RandomEventType.Positive, RandomEventRarity.Legendary, 5, 10);
	
	private static Random rand = new Random();
	
	private RandomEventType eventType;
	
	private int minAffectedPlayers;
	private int maxAffectedPlayers;
	
	public RandomEvent(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		eventType = type;
		minAffectedPlayers = minPlayers;
		maxAffectedPlayers = maxPlayers;
		
		for(int i = 0; i < rarity.getNumberOfEntries(); i++) {
			randomEventList.add(this);
		}
	}
	
	public RandomEventType getEventType() {
		return eventType;
	}

	public int getMinAffectedPlayers() {
		return minAffectedPlayers;
	}

	public int getMaxAffectedPlayers() {
		return maxAffectedPlayers;
	}
	
	/**
	 * Calls activateEventOnPlayer() for each player passed in. All players are gauranteed to be on playing teams and not in spawns.
	 * @param affectedPlayers
	 */
	public void onEvent(ArrayList<PlayerProfile> affectedPlayers) {
		for(PlayerProfile pp : affectedPlayers) {
			activateEventOnPlayer(pp);
		}
	}
	
	protected void broadcastEventMessage(String playerName, String message) {
		String prefix = ChatColor.GREEN + "[" + ChatColor.AQUA + "RandEvent" + ChatColor.GREEN + "] " + ChatColor.WHITE;
		if(eventType.equals(RandomEventType.Negative)) {
			Bukkit.getServer().broadcastMessage(prefix + playerName + ChatColor.DARK_RED + message);
		} else {
			Bukkit.getServer().broadcastMessage(prefix + playerName + ChatColor.LIGHT_PURPLE + message);
		}
	}
	
	protected abstract void activateEventOnPlayer(PlayerProfile pp);

	public static boolean isArrowInstaKill(Arrow a) {
		boolean instakill = instaKillArrows.contains(a);
		for(int i = 0; i < instaKillArrows.size(); i++) {
			if(instaKillArrows.get(i).isDead()) {
				instaKillArrows.remove(i);
			}
		}
		return instakill;
	}

	public static RandomEvent getRandomEvent() {
		return randomEventList.get(rand.nextInt(randomEventList.size()));
	}

	public static void activateEvent(RandomEvent event) {
		int minPlayers = event.getMinAffectedPlayers();
		int maxPlayers = event.getMaxAffectedPlayers();
		int affectedPlayerCount = 0;
		if(maxPlayers - minPlayers == 0) {
			affectedPlayerCount = minPlayers;
		} else {
			affectedPlayerCount = rand.nextInt(maxPlayers - minPlayers) + minPlayers;
		}
		ArrayList<PlayerProfile> affectedPlayers = new ArrayList<PlayerProfile>();
		for(int i = 0; i < affectedPlayerCount; i++) {
			PlayerProfile pp = null;
			int attempts = 10;
			do {
				if(PrimeCraftMain.instance.game.getPlayers().size() > 0) {
					PlayerProfile possiblePlayer = PrimeCraftMain.instance.game.getRandomPlayer(true);
					if(possiblePlayer != null) {
						pp = possiblePlayer;
					}
				}
				attempts--;
			} while(pp == null && attempts != 0);
			if(pp != null && !affectedPlayers.contains(pp)) {
				affectedPlayers.add(pp);
			}
		}
		activateEventForPlayers(event, affectedPlayers);
	}
	
	public static void activateEventForPlayers(RandomEvent event, ArrayList<PlayerProfile> players) {
		event.onEvent(players);
	}
}
