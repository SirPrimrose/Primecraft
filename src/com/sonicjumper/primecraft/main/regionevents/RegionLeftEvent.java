package com.sonicjumper.primecraft.main.regionevents;

import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class RegionLeftEvent extends RegionEvent {
	public RegionLeftEvent(ProtectedRegion region, Player player, MovementWay movement) {
		super(region, player, movement);
	}
}
