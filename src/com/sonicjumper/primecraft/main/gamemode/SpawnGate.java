package com.sonicjumper.primecraft.main.gamemode;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;

public class SpawnGate {
	public enum GateType {
		Respawn,
		Game;
	}
	//Allows SpawnGate to know which team it belongs to
	private TeamDetails referenceTeamDetails;
	
	private ArrayList<Block> blockList;
	
	private GateType gateType;
	
	public SpawnGate(TeamDetails teamDetails, String worldName, GateType type) {
		referenceTeamDetails = teamDetails;
		blockList = new ArrayList<Block>();
		gateType = type;
		loadFromConfig(worldName);
	}

	/**
	 * @return True if added block. False if block already existed in list.
	 */
	public boolean addBlockToList(Block clickedBlock) {
		if(!blockList.contains(clickedBlock)) {
			blockList.add(clickedBlock);
			return true;
		}
		return false;
	}
	
	/**
	 * @return True if removed block. False if block did not exist in list.
	 */
	public boolean removeBlockFromList(Block clickedBlock) {
		if(blockList.contains(clickedBlock)) {
			blockList.remove(clickedBlock);
			return true;
		}
		return false;
	}
	
	public void saveToConfig(String worldName) {
		ArrayList<String> blockSaveStrings = new ArrayList<String>();
		for(Block b : blockList) {
			blockSaveStrings.add(b.getX() + "," + b.getY() + "," + b.getZ());
		}
		PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + "." + referenceTeamDetails.name() + "." + gateType.name(), blockSaveStrings);
	}

	public void loadFromConfig(String worldName) {
		List<String> stringList = PrimeCraftMain.instance.getWorldConfig().getStringList("World." + worldName + "." + referenceTeamDetails.name() + "." + gateType.name());
		for(String s : stringList) {
			String[] coords = s.split(",");
			Block b = Bukkit.getWorld(worldName).getBlockAt(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]), Integer.parseInt(coords[2]));
			addBlockToList(b);
		}
	}

	public void openGate() {
		for(Block b : blockList) {
			b.setType(Material.AIR);
		}
	}
	
	@SuppressWarnings("deprecation")
	public void closeGate() {
		for(Block b: blockList) {
			b.setType(Material.STAINED_GLASS_PANE);
			b.setData(referenceTeamDetails.getDyeColor().getData());
		}
	}
	
	public TeamDetails getTeamDetails() {
		return referenceTeamDetails;
	}
	
	public GateType getGateType() {
		return gateType;
	}
	
	@Override
	public String toString() {
		return blockList.toString();
	}
}
