package com.sonicjumper.primecraft.main.weapons;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class TimeStoppedArrow extends PrimeProjectile {
	private Vector originalVelocity;
	
	private int timeStopped;
	
	public TimeStoppedArrow(Arrow proj, PlayerProfile player, Vector velocity, int time) {
		super(proj, player);
		
		originalVelocity = velocity;
		timeStopped = time;
		referenceProjectile.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		referenceProjectile.setVelocity(referenceProjectile.getVelocity().add(new Vector(0.0D, 0.0995D, 0.0D)));
		
		if(timeStopped > 0) {
			timeStopped--;
		} else {
			releaseFromTimeStop();
			PrimeCraftMain.instance.game.weaponHandler.removeSpecialArrow(this);
		}
	}

	@Override
	public void onHitEntity(LivingEntity entity) {
		
	}

	@Override
	protected void onExpire() {
	}
	
	public void releaseFromTimeStop() {
		if(!referenceProjectile.isDead()) {
			referenceProjectile.setVelocity(originalVelocity);
		}
	}
}
