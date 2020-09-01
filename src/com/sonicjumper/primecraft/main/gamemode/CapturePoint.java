package com.sonicjumper.primecraft.main.gamemode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public class CapturePoint {
	public enum CaptureLevel {
		Low(1),
		Half(2),
		High(3),
		Full(4);
		
		public int levelInt;
		
		CaptureLevel(int level) {
			levelInt = level;
		}
		
		public static CaptureLevel getLevelForInt(int level) {
			for(CaptureLevel cl : values()) {
				if(cl.levelInt == level) {
					return cl;
				}
			}
			return null;
		}

		public static CaptureLevel getLevelForName(String string) {
			for(CaptureLevel cl : values()) {
				if(cl.name().equalsIgnoreCase(string)) {
					return cl;
				}
			}
			return null;
		}
	}
	
	private static final int MAX_ALLIANCE_STRENGTH = 250;
	
	//Allows CapturePoint to know which team it belongs to
	private TeamDetails pointAlliance;
	private Location pointLocation;
	private int pointID;
	
	private int allianceStrength;
	private boolean captureInProgress;
	private boolean allianceChanged;
	
	private TeamDetails initialTeam;
	private ArrayList<CapturePointBlock> captureBlockList;
	private Block signInfoBlock;
	private HashMap<TeamDetails, Block> teamToTPSign;
	
	public CapturePoint(Location loc, int id) {
		pointLocation = loc;
		pointID = id;
		initialTeam = TeamDetails.Neutral;
		captureBlockList = new ArrayList<CapturePointBlock>();
		teamToTPSign = new HashMap<TeamDetails, Block>();
		loadFromConfig(loc.getWorld().getName());
		pointAlliance = initialTeam;
		allianceStrength = MAX_ALLIANCE_STRENGTH;
		captureInProgress = false;
		allianceChanged = false;
		updateCapturePoint();
	}

	/**
	 * @return True if added block. False if block already existed in list.
	 */
	public boolean addBlockToList(Block clickedBlock, int level) {
		if(!isBlockRegistered(clickedBlock)) {
			captureBlockList.add(new CapturePointBlock(clickedBlock, CaptureLevel.getLevelForInt(level)));
			updateCapturePoint();
			return true;
		}
		return false;
	}

	/**
	 * @return True if removed block. False if block did not exist in list.
	 */
	public boolean removeBlockFromList(Block clickedBlock) {
		CapturePointBlock cpb = getCaptureBlockFromBlock(clickedBlock);
		if(cpb != null) {
			captureBlockList.remove(cpb);
			return true;
		}
		return false;
	}

	private boolean isBlockRegistered(Block clickedBlock) {
		for(CapturePointBlock cpb : captureBlockList) {
			if(cpb.getBlock().equals(clickedBlock)) {
				return true;
			}
		}
		return false;
	}
	
	private CapturePointBlock getCaptureBlockFromBlock(Block clickedBlock) {
		for(CapturePointBlock cpb : captureBlockList) {
			if(cpb.getBlock().equals(clickedBlock)) {
				return cpb;
			}
		}
		return null;
	}

	public void addTeamAlliance(TeamDetails details) {
		addTeamAlliance(details, 1);
	}

	/**
	 * Returns true if the command post was captured by that tick
	 * @param details
	 * @param i
	 * @return
	 */
	public boolean addTeamAlliance(TeamDetails details, int i) {
		captureInProgress = true;
		if(!details.isPlayingTeam() && !pointAlliance.equals(details) && allianceStrength >= (MAX_ALLIANCE_STRENGTH / 4)) {
			captureInProgress = false;
			return false;
		}
		if(details.equals(pointAlliance)) {
			allianceStrength += i;
		} else {
			allianceStrength -= i;
		}
		if(allianceStrength <= 0) {
			allianceStrength = 0;
			pointAlliance = details;
			allianceChanged = true;
		} else if(allianceStrength > MAX_ALLIANCE_STRENGTH) {
			allianceStrength = MAX_ALLIANCE_STRENGTH;
			captureInProgress = false;
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("deprecation")
	public void updateCapturePoint() {
		allianceChanged = false;
		for(CapturePointBlock cpb : captureBlockList) {
			if(allianceStrength >= (cpb.getActivatingLevel().levelInt * (MAX_ALLIANCE_STRENGTH / 4))) {
				if(cpb.getBlock().getData() != pointAlliance.getDyeColor().getData()) {
					setAllianceColor(cpb, pointAlliance.getDyeColor());
				}
			} else {
				if(cpb.getBlock().getData() != DyeColor.WHITE.getData()) {
					setAllianceColor(cpb, DyeColor.WHITE);
				}
			}
		}
		if(signInfoBlock != null) {
			if(signInfoBlock.getState() instanceof Sign) {
				Sign signData = (Sign) signInfoBlock.getState();
				signData.setLine(0, "Command Post " + (pointID + 1));
				signData.setLine(1, pointAlliance.getChatColor() + pointAlliance.name());
				signData.setLine(2, (int) (((allianceStrength * 1.0D) / (MAX_ALLIANCE_STRENGTH * 1.0D) * 100.0D)) + "% Captured");
				if(isFullyCaptured()) {
					signData.setLine(3, ChatColor.GREEN + "Fully Captured");
				} else {
					signData.setLine(3, ChatColor.RED + "Not Captured");
				}
				signData.update();
			} else {
				signInfoBlock.setType(Material.WALL_SIGN);
			}
		}
		for(TeamDetails team : teamToTPSign.keySet()) {
			Block tpSign = teamToTPSign.get(team);
			if(tpSign != null) {
				if(!tpSign.getType().equals(Material.WALL_SIGN)) {
					tpSign.setType(Material.WALL_SIGN);
				}
				Sign signData = (Sign) tpSign.getState();
				signData.setLine(0, "[CP Teleport " + (pointID + 1) + "]");
				signData.setLine(1, "Command Post " + (pointID + 1));
				signData.setLine(2, "Teleport Status:");
				if(pointAlliance.equals(team) && getCapturePointScoreValue() >= 3) {
					signData.setLine(3, ChatColor.GREEN + "Can Teleport");
				} else {
					signData.setLine(3, ChatColor.RED + "Not Captured");
				}
				signData.update();
			}
			Block tpSignAttached = PrimeUtility.getBlockSignAttachedTo(tpSign);
			if(tpSignAttached != null) {
				if(!tpSignAttached.getType().equals(Material.WOOL)) {
					tpSignAttached.setType(Material.WOOL);
				}
				if(tpSignAttached.getData() != pointAlliance.getDyeColor().getData()) {
					tpSignAttached.setData(pointAlliance.getDyeColor().getData());
				}
			}
		}
	}
	
	public void saveToConfig(String worldName) {
		ArrayList<String> blockSaveStrings = new ArrayList<String>();
		for(CapturePointBlock cpb : captureBlockList) {
			blockSaveStrings.add(cpb.getActivatingLevel().levelInt + "," + cpb.getBlock().getX() + "," + cpb.getBlock().getY() + "," + cpb.getBlock().getZ());
		}
		PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".CP" + pointID + ".BlockList", blockSaveStrings);
		PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".CP" + pointID + ".InfoSign.X", signInfoBlock.getX());
		PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".CP" + pointID + ".InfoSign.Y", signInfoBlock.getY());
		PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".CP" + pointID + ".InfoSign.Z", signInfoBlock.getZ());
		for(TeamDetails team : teamToTPSign.keySet()) {
			Block signBlock = teamToTPSign.get(team);
			PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".CP" + pointID + ".TPSign" + team.getTeamID() + ".X", signBlock.getX());
			PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".CP" + pointID + ".TPSign" + team.getTeamID() + ".Y", signBlock.getY());
			PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".CP" + pointID + ".TPSign" + team.getTeamID() + ".Z", signBlock.getZ());
		}
		PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".CP" + pointID + ".InitialTeam", initialTeam.name());
	}

	public void loadFromConfig(String worldName) {
		List<String> stringList = PrimeCraftMain.instance.getWorldConfig().getStringList("World." + worldName + ".CP" + pointID + ".BlockList");
		for(String s : stringList) {
			String[] coords = s.split(",");
			Block b = Bukkit.getWorld(worldName).getBlockAt(Integer.parseInt(coords[1]), Integer.parseInt(coords[2]), Integer.parseInt(coords[3]));
			addBlockToList(b, Integer.parseInt(coords[0]));
		}
		int infoSignX = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".CP" + pointID + ".InfoSign.X");
		int infoSignY = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".CP" + pointID + ".InfoSign.Y");
		int infoSignZ = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".CP" + pointID + ".InfoSign.Z");
		signInfoBlock = pointLocation.getWorld().getBlockAt(infoSignX, infoSignY, infoSignZ);
		for(TeamDetails team : TeamDetails.values()) {
			if(PrimeCraftMain.instance.getWorldConfig().contains("World." + worldName + ".CP" + pointID + ".TPSign" + team.getTeamID() + ".X")) {
				int teleportingSignX = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".CP" + pointID + ".TPSign" + team.getTeamID() + ".X");
				int teleportingSignY = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".CP" + pointID + ".TPSign" + team.getTeamID() + ".Y");
				int teleportingSignZ = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".CP" + pointID + ".TPSign" + team.getTeamID() + ".Z");
				Block signBlock = pointLocation.getWorld().getBlockAt(teleportingSignX, teleportingSignY, teleportingSignZ);
				teamToTPSign.put(team, signBlock);
			}
		}
		if(PrimeCraftMain.instance.getWorldConfig().contains("World." + worldName + ".CP" + pointID + ".InitialTeam")) {
			try {
				String teamName = PrimeCraftMain.instance.getWorldConfig().getString("World." + worldName + ".CP" + pointID + ".InitialTeam");
				initialTeam = TeamDetails.getDetailsForName(teamName);
			} catch(Exception e) {}
		}
	}

	public void resetPoint() {
		for(CapturePointBlock cpb : captureBlockList) {
			setAllianceColor(cpb, DyeColor.WHITE);
		}
		for(TeamDetails team : teamToTPSign.keySet()) {
			Block tpSign = teamToTPSign.get(team);
			Block tpSignAttached = PrimeUtility.getBlockSignAttachedTo(tpSign);
			if(tpSignAttached != null) {
				if(!tpSignAttached.getType().equals(Material.WOOL)) {
					tpSignAttached.setType(Material.WOOL);
				}
				tpSignAttached.setData(DyeColor.WHITE.getData());
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public void setAllianceColor(CapturePointBlock cpb, DyeColor color) {
		if(!cpb.getBlock().getType().equals(Material.WOOL) && !cpb.getBlock().getType().equals(Material.STAINED_CLAY) && !cpb.getBlock().getType().equals(Material.STAINED_GLASS) && !cpb.getBlock().getType().equals(Material.STAINED_GLASS_PANE)) {
			cpb.getBlock().setType(Material.WOOL);
		}
		cpb.getBlock().setData(color.getData());
	}

	public void setInfoSign(Block clickedBlock) {
		signInfoBlock = clickedBlock;
		updateCapturePoint();
	}

	public void setTeleportingSign(TeamDetails team, Block clickedBlock) {
		teamToTPSign.put(team, clickedBlock);
		updateCapturePoint();
	}

	public void setInitialTeam(TeamDetails team) {
		initialTeam = team;
		updateCapturePoint();
	}

	public boolean isBeingCaptured() {
		return captureInProgress;
	}

	public boolean isFullyCaptured() {
		return allianceStrength == MAX_ALLIANCE_STRENGTH;
	}

	public boolean didAllianceChange() {
		return allianceChanged;
	}
	
	public int getCapturePointScoreValue() {
		return allianceStrength / (MAX_ALLIANCE_STRENGTH / 4);
	}
	
	/**
	 * @return The central Location of the point, ready for manipulation
	 */
	public Location getLocation() {
		return new Location(pointLocation.getWorld(), pointLocation.getX() + 0.5D, pointLocation.getY() + 0.5D, pointLocation.getZ() + 0.5D);
	}

	public TeamDetails getAlliance() {
		return pointAlliance;
	}
	
	@Override
	public String toString() {
		return captureBlockList.toString();
	}

	public int getID() {
		return pointID;
	}
}
