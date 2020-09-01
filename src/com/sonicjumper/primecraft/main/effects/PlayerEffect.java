package com.sonicjumper.primecraft.main.effects;

import java.util.Random;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public abstract class PlayerEffect {
	public static PlayerEffect[] effectIndex = new PlayerEffect[16];
	
	public static PlayerEffect heartCrown = new PlayerEffectHeartCrown(0, "HeartCrown");
	public static PlayerEffect spawnerBlaze = new PlayerEffectSpawnerBlaze(1, "Blaze");
	public static PlayerEffect noteTrail = new PlayerEffectNoteTrail(2, "NoteTrail");
	public static PlayerEffect storm = new PlayerEffectStorm(3, "Storm");
	public static PlayerEffect cloudThrone = new PlayerEffectCloudThrone(4, "CloudThrone");
	public static PlayerEffect magicAura = new PlayerEffectMagicAura(5, "MagicAura");
	
	protected int effectId;
	protected String effectName;
	
	protected Random rand;
	
	public PlayerEffect(int id, String name) {
		effectId = id;
		effectName = name;
		rand = new Random();
		
		effectIndex[id] = this;
	}
	
	public void onUpdate(PlayerProfile player, int tickTime) {
		playEffect(player, tickTime);
	}
	
	public abstract void onEnable(PlayerProfile player);
	
	protected abstract void playEffect(PlayerProfile player, int tickTime);
	
	public abstract void onDisable(PlayerProfile player);
	
	public int getId() {
		return effectId;
	}

	public String getName() {
		return effectName;
	}
	
	public static PlayerEffect getEffectForId(int id) {
		return effectIndex[id];
	}
	
	public static PlayerEffect getEffectForName(String name) {
		for(PlayerEffect pe : effectIndex) {
			if(pe != null && pe.effectName.equalsIgnoreCase(name)) {
				return pe;
			}
		}
		return null;
	}
}
