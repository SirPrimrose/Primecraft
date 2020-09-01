package com.sonicjumper.primecraft.main.effects;

import org.bukkit.Effect;
import org.bukkit.Location;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class PlayerEffectHeartCrown extends PlayerEffect {
	public PlayerEffectHeartCrown(int id, String name) {
		super(id, name);
	}

	@Override
	public void onEnable(PlayerProfile player) {
	}

	@Override
	protected void playEffect(PlayerProfile player, int tickTime) {
		if(tickTime % 10 == 0) {
			double playerDirectionRadians = player.getPlayer().getLocation().getYaw() * (Math.PI / 180.0D) + (Math.PI / 2.0D);
			if(tickTime % 20 == 0) {
				//i starts at 1 to skip the heart that appears in front of the player
				for(int i = 1; i < 4; i++) {
					double angle = playerDirectionRadians + ((Math.PI * i) / 2);
					double x = Math.cos(angle) * 0.5D;
					double z = Math.sin(angle) * 0.5D;
					Location effectLocation = player.getPlayer().getLocation().add(x, 2.0D, z);
					player.getPlayer().getWorld().playEffect(effectLocation, Effect.HEART, 0);
				}
			} else {
				for(int i = 0; i < 4; i++) {
					double angle = playerDirectionRadians + (((Math.PI * i) / 2) + (Math.PI / 4.0D));
					double x = Math.cos(angle) * 0.5D;
					double z = Math.sin(angle) * 0.5D;
					Location effectLocation = player.getPlayer().getLocation().add(x, 2.0D, z);
					player.getPlayer().getWorld().playEffect(effectLocation, Effect.HEART, 0);
				}
			}
		}
	}

	@Override
	public void onDisable(PlayerProfile player) {
	}
}
