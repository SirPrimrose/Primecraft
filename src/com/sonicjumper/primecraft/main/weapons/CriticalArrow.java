package com.sonicjumper.primecraft.main.weapons;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class CriticalArrow extends PrimeProjectile {
	public CriticalArrow(Arrow proj, PlayerProfile player) {
		super(proj, player);

		referenceProjectile.setCritical(true);
		referenceProjectile.setFireTicks(400);
		referenceProjectile.setPassenger(referenceProjectile.getWorld().dropItem(referenceProjectile.getLocation(), new ItemStack(Material.NETHER_STAR)));
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		
		if(referenceProjectile.getTicksLived() % 4 == 0) {
			referenceProjectile.getWorld().playEffect(lastLocation, Effect.HAPPY_VILLAGER, 0);
			referenceProjectile.getWorld().playSound(lastLocation, Sound.ORB_PICKUP, 0.5F, 1.0F);
		}
	}

	@Override
	public void onHitEntity(LivingEntity entity) {
		if(entity instanceof Player) {
			Player p = (Player) entity;
			p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 200, 0, true, true));
		}
	}

	@Override
	protected void onExpire() {
	}
}
