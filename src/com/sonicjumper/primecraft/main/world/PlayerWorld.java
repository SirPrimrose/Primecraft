package com.sonicjumper.primecraft.main.world;

import com.sonicjumper.primecraft.main.gamemode.Gamemode;

public class PlayerWorld {
	private String worldName;
	private String worldDescription;
	private String authorName;
	private String gamemodeName;
	private int preferredTime;
	
	public PlayerWorld(String world, String desc, String author, String gamemode, int prefTime) {
		worldName = world;
		worldDescription = desc;
		authorName = author;
		gamemodeName = gamemode;
		preferredTime = prefTime;
	}

	public boolean hasPreferredTime() {
		return preferredTime >= 0;
	}
	
	public String getWorldName() {
		return worldName;
	}
	
	public String getDescription() {
		return worldDescription;
	}
	
	public String getAuthor() {
		return authorName;
	}
	
	public Gamemode getGamemode() {
		return Gamemode.getGamemodeForName(gamemodeName);
	}
	
	public int getPreferredTime() {
		return preferredTime;
	}
	
	@Override
	public String toString() {
		return worldName + "/" + gamemodeName + "/" + preferredTime;
	}
}
