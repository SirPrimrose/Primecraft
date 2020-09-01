package com.sonicjumper.primecraft.main.gamemode;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public class Artifact {
	private Location artifactLocation;

	private Block startPosition;
	private Block artifactBlock;

	private PlayerProfile artifactHolder;

	public int timeOnGround;

	public Artifact(Block block) {
		startPosition = block;
		resetArtifact();
	}
	
	public void resetArtifact() {
		timeOnGround = -1;
		if(isOnGround() && artifactBlock != null) {
			artifactBlock.setType(Material.AIR);
		}
		artifactBlock = startPosition;
		artifactBlock.setType(Material.ENDER_STONE);
		artifactHolderDrop();
		updateLocation();
	}

	public void dropArtifact() {
		//If the block lands inside another block, do a search for empty space. If fail, then reset artifact.
		if(artifactLocation.getBlock().getType() != Material.AIR) {
			Block airBlock = PrimeUtility.getNearestBlock(artifactLocation, Material.AIR, 3);
			if(airBlock != null) {
				artifactBlock = airBlock;
			} else {
				resetArtifact();
			}
		} else {
			artifactBlock = artifactLocation.getBlock();
		}
		artifactBlock.setType(Material.ENDER_STONE);
		artifactHolderDrop();
		updateLocation();
	}

	/**
	 * Called when a Player is no longer the flagbearer 
	 */
	private void artifactHolderDrop() {
		if(artifactHolder != null) {
			PlayerProfile pp = artifactHolder;
			artifactHolder = null;
			pp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20, 4), true);
			pp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 0), true);
			pp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0), true);
			pp.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 2), true);
			pp.refreshHelmet();
			pp.refreshPlayerDisplayName();
		}
	}

	public void pickupArtifact(PlayerProfile pp) {
		artifactHolder = pp;
		artifactHolder.getPlayer().sendMessage(ChatColor.DARK_RED + "You have taken the artifact!");
		artifactHolder.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100000, 4), true);
		artifactHolder.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100000, 0), true);
		artifactHolder.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 0), true);
		artifactHolder.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100000, 2), true);
		artifactHolder.getPlayer().getInventory().setHelmet(new ItemStack(Material.ENDER_STONE));
		artifactHolder.getPlayer().setSneaking(false);
		artifactBlock.setType(Material.AIR);
		artifactBlock = null;
		//Don't set name to green, just use refreshPlayer to make the nametag be green.
		artifactHolder.updatePlayerNameInScoreboard();
	}
	
	public void updateLocation() {
		if(isBeingCarried()) {
			artifactLocation = artifactHolder.getPlayer().getLocation();
		} else {
			artifactLocation = artifactBlock.getLocation();
		}
	}
	
	public boolean isOnGround() {
		return artifactHolder == null;
	}
	
	public boolean isBeingCarried() {
		return artifactHolder != null;
	}
	
	public boolean isAtBase() {
		return getStartPosition().getBlock().equals(getCurrentLocation().getBlock());
	}

	public String getLocationString() {
		if(isBeingCarried()) {
			return "being carried by " + artifactHolder.getPlayer().getDisplayName();
		} else if(isAtBase()) {
			return "at spawn";
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
		return artifactLocation;
	}
	
	/**
	 * @return The Block that represents the flag, as long as it is on the ground.
	 */
	public Block getBlock() {
		return artifactBlock;
	}
	
	/**
	 * @return The Player that carries the flag, or null if it is on ground.
	 */
	public PlayerProfile getArtifactHolder() {
		return artifactHolder;
	}
}
