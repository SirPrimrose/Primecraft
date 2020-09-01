package com.sonicjumper.primecraft.main.gamemode;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

@SuppressWarnings("deprecation")
public class Flag {
	private Location flagLocation;
	
	private Block startPosition;
	
	private Block flagBlock;
	private TeamDetails flagTeamDetails;
	
	private PlayerProfile flagbearer;
	
	public int timeOnGround;
	
	public Flag(Block flagStartBlock, TeamDetails teamDetails) {
		startPosition = flagStartBlock;
		flagTeamDetails = teamDetails;
		resetFlag();
	}
	
	public void resetFlag() {
		timeOnGround = -1;
		if(isOnGround() && flagBlock != null) {
			flagBlock.setType(Material.AIR);
		}
		flagBlock = startPosition;
		flagBlock.setType(Material.WOOL);
		flagBlock.setData(flagTeamDetails.getDyeColor().getWoolData());
		flagbearerDrop();
		updateLocation();
	}

	public void dropFlag() {
		//If the block lands inside another block, do a search for empty space. If fail, then reset flag.
		if(flagLocation.getBlock().getType() != Material.AIR) {
			Block airBlock = PrimeUtility.getNearestBlock(flagLocation, Material.AIR, 3);
			if(airBlock != null) {
				flagBlock = airBlock;
			} else {
				resetFlag();
			}
		} else {
			flagBlock = flagLocation.getBlock();
		}
		flagBlock.setType(Material.WOOL);
		flagBlock.setData(flagTeamDetails.getDyeColor().getWoolData());
		flagbearerDrop();
		updateLocation();
	}

	/**
	 * Called when a Player is no longer the flagbearer 
	 */
	private void flagbearerDrop() {
		if(flagbearer != null) {
			PlayerProfile pp = flagbearer;
			flagbearer = null;
			pp.refreshHelmet();
			pp.refreshPlayerDisplayName();
		}
	}

	public void pickupFlag(PlayerProfile pp) {
		flagbearer = pp;
		flagbearer.getPlayer().sendMessage(ChatColor.DARK_RED + "You have taken the enemy flag!");
		flagbearer.getPlayer().getInventory().setHelmet(new ItemStack(Material.WOOL, 1, flagTeamDetails.getDyeColor().getWoolData()));
		flagBlock.setType(Material.AIR);
		flagBlock = null;
		//Don't set name to green, just use refreshPlayer to make the nametag be green.
		flagbearer.updatePlayerNameInScoreboard();
	}
	
	public void updateLocation() {
		if(isBeingCarried()) {
			flagLocation = flagbearer.getPlayer().getLocation();
		} else {
			flagLocation = flagBlock.getLocation();
		}
	}
	
	public boolean isOnGround() {
		return flagbearer == null;
	}
	
	public boolean isBeingCarried() {
		return flagbearer != null;
	}
	
	public boolean isAtBase() {
		return getStartPosition().getBlock().equals(getCurrentLocation().getBlock());
	}

	public String getLocationString() {
		if(isBeingCarried()) {
			return "being carried by " + flagbearer.getPlayer().getDisplayName();
		} else if(isAtBase()) {
			return "at base";
		} else if(isOnGround()) {
			return "on the ground.";
		}
		return " lost?";
	}
	
	public Location getStartPosition() {
		return startPosition.getLocation().clone().add(0.5D, 0.5D, 0.5D);
	}
	
	/**
	 * @return The current location of the flag, whether being carried or on the ground.
	 */
	public Location getCurrentLocation() {
		return flagLocation;
	}
	
	/**
	 * @return The Block that represents the flag, as long as it is on the ground.
	 */
	public Block getBlock() {
		return flagBlock;
	}
	
	/**
	 * @return The Player that carries the flag, or null if it is on ground.
	 */
	public PlayerProfile getFlagbearer() {
		return flagbearer;
	}
	
	public TeamDetails getTeamDetails() {
		return flagTeamDetails;
	}
}
