package com.sonicjumper.primecraft.main.player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.sonicjumper.primecraft.main.PrimeCraftMain;

public class LegacyManager {
	public ArrayList<PlayerProfile> legacyList;
	
	public LegacyManager() {
		legacyList = new ArrayList<PlayerProfile>();
		loadFromConfig();
	}

	public void addNewPlayer(PlayerProfile pp) {
		legacyList.add(pp);
	}

	public PlayerProfile getLegacyProfile(Player player) {
		//Avoid NullPointerException
		if(player == null) {
			return null;
		}
		for(PlayerProfile pp : legacyList) {
			if(pp.getPlayerID().equals(player.getUniqueId())) {
				return pp;
			}
		}
		return null;
	}

	public PlayerProfile getLegacyProfile(String name) {
		for(PlayerProfile pp : legacyList) {
			if(pp.getPlayerName().equalsIgnoreCase(name)) {
				return pp;
			}
		}
		return null;
	}

	public PlayerProfile getLegacyProfile(UUID id) {
		for(PlayerProfile pp : legacyList) {
			if(pp.getPlayerID().equals(id)) {
				return pp;
			}
		}
		return null;
	}
	
	public void loadFromConfig() {
		List<String> playerNameList = PrimeCraftMain.instance.getPlayerDataConfig().getStringList("Player.LegacyPlayers");
		for(String line : playerNameList) {
			String[] args = line.split(",");
			if(args.length == 2) {
				legacyList.add(new PlayerProfile(args[0], UUID.fromString(args[1])));
			}
		}
	}
	
	public void saveToConfig() {
		ArrayList<String> playerNameList = new ArrayList<String>();
		for(PlayerProfile pp : legacyList) {
			playerNameList.add(pp.getPlayerName() + "," + pp.getPlayerID().toString());
		}
		PrimeCraftMain.instance.getPlayerDataConfig().set("Player.LegacyPlayers", playerNameList);
	}
}
