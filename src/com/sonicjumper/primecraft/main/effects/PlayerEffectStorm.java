package com.sonicjumper.primecraft.main.effects;

import org.bukkit.Effect;
import org.bukkit.Location;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class PlayerEffectStorm extends PlayerEffect {
	public PlayerEffectStorm(int id, String name) {
		super(id, name);
	}

	@Override
	protected void playEffect(PlayerProfile player, int tickTime) {
		if(tickTime % 2 == 0) {
			double angle = (Math.PI / 10.0D) * ((tickTime % 40) / 2);
			double x = Math.cos(angle) * 0.5D;
			double z = Math.sin(angle) * 0.5D;
			Location effectThundercloudLocation = player.getPlayer().getLocation().add(x, 2.0D, z);
			Location effectDropletLocation = player.getPlayer().getLocation().add(x, 2.4D, z);
			player.getPlayer().getWorld().playEffect(effectThundercloudLocation, Effect.VILLAGER_THUNDERCLOUD, 0);
			player.getPlayer().getWorld().playEffect(effectDropletLocation, Effect.WATERDRIP, 0);
			
			double angle2 = (Math.PI / 10.0D) * ((tickTime % 40) / 2) + Math.PI;
			double x2 = Math.cos(angle2) * 0.5D;
			double z2 = Math.sin(angle2) * 0.5D;
			Location effectThundercloudLocation2 = player.getPlayer().getLocation().add(x2, 2.0D, z2);
			Location effectDropletLocation2 = player.getPlayer().getLocation().add(x2, 2.4D, z2);
			player.getPlayer().getWorld().playEffect(effectThundercloudLocation2, Effect.VILLAGER_THUNDERCLOUD, 0);
			player.getPlayer().getWorld().playEffect(effectDropletLocation2, Effect.WATERDRIP, 0);
		}
	}

	@Override
	public void onEnable(PlayerProfile player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisable(PlayerProfile player) {
		// TODO Auto-generated method stub
		
	}
}
