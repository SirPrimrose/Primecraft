package com.sonicjumper.primecraft.main.weapons;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class KnockbackArrow extends PrimeProjectile {
	public KnockbackArrow(Arrow proj, PlayerProfile player) {
		super(proj, player);
	}

	@Override
	public void onHitEntity(LivingEntity entity) {
		Vector v = entity.getVelocity();
		v.add(new Vector(referenceProjectile.getVelocity().getX(), 1.5D, referenceProjectile.getVelocity().getZ()));
		entity.setVelocity(v);
	}

	@Override
	protected void onExpire() {
	}
}
