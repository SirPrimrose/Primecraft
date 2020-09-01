package com.sonicjumper.primecraft.main.weapons;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ExplosiveArrow extends PrimeProjectile {
	public ExplosiveArrow(Arrow proj, PlayerProfile player) {
		super(proj, player);
	}
	
	@Override
	public void onHitEntity(LivingEntity entity) {
		createExplosion();
	}

	@Override
	protected void onExpire() {
		createExplosion();
	}
	
	private void createExplosion() {
		TNTPrimed tnt = (TNTPrimed) referenceProjectile.getWorld().spawnEntity(referenceProjectile.getLocation(), EntityType.PRIMED_TNT);
		tnt.setFuseTicks(0);
		PrimeCraftMain.instance.game.weaponHandler.addOwnerToTNT(tnt, new Mine(null, shooter));
		PrimeCraftMain.instance.game.weaponHandler.removeSpecialArrow(this);
		referenceProjectile.remove();
	}
}
