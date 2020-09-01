package com.sonicjumper.primecraft.main.weapons;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;

public class Mine {
	private Block mineBlock;
	private TeamDetails teamAlliance;
	private PlayerProfile mineOwner;
	
	public Mine(Block placedBlockLocation, PlayerProfile placingPlayer) {
		mineBlock = placedBlockLocation;
		teamAlliance = placingPlayer.getCurrentTeam().getTeamDetails();
		mineOwner = placingPlayer;
	}

	public void remove() {
		if(mineBlock != null) {
			PrimeCraftMain.instance.getLogger().info("Tried to set block to air at X/Y/Z: " + mineBlock.getX() + "/" + mineBlock.getY() + "/" + mineBlock.getZ());
			mineBlock.setType(Material.AIR);
		}
	}

	public TNTPrimed explode() {
		TNTPrimed tnt = mineBlock.getWorld().spawn(mineBlock.getLocation(), TNTPrimed.class);
        tnt.setFuseTicks(0);
		//mineBlock.getWorld().createExplosion(mineBlock.getLocation().getX() + 0.5D, mineBlock.getLocation().getY() + 0.5D, mineBlock.getLocation().getZ() + 0.5D, 4.0F, false, false);
		remove();
		return tnt;
	}

	public Block getBlock() {
		return mineBlock;
	}

	public PlayerProfile getOwner() {
		return mineOwner;
	}
	
	public TeamDetails getAlliance() {
		return teamAlliance;
	}
}
