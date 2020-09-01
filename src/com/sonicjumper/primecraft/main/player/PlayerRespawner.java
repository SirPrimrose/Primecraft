package com.sonicjumper.primecraft.main.player;

import java.util.HashMap;

public class PlayerRespawner {
	public HashMap<PlayerProfile, Integer> respawnTimeleftMap;
	
	public PlayerRespawner() {
		respawnTimeleftMap = new HashMap<PlayerProfile, Integer>();
	}
	
	public void registerPlayerForRespawn(PlayerProfile pp) {
		respawnTimeleftMap.put(pp, 10);
	}
	
	public void updateRespawner() {
		for(PlayerProfile pp : respawnTimeleftMap.keySet()) {
			int timeLeft = respawnTimeleftMap.get(pp);
			if(timeLeft > 0) {
				if(pp.isRespawning()) {
					timeLeft--;
					respawnTimeleftMap.put(pp, timeLeft);
					pp.getPlayer().sendMessage("Moving in " + timeLeft + " seconds...");
				} else {
					resetRespawner(pp);
				}
			} else {
				if(pp.getPlayer().isOnline() && pp.isRespawning()) {
					pp.getCurrentTeam().teleportPlayerToSpawn(pp);
				}
				resetRespawner(pp);
			}
		}
	}

	private void resetRespawner(PlayerProfile pp) {
		respawnTimeleftMap.remove(pp);
	}
}
