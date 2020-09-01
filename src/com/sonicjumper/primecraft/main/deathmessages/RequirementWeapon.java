package com.sonicjumper.primecraft.main.deathmessages;

import org.bukkit.Material;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class RequirementWeapon extends DeathRequirement {
	private Material reqMaterial;
	
	public RequirementWeapon(Material material) {
		reqMaterial = material;
	}
	
	@Override
	public boolean meetsRequirement(PlayerProfile playerKilled, PlayerProfile killer) {
		if(killer.getPlayer().getInventory().getItemInHand() == null) {
			return reqMaterial == null;
		}
		if(killer.getPlayer().getInventory().getItemInHand().getType().equals(reqMaterial)) {
			return true;
		}
		return false;
	}
}
