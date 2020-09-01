package com.sonicjumper.primecraft.main.effects;

import org.bukkit.Location;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class PlayerEffectMagicAura extends PlayerEffect {
	private double particleCount = 30.0D;
	private double effectFrequency = 4.0D;
	private int effectRate = 40;
	
	public PlayerEffectMagicAura(int id, String name) {
		super(id, name);
	}

	@Override
	protected void playEffect(PlayerProfile player, int tickTime) {
		int effectTick = tickTime % effectRate;
		if(effectTick % (effectRate / effectFrequency) == 0) {
			int tickID = (int) ((effectFrequency - 1) - (effectTick / (effectRate / effectFrequency)));
			for(int i = 0; i < particleCount; i++) {
				double angle = (Math.PI / particleCount) * i * 2;
				double x = Math.cos(angle) * (0.45D - tickID * 0.05D);
				double yHeight = 1.1D;
				double y = (yHeight - yHeight * tickID / 4.0D) + 0.1D;
				double z = Math.sin(angle) * (0.45D - tickID * 0.05D);
				Location effectLocation = player.getPlayer().getLocation().add(x, y, z);
				ParticleEffects.sendToLocation(ParticleEffects.FIREWORK_SPARK, effectLocation, 0.0F, 0.0F, 0.0F, 0, 1);
			}
		}
	}

	@Override
	public void onEnable(PlayerProfile player) {
	}

	@Override
	public void onDisable(PlayerProfile player) {
	}
}
