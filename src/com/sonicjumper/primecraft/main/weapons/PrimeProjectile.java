package com.sonicjumper.primecraft.main.weapons;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;

public abstract class PrimeProjectile {
	protected Arrow referenceProjectile;
	protected PlayerProfile shooter;
	protected TeamDetails teamAlliance;
	
	protected Location lastLocation;
	
	public PrimeProjectile(Arrow proj, PlayerProfile player) {
		referenceProjectile = proj;
		shooter = player;
		if(player != null) {
			teamAlliance = player.getCurrentTeam().getTeamDetails();
		}
	}
	
	public void onUpdate() {
		if(referenceProjectile == null || referenceProjectile.isDead() || referenceProjectile.getLocation().equals(lastLocation)) {
			onExpire();
			PrimeCraftMain.instance.game.weaponHandler.removeSpecialArrow(this);
		} else {
			lastLocation = referenceProjectile.getLocation();
		}
	}
	
	public abstract void onHitEntity(LivingEntity entity);
	
	protected abstract void onExpire();
	
	public Arrow getProjectile() {
		return referenceProjectile;
	}
	
	public TeamDetails getTeamAlliance() {
		return teamAlliance;
	}
}
