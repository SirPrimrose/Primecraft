package com.sonicjumper.primecraft.main.effects;

import java.util.Random;

import org.bukkit.Location;

public abstract class WorldEffect {
	protected Location centerLocation;
	
	protected int lifeTime;
	protected int maxLifeTime;
	
	protected Random rand;
	
	public WorldEffect(Location center, int length) {
		centerLocation = center;
		lifeTime = 0;
		maxLifeTime = length;
		rand = new Random();
	}
	
	public boolean isDead() {
		return lifeTime >= maxLifeTime;
	}
	
	public void onUpdate() {
		lifeTime++;
		playEffect();
	}
	
	protected abstract void playEffect();
}
