package com.sonicjumper.primecraft.main.effects;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;

public class WorldEffectTimeStop extends WorldEffect {
	private double maxRadius;
	
	public WorldEffectTimeStop(Location center, int length, double radius) {
		super(center, length);
		maxRadius = radius;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void playEffect() {
		double lifePart = ((double) lifeTime) / ((double) maxLifeTime);
		double ringDistance = (maxRadius * lifePart);
		for(int i = 0; i < 200 * lifePart; i++) {
			double angle = rand.nextDouble() * Math.PI * 2;
			double x = Math.cos(angle) * ringDistance;
			double z = Math.sin(angle) * ringDistance;
			Location effectLocation = new Location(centerLocation.getWorld(), centerLocation.getX() + x, centerLocation.getY() + rand.nextDouble() * 2.0D, centerLocation.getZ() + z);
			//EffectUtil.playSwirlAtLocation(effectLocation, SwirlColor.ORANGE);
			centerLocation.getWorld().playEffect(effectLocation, Effect.TILE_BREAK, Material.GLOWSTONE.getId());
		}
	}
}
