package com.sonicjumper.primecraft.main.effects;

import com.sonicjumper.primecraft.main.effects.EffectUtil.SwirlColor;
import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class PlayerEffectColorSwirl extends PlayerEffect {
	//Don't use this class.... it broke
	private SwirlColor[] swirlColors;
	
	public PlayerEffectColorSwirl(int id, String name, SwirlColor[] colors) {
		super(id, name);
		swirlColors = colors;
	}

	@Override
	public void onEnable(PlayerProfile player) {
		for(SwirlColor sc : swirlColors) {
			EffectUtil.playSwirlAtEntity(player.getPlayer(), sc);
		}
	}

	@Override
	protected void playEffect(PlayerProfile player, int tickTime) {
	}

	@Override
	public void onDisable(PlayerProfile player) {
		EffectUtil.stopSwirlAtEntity(player.getPlayer());
	}
}
