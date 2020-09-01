package com.sonicjumper.primecraft.main.effects;

import org.bukkit.Effect;
import org.bukkit.Location;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class PlayerEffectNoteTrail extends PlayerEffect {
	public PlayerEffectNoteTrail(int id, String name) {
		super(id, name);
	}

	@Override
	protected void playEffect(PlayerProfile player, int tickTime) {
		if(tickTime % 2 == 0) {
			double playerDirectionRadians = player.getPlayer().getLocation().getYaw() * (Math.PI / 180.0D) + (Math.PI / 2.0D);
			double angle = playerDirectionRadians + Math.PI;
			double x = Math.cos(angle) * 0.5D;
			double z = Math.sin(angle) * 0.5D;
			Location effectLocation = player.getPlayer().getLocation().add(x, 1.0D, z);
			player.getPlayer().getWorld().playEffect(effectLocation, Effect.NOTE, 0);
		}
	}

	@Override
	public void onEnable(PlayerProfile player) {
	}

	@Override
	public void onDisable(PlayerProfile player) {
	}
}
