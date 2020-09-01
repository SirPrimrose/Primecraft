package com.sonicjumper.primecraft.main.player;

import java.util.HashMap;

import org.bukkit.Material;

public class MaterialDictionary {
	private static HashMap<Material, Integer> materialDict;
	
	private static void loadDictionary() {
		materialDict = new HashMap<Material, Integer>();
		materialDict.put(Material.AIR, 0);
		materialDict.put(Material.BOW, 5);
		materialDict.put(Material.GOLD_SWORD, 3);
		materialDict.put(Material.LEATHER_HELMET, 3);
		materialDict.put(Material.LEATHER_CHESTPLATE, 3);
		materialDict.put(Material.LEATHER_LEGGINGS, 3);
		materialDict.put(Material.LEATHER_BOOTS, 3);
		materialDict.put(Material.WOOD_SWORD, 4);
		materialDict.put(Material.STONE_SWORD, 5);
		materialDict.put(Material.GOLD_HELMET, 5);
		materialDict.put(Material.GOLD_CHESTPLATE, 5);
		materialDict.put(Material.GOLD_LEGGINGS, 5);
		materialDict.put(Material.GOLD_BOOTS, 5);
		materialDict.put(Material.CHAINMAIL_HELMET, 6);
		materialDict.put(Material.CHAINMAIL_CHESTPLATE, 6);
		materialDict.put(Material.CHAINMAIL_LEGGINGS, 6);
		materialDict.put(Material.CHAINMAIL_BOOTS, 6);
		materialDict.put(Material.IRON_SWORD, 8);
		materialDict.put(Material.IRON_HELMET, 8);
		materialDict.put(Material.IRON_CHESTPLATE, 8);
		materialDict.put(Material.IRON_LEGGINGS, 8);
		materialDict.put(Material.IRON_BOOTS, 8);
		materialDict.put(Material.DIAMOND_SWORD, 11);
		materialDict.put(Material.DIAMOND_HELMET, 10);
		materialDict.put(Material.DIAMOND_CHESTPLATE, 10);
		materialDict.put(Material.DIAMOND_LEGGINGS, 10);
		materialDict.put(Material.DIAMOND_BOOTS, 10);
		materialDict.put(Material.POTATO_ITEM, 3);
		materialDict.put(Material.COOKIE, 3);
		materialDict.put(Material.MELON, 3);
		materialDict.put(Material.APPLE, 4);
		materialDict.put(Material.CARROT_ITEM, 4);
		materialDict.put(Material.BREAD, 4);
		materialDict.put(Material.BAKED_POTATO, 5);
		materialDict.put(Material.GOLDEN_CARROT, 5);
		materialDict.put(Material.COOKED_BEEF, 5);
	}
	
	public static int getValueForMaterial(Material mat) {
		if(materialDict == null) {
			MaterialDictionary.loadDictionary();
		}
		return materialDict.get(mat);
	}
}
