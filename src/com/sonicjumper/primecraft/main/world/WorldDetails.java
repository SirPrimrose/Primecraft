package com.sonicjumper.primecraft.main.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;

public class WorldDetails {
	public static void setLobbySpawn(Location loc) {
		PrimeCraftMain.instance.getWorldConfig().set("World.LobbyName", loc.getWorld().getName());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".Lobby.X", loc.getX());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".Lobby.Y", loc.getY());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".Lobby.Z", loc.getZ());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".Lobby.Yaw", loc.getYaw());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".Lobby.Pitch", loc.getPitch());
	}
	
	public static void setTeamSpawn(Location loc, int teamNumber) {
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Spawn.X", loc.getX());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Spawn.Y", loc.getY());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Spawn.Z", loc.getZ());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Spawn.Yaw", loc.getYaw());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Spawn.Pitch", loc.getPitch());
	}
	
	public static void setTeamFlagSpawn(Location loc, int teamNumber) {
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Flag.X", loc.getBlockX());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Flag.Y", loc.getBlockY());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Flag.Z", loc.getBlockZ());
	}
	
	public static void setArtifactSpawn(Location loc) {
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".Artifact.X", loc.getBlockX());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".Artifact.Y", loc.getBlockY());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".Artifact.Z", loc.getBlockZ());
	}
	
	public static void setCapturePointCount(Location loc, int hillCount) {
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".CapturePointCount", hillCount);
	}
	
	public static void setCapturePointLocation(Location loc, int hillNumber) {
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".CapturePoint.CP" + hillNumber + ".X", loc.getBlockX());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".CapturePoint.CP" + hillNumber + ".Y", loc.getBlockY());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".CapturePoint.CP" + hillNumber + ".Z", loc.getBlockZ());
	}
	
	public static void setAssaultPointCount(Location loc, int hillCount) {
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".AssaultPointCount", hillCount);
	}
	
	public static void setAssaultPointLocation(Location loc, int id) {
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".AssaultPoint.AP" + id + ".X", loc.getBlockX());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".AssaultPoint.AP" + id + ".Y", loc.getBlockY());
		PrimeCraftMain.instance.getWorldConfig().set("World." + loc.getWorld().getName() + ".AssaultPoint.AP" + id + ".Z", loc.getBlockZ());
	}
	
	public static Location getLobbySpawn() {
		String worldName = PrimeCraftMain.instance.getWorldConfig().getString("World.LobbyName");
		double x = PrimeCraftMain.instance.getWorldConfig().getDouble("World." + worldName + ".Lobby.X");
		double y = PrimeCraftMain.instance.getWorldConfig().getDouble("World." + worldName + ".Lobby.Y");
		double z = PrimeCraftMain.instance.getWorldConfig().getDouble("World." + worldName + ".Lobby.Z");
		float yaw = (float) PrimeCraftMain.instance.getWorldConfig().getDouble("World." + worldName + ".Lobby.Yaw");
		float pitch = (float) PrimeCraftMain.instance.getWorldConfig().getDouble("World." + worldName + ".Lobby.Pitch");
		if(worldName != null) {
			return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
		} else {
			return null;
		}
	}
	
	public static Location getTeamSpawn(String worldName, int teamNumber) {
		double x = PrimeCraftMain.instance.getWorldConfig().getDouble("World." + worldName + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Spawn.X");
		double y = PrimeCraftMain.instance.getWorldConfig().getDouble("World." + worldName + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Spawn.Y");
		double z = PrimeCraftMain.instance.getWorldConfig().getDouble("World." + worldName + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Spawn.Z");
		float yaw = (float) PrimeCraftMain.instance.getWorldConfig().getDouble("World." + worldName + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Spawn.Yaw");
		float pitch = (float) PrimeCraftMain.instance.getWorldConfig().getDouble("World." + worldName + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Spawn.Pitch");
		return new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch);
	}

	public static Location getTeamFlagSpawn(String worldName, int teamNumber) {
		int x = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Flag.X");
		int y = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Flag.Y");
		int z = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + "." + TeamDetails.getDetailsForID(teamNumber).name() + ".Flag.Z");
		return new Location(Bukkit.getWorld(worldName), x, y, z);
	}

	public static Location getArtifactSpawn(String worldName) {
		int x = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".Artifact.X");
		int y = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".Artifact.Y");
		int z = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".Artifact.Z");
		return new Location(Bukkit.getWorld(worldName), x, y, z);
	}

	public static int getCapturePointCount(String worldName) {
		int capturePointCount = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".CapturePointCount");
		return capturePointCount;
	}

	public static Location getCapturePointLocation(String worldName, int hillNumber) {
		int x = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".CapturePoint.CP" + hillNumber + ".X");
		int y = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".CapturePoint.CP" + hillNumber + ".Y");
		int z = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".CapturePoint.CP" + hillNumber + ".Z");
		return new Location(Bukkit.getWorld(worldName), x, y, z);
	}

	public static int getAssaultPointCount(String worldName) {
		int assaultPointCount = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AssaultPointCount");
		return assaultPointCount;
	}

	public static Location getAssaultPointLocation(String worldName, int pointNumber) {
		int x = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AssaultPoint.AP" + pointNumber + ".X");
		int y = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AssaultPoint.AP" + pointNumber + ".Y");
		int z = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AssaultPoint.AP" + pointNumber + ".Z");
		return new Location(Bukkit.getWorld(worldName), x, y, z);
	}
}
