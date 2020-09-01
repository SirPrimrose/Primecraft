package com.sonicjumper.primecraft.main.effects;

import org.bukkit.Location;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class PlayerEffectCloudThrone extends PlayerEffect {
	public PlayerEffectCloudThrone(int id, String name) {
		super(id, name);
	}

	@Override
	protected void playEffect(PlayerProfile player, int tickTime) {
		int effectTick = Math.abs(tickTime % 60 - 30);
		for(int i = 0; i < 2; i++) {
			double angle = (Math.PI / 5.0D) * effectTick + (i * Math.PI);
			double x = Math.cos(angle) * 0.25D;
			double yHeight = 0.75D;
			double y = yHeight - (effectTick * yHeight / 30) + 0.5D;
			double z = Math.sin(angle) * 0.25D;
			Location effectLocation = player.getPlayer().getLocation().add(x, y, z);
			ParticleEffects.sendToLocation(ParticleEffects.CLOUD, effectLocation, 0.0F, 0.0F, 0.0F, 0, 1);
		}
	}

	@Override
	public void onEnable(PlayerProfile player) {
	}

	@Override
	public void onDisable(PlayerProfile player) {
	}
}
