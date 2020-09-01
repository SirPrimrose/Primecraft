package com.sonicjumper.primecraft.main.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.sonicjumper.primecraft.main.PrimeCraftMain;

public class Voter {
	private static Voter voter = new Voter();
	
	private HashMap<String, Integer> playerVoteMap;
	private HashMap<String, Integer> totalVoteStats;
	
	private Random rand = new Random();
	
	public boolean isVotingOpen;
	
	private Voter() {
		playerVoteMap = new HashMap<String, Integer>();
		totalVoteStats = new HashMap<String, Integer>();
	}
	
	/**
	 * Initializes a voting session with this Voter
	 */
	public void startVoting() {
		playerVoteMap.clear();
		isVotingOpen = true;
	}
	
	public Integer playerVoteForMap(String playerName, Integer mapVotedFor) {
		return playerVoteMap.put(playerName, mapVotedFor);
	}
	
	public Integer getWinnngMap() {
		if(playerVoteMap.isEmpty()) {
			playerVoteMap.put("Player", rand.nextInt(PrimeCraftMain.instance.game.MAPS_PER_VOTE_SESSION) + 1);
		}
		isVotingOpen = false;
		HashMap<Integer, Integer> mapVoteTally = new HashMap<Integer, Integer>();
		for(Integer i : playerVoteMap.values()) {

			// Log votes in stats HashMap
			String worldVotedFor = PrimeCraftMain.instance.game.mapsForVoteSession.get(i - 1).getWorldName();
			int currentVotes = 0;
			if(totalVoteStats.containsKey(worldVotedFor)) {
				currentVotes = totalVoteStats.get(worldVotedFor);
			}
			totalVoteStats.put(worldVotedFor, currentVotes + 1);
			// End Log Votes
			
			if(mapVoteTally.get(i) == null) {
				mapVoteTally.put(i, 0);
			}
			mapVoteTally.put(i, mapVoteTally.get(i) + 1);
		}
		Integer winnerMap = 0;
		Integer winnerTally = 0;
		for(Integer i : mapVoteTally.keySet()) {
			if(mapVoteTally.get(i) > winnerTally) {
				winnerMap = i;
				winnerTally = mapVoteTally.get(i);
			}
			//Future? Do tiebreaker if votes are equal
		}
		return winnerMap;
	}
	
	public static Voter getVoter() {
		return voter;
	}
	
	public void loadStats() {
		List<String> savedStrings = PrimeCraftMain.instance.getGameConfig().getStringList("MapVoteList");
		for(String s : savedStrings) {
			String[] splitString = s.split(",");
			totalVoteStats.put(splitString[0], Integer.parseInt(splitString[1]));
		}
	}
	
	public void saveStats() {
		List<String> saveStrings = new ArrayList<String>();
		for(String s : totalVoteStats.keySet()) {
			saveStrings.add(s + "," + totalVoteStats.get(s));
		}
		PrimeCraftMain.instance.getGameConfig().set("MapVoteList", saveStrings);
	}
}
