package com.sonicjumper.primecraft.main.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.parkour.ParkourManager;

public class PlayerStats {
	private PlayerProfile referencedProfile;
	private UUID playerID;
	
	private int kills;
	private int deaths;
	private int wins;
	private int losses;
	private int captures;
	private long totalTimePlayed;
	private double damageDealt;
	private double damageTaken;
	private HashMap<String, Integer> parkourCourseToLevel;
	private List<Integer> prizesWon;

	private int voteCount;
	
	public PlayerStats(PlayerProfile profile, UUID id) {
		referencedProfile = profile;
		playerID = id;
		parkourCourseToLevel = new HashMap<String, Integer>();
		prizesWon = new LinkedList<Integer>();
	}
	
	public void addVote() {
		voteCount++;
		if(referencedProfile.getPlayer() != null && referencedProfile.getPlayer().isOnline()) {
			referencedProfile.getPlayer().sendMessage(ChatColor.GREEN + "Thank you for voting! Enjoy these coins!");
		}
		referencedProfile.giveMoney(200);
		if(voteCount % 10 == 0) {
			int moneyGiven = Math.min(1000, voteCount * 10);
			referencedProfile.getPlayer().sendMessage(ChatColor.GREEN + "" + voteCount + " votes! Have an extra " + moneyGiven + " coins.");
			referencedProfile.giveMoney(moneyGiven);
		}
	}

	public void addKill() {
		kills++;
	}
	
	public void addDeath() {
		deaths++;
	}
	
	public void addWin() {
		wins++;
	}
	
	public void addLoss() {
		losses++;
	}
	
	public void addCapture() {
		captures++;
	}
	
	public void addDamageDealt(double damage) {
		damageDealt += damage;
	}
	
	public void addDamageTaken(double damage) {
		damageTaken += damage;
	}

	public void addTimePlayed() {
		totalTimePlayed++;
	}
	
	public boolean doesHavePrize(Integer prizeID) {
		return prizesWon.contains(prizeID);
	}
	
	public void addPrize(Integer prizeID) {
		prizesWon.add(prizeID);
	}
	
	public void setParkourLevel(String course, int level) {
		parkourCourseToLevel.put(course, level);
	}

	public int getParkourLevel(String course) {
		if(ParkourManager.doesCourseExist(course)) {
			for(String s : parkourCourseToLevel.keySet()) {
				if(s.equalsIgnoreCase(course)) {
					return parkourCourseToLevel.get(s);
				}
			}
			return 0;
		}
		return -1;
	}

	public void loadFromConfig() {
		if(PrimeCraftMain.instance.getPlayerDataConfig().contains("Player." + playerID.toString() + ".Stats")) {
			kills = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + playerID.toString() + ".Stats.Kills");
			deaths = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + playerID.toString() + ".Stats.Deaths");
			wins = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + playerID.toString() + ".Stats.Wins");
			losses = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + playerID.toString() + ".Stats.Losses");
			captures = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + playerID.toString() + ".Stats.Captures");
			List<String> courseArray = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player." + playerID.toString() + ".Stats.Parkour.Courses");
			for(String s : courseArray) {
				int level = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + playerID.toString() + ".Stats.Parkour." + s + ".Level");
				parkourCourseToLevel.put(s, level);
			}
			totalTimePlayed = PrimeCraftMain.instance.getPlayerDataConfig().getLong("Player." + playerID.toString() + ".Stats.TimePlayed");
			damageDealt = PrimeCraftMain.instance.getPlayerDataConfig().getDouble("Player." + playerID.toString() + ".Stats.DamageDealt");
			damageTaken = PrimeCraftMain.instance.getPlayerDataConfig().getDouble("Player." + playerID.toString() + ".Stats.DamageTaken");
			prizesWon = PrimeCraftMain.instance.getPlayerDataConfig().getIntegerList("Player." + playerID.toString() + ".Stats.Prizes");
			voteCount = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + playerID.toString() + ".Stats.VoteCount");
		} else if(PrimeCraftMain.instance.getPlayerDataConfig().contains("Player." + referencedProfile.getPlayerName() + ".Stats")) {
			kills = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + referencedProfile.getPlayerName() + ".Stats.Kills");
			deaths = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + referencedProfile.getPlayerName() + ".Stats.Deaths");
			wins = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + referencedProfile.getPlayerName() + ".Stats.Wins");
			losses = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + referencedProfile.getPlayerName() + ".Stats.Losses");
			captures = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + referencedProfile.getPlayerName() + ".Stats.Captures");
			//parkourLevel = PrimeCraftMain.instance.getPlayerDataConfig().getInt("Player." + referencedProfile.getPlayerName() + ".Stats.ParkourLevel");
			totalTimePlayed = PrimeCraftMain.instance.getPlayerDataConfig().getLong("Player." + referencedProfile.getPlayerName() + ".Stats.TimePlayed");
			damageDealt = PrimeCraftMain.instance.getPlayerDataConfig().getDouble("Player." + referencedProfile.getPlayerName() + ".Stats.DamageDealt");
			damageTaken = PrimeCraftMain.instance.getPlayerDataConfig().getDouble("Player." + referencedProfile.getPlayerName() + ".Stats.DamageTaken");
			prizesWon = PrimeCraftMain.instance.getPlayerDataConfig().getIntegerList("Player." + referencedProfile.getPlayerName() + ".Stats.Prizes");
		}
	}

	public void saveToConfig() {
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.Kills", kills);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.Deaths", deaths);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.Wins", wins);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.Losses", losses);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.Captures", captures);
		List<String> courseArray = new ArrayList<String>();
		for(String s : parkourCourseToLevel.keySet()) {
			courseArray.add(s);
			PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.Parkour." + s + ".Level", parkourCourseToLevel.get(s));
		}
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.Parkour.Courses", courseArray);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.TimePlayed", totalTimePlayed);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.DamageDealt", damageDealt);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.DamageTaken", damageTaken);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.Prizes", prizesWon);
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player." + playerID.toString() + ".Stats.VoteCount", voteCount);
	}
	
	private String getTimeString() {
		int seconds = (int) ((totalTimePlayed / 10) % 60);
		int minutes = (int) ((totalTimePlayed / 600) % 60);
		int hours = (int) ((totalTimePlayed / 36000) % 24);
		int days = (int) (totalTimePlayed / 864000);
		String timeString = "";
		timeString += days > 0 ? days + "d" : "";
		timeString += hours > 0 ? hours + "h" : "";
		timeString += minutes > 0 ? minutes + "m" : "";
		timeString += seconds + "s";
		return timeString;
	}

	public String[] getStatsMessage() {
		String[] statsMessages = new String[] {
				"Player Name: " + referencedProfile.getPlayerName(),
				"- Kills: " + kills,
				"- Deaths: " + deaths,
				"- K/D Ratio: " + ((float) kills)/((float) deaths),
				"- Wins: " + wins,
				"- Losses: " + losses,
				"- Flag Captures: " + captures,
				//"- Completed Parkour Levels: " + parkourLevel,
				"- Damage Dealt: " + Math.round(damageDealt),
				"- Damage Taken: " + Math.round(damageTaken),
				"- Total Time Played: " + getTimeString()
		};
		return statsMessages;
	}
}
