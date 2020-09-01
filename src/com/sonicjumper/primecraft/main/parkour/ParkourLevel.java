package com.sonicjumper.primecraft.main.parkour;

import org.bukkit.Location;

public class ParkourLevel {
	private String courseName;
	private Location spawnLocation;
	private int levelNumber;
	private int prizeAmount;
	
	public ParkourLevel(String course, int level, int prize, Location spawn) {
		courseName = course;
		spawnLocation = spawn;
		levelNumber = level;
		prizeAmount = prize;
	}
	
	public String getCourseName() {
		return courseName;
	}
	
	public Location getLevelSpawn() {
		return spawnLocation;
	}
	
	public int getLevelNumber() {
		return levelNumber;
	}
	
	public int getLevelPrize() {
		return prizeAmount;
	}
}
