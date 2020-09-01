package com.sonicjumper.primecraft.main.effects;

import java.util.ArrayList;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class SpecialEffectsHandler {
	private ArrayList<WorldEffect> activeWorldEffects;
	
	private int effectTicker;
	
	public SpecialEffectsHandler() {
		activeWorldEffects = new ArrayList<WorldEffect>();
	}
	
	public void activateWorldEffect(WorldEffect effect) {
		activeWorldEffects.add(effect);
	}

	public void updateEffects() {
		for(PlayerProfile pp : PrimeCraftMain.instance.game.getPlayers()) {
			for(PlayerEffect pe : pp.getEnabledEffects()) {
				if(pp.getPlayer().isOnline() && !pp.getPlayer().isDead()) {
					pe.onUpdate(pp, effectTicker);
				}
			}
		}
		for(int i = 0; i < activeWorldEffects.size(); i++) {
			WorldEffect pe = activeWorldEffects.get(i);
			if(pe.isDead()) {
				activeWorldEffects.remove(pe);
			}
			pe.onUpdate();
		}

		effectTicker++;
	}

	public int getWorldTicker() {
		return effectTicker;
	}
}
