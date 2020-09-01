package com.sonicjumper.primecraft.main.listeners;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.sonicjumper.primecraft.main.regionevents.MovementWay;
import com.sonicjumper.primecraft.main.regionevents.RegionEnterEvent;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public class SpawnGuardListener implements Listener {
	@EventHandler
	public void onPlayerEnterRegion(RegionEnterEvent event) {
		if(event.getMovementWay().equals(MovementWay.MOVE)) {
			if(event.getRegion().getId().contains("spawn")) {
				if(!event.getPlayer().isOp()) {
					event.getPlayer().sendMessage("You are not allowed to enter spawns! Use /spawn to go to your team's spawn");
					event.getPlayer().damage(2.0D);
					Block blockOutOfSpawn = PrimeUtility.getNearestBlockOutOfSpawn(event.getPlayer().getLocation(), 4);
					if(blockOutOfSpawn != null) {
						event.getPlayer().teleport(blockOutOfSpawn.getLocation().add(0.5D, 0.5D, 0.5D));
					}
				} else {
					event.getPlayer().sendMessage("I'll let you through because you are op, but don't spawn camp!");
				}
			}
		}
	}
}
