package com.sonicjumper.primecraft.main.player;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Material;

public class MoneyItem {
	protected enum MoneyItemRarity {
		Common(20),
		Uncommon(15),
		Rare(10),
		SuperRare(5),
		UltraRare(3),
		Legendary(1);
		
		private int numberOfEntries;
		
		private MoneyItemRarity(int entries) {
			numberOfEntries = entries;
		}
		
		public int getNumberOfEntries() {
			return numberOfEntries;
		}
	}
	
	private static ArrayList<MoneyItem> itemList = new ArrayList<MoneyItem>();
	
	public static MoneyItem coal = new MoneyItem(MoneyItemRarity.Common, Material.COAL, 1);
	public static MoneyItem goldNugget = new MoneyItem(MoneyItemRarity.Common, Material.GOLD_NUGGET, 2);
	public static MoneyItem ironIngot = new MoneyItem(MoneyItemRarity.Uncommon, Material.IRON_INGOT, 3);
	public static MoneyItem goldIngot = new MoneyItem(MoneyItemRarity.Rare, Material.GOLD_INGOT, 5);
	public static MoneyItem diamond = new MoneyItem(MoneyItemRarity.SuperRare, Material.DIAMOND, 8);
	public static MoneyItem emerald = new MoneyItem(MoneyItemRarity.UltraRare, Material.EMERALD, 10);
	public static MoneyItem chest = new MoneyItem(MoneyItemRarity.Legendary, Material.CHEST, 20);
	public static MoneyItem redstone = new MoneyItem(MoneyItemRarity.Legendary, Material.REDSTONE, 25);
	
	private static Random rand = new Random();
	
	private Material representativeMaterial;
	private int materialValue;
	
	public MoneyItem(MoneyItemRarity rarity, Material material, int value) {
		representativeMaterial = material;
		materialValue = value;
		
		for(int i = 0; i < rarity.getNumberOfEntries(); i++) {
			itemList.add(this);
		}
	}
	
	public Material getMaterial() {
		return representativeMaterial;
	}
	
	public int getMoneyValue() {
		return materialValue;
	}
	
	public static boolean doesHaveValue(Material mat) {
		for(MoneyItem mi : itemList) {
			if(mi.getMaterial().equals(mat)) {
				return true;
			}
		}
		return false;
	}
	
	public static int getValueForMaterial(Material mat) {
		for(MoneyItem mi : itemList) {
			if(mi.getMaterial().equals(mat)) {
				return mi.getMoneyValue();
			}
		}
		return 0;
	}

	public static MoneyItem getRandomMoneyItem() {
		return itemList.get(rand.nextInt(itemList.size()));
	}
}
