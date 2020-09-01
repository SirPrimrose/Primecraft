package com.sonicjumper.primecraft.main.effects;

import org.bukkit.Effect;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class PlayerEffectSpawnerBlaze extends PlayerEffect {
	public PlayerEffectSpawnerBlaze(int id, String name) {
		super(id, name);
	}

	@Override
	protected void playEffect(PlayerProfile player, int tickTime) {
		if(tickTime % 10 == 0) {
			player.getPlayer().getWorld().playEffect(player.getPlayer().getLocation().add(0.0D, 1.0D, 0.0D), Effect.MOBSPAWNER_FLAMES, 0);
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
