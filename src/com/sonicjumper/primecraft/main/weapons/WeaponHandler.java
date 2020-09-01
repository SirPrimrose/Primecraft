package com.sonicjumper.primecraft.main.weapons;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class WeaponHandler {
	private ArrayList<Mine> mineList;
	private ArrayList<PrimeProjectile> specialArrowList;
	
	private HashMap<TNTPrimed, Mine> tntToOwnerMineMap;
	private HashMap<LightningStrike, PlayerProfile> lightningToOwnerMap;
	
	public WeaponHandler() {
		mineList = new ArrayList<Mine>();
		specialArrowList = new ArrayList<PrimeProjectile>();
		
		tntToOwnerMineMap = new HashMap<TNTPrimed, Mine>();
		lightningToOwnerMap = new HashMap<LightningStrike, PlayerProfile>();
	}
	
	public void updateWeapons() {
		for(int i = 0; i < specialArrowList.size(); i++) {
			PrimeProjectile ea = specialArrowList.get(i);
			ea.onUpdate();
		}
	}

	public void clearWeapons() {
		for(int i = 0; i < mineList.size(); i++) {
			Mine m = mineList.get(i);
			mineList.remove(m);
			m.remove();
		}
		
		specialArrowList.clear();
		tntToOwnerMineMap.clear();
		lightningToOwnerMap.clear();
	}

	/**
	 * @return True if Mine exploded, false if otherwise.
	 */
	public boolean onPressurePlate(Block clickedBlock, PlayerProfile playerStepping) {
		for(int i = 0; i < mineList.size(); i++) {
			Mine m = mineList.get(i);
			if(clickedBlock.equals(m.getBlock())) {
				if(!playerStepping.getCurrentTeam().getTeamDetails().equals(m.getAlliance())) {
					TNTPrimed tnt = m.explode();
					tntToOwnerMineMap.put(tnt, m);
					mineList.remove(m);
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isSpecialArrow(Projectile arrow) {
		for(PrimeProjectile ea : specialArrowList) {
			if(ea.getProjectile().equals(arrow)) {
				return true;
			}
		}
		return false;
	}
	
	public void addMineToList(Mine m) {
		mineList.add(m);
	}

	public void addSpecialArrow(PrimeProjectile projectile) {
		specialArrowList.add(projectile);
	}
	
	public void addOwnerToTNT(TNTPrimed tnt, Mine mine) {
		tntToOwnerMineMap.put(tnt, mine);
	}
	
	public void addOwnerToLightning(LightningStrike ls, PlayerProfile owner) {
		lightningToOwnerMap.put(ls, owner);
	}

	public PrimeProjectile getSpecialArrow(Projectile arrow) {
		for(PrimeProjectile ea : specialArrowList) {
			if(ea.getProjectile().equals(arrow)) {
				return ea;
			}
		}
		return null;
	}
	
	public Mine getOwnerOfExplosion(TNTPrimed tnt) {
		return tntToOwnerMineMap.get(tnt);
	}
	
	public PlayerProfile getOwnerOfLightning(LightningStrike ls) {
		return lightningToOwnerMap.get(ls);
	}

	public void removeSpecialArrow(PrimeProjectile projectile) {
		specialArrowList.remove(projectile);
	}
}
