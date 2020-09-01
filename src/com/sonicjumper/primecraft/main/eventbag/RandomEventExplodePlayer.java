package com.sonicjumper.primecraft.main.eventbag;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.weapons.Mine;

public class RandomEventExplodePlayer extends RandomEvent {
	public RandomEventExplodePlayer(RandomEventType type, RandomEventRarity rarity, int minPlayers, int maxPlayers) {
		super(type, rarity, minPlayers, maxPlayers);
	}

	@Override
	protected void activateEventOnPlayer(PlayerProfile pp) {
		TNTPrimed tnt = (TNTPrimed) pp.getPlayer().getWorld().spawnEntity(pp.getPlayer().getEyeLocation(), EntityType.PRIMED_TNT);
		tnt.setFuseTicks(0);
		PrimeCraftMain.instance.game.weaponHandler.addOwnerToTNT(tnt, new Mine(null, pp));
		broadcastEventMessage(pp.getPlayer().getDisplayName(), " became a suicide bomber!");
	}
}
