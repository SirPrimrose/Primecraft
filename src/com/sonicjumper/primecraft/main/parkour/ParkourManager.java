package com.sonicjumper.primecraft.main.parkour;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ParkourManager {
	private static ArrayList<ParkourLevel> levelList = new ArrayList<ParkourLevel>();
	
	public static void teleportPlayerToParkour(String course, PlayerProfile pp) {
		ParkourLevel pl = getParkourLevelForNumAndCourse(course, pp.getParkourLevel(course));
		if(pl != null) {
			pp.getPlayer().teleport(pl.getLevelSpawn());
		} else if(!getUniqueCourseNames().contains(course)) {
			pp.getPlayer().sendMessage("This course does not exist. Available courses:");
			for(String courseName : getUniqueCourseNames()) {
				pp.getPlayer().sendMessage(courseName);
			}
			pp.getCurrentTeam().teleportPlayerToSpawn(pp);
		} else {
			pp.getCurrentTeam().teleportPlayerToSpawn(pp);
		}
	}

	public static void addNewParkourLevel(String course, int level, int prize, Location spawn) {
		if(getParkourLevelForNumAndCourse(course, level) != null) {
			levelList.remove(getParkourLevelForNumAndCourse(course, level));
		}
		levelList.add(new ParkourLevel(course, level, prize, spawn));
	}

	public static ParkourLevel getParkourLevelForNumAndCourse(String course, int parkourLevel) {
		for(ParkourLevel pl : levelList) {
			if(pl.getLevelNumber() == parkourLevel && pl.getCourseName().equalsIgnoreCase(course)) {
				return pl;
			}
		}
		return null;
	}

	public static ArrayList<String> getUniqueCourseNames() {
		ArrayList<String> courseNames = new ArrayList<String>();
		for(ParkourLevel pl : levelList) {
			if(!courseNames.contains(pl.getCourseName())) {
				courseNames.add(pl.getCourseName());
			}
		}
		return courseNames;
	}

	public static boolean doesCourseExist(String course) {
		for(String s : getUniqueCourseNames()) {
			if(s.equalsIgnoreCase(course)) {
				return true;
			}
		}
		return false;
	}
	
	public static void saveToConfig() {
		ArrayList<String> saveStrings = new ArrayList<String>();
		for(ParkourLevel pl : levelList) {
			String saveString = pl.getCourseName() + "," + pl.getLevelNumber() + "," + pl.getLevelPrize() + "," + pl.getLevelSpawn().getX() + "," + pl.getLevelSpawn().getY() + "," + pl.getLevelSpawn().getZ() + "," + pl.getLevelSpawn().getYaw() + "," + pl.getLevelSpawn().getPitch();
			saveStrings.add(saveString);
		}
		PrimeCraftMain.instance.getParkourConfig().set("Parkour.ParkourLevels", saveStrings);
	}
	
	public static void loadFromConfig() {
		List<String> stringsToLoad = PrimeCraftMain.instance.getParkourConfig().getStringList("Parkour.ParkourLevels");
		for(String s : stringsToLoad) {
			String[] delims = s.split(",");
			levelList.add(new ParkourLevel(delims[0], Integer.parseInt(delims[1]), Integer.parseInt(delims[2]), new Location(Bukkit.getWorld("Parkour"), Double.parseDouble(delims[3]), Double.parseDouble(delims[4]), Double.parseDouble(delims[5]), Float.parseFloat(delims[6]), Float.parseFloat(delims[7]))));
		}
	}
}
