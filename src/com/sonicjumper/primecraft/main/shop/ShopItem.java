package com.sonicjumper.primecraft.main.shop;

import org.bukkit.ChatColor;
import org.bukkit.Material;

import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class ShopItem {
	public enum UpgradePath {
		Sword(0),
		Bow(1),
		Arrow(2),
		Offensive(3),
		Tactical(4),
		Potion_Positive(5),
		Potion_Negative(6),
		Helmet(-1),
		Chestplate(-1),
		Leggings(-1),
		Boots(-1),
		Food(8),
		None(-1);
		
		int locatedItemSlot = -1;
		
		UpgradePath(int itemSlot) {
			locatedItemSlot = itemSlot;
		}
	}
	
	public enum ItemType {
		Upgrade,
		AddToLoadout;
	}
	
	public static ShopItem[] shopItems = new ShopItem[128];
	
	public static ShopItem noItem = new ShopItem(0, "Nothing", "Nothing", UpgradePath.None, ItemType.Upgrade, Material.AIR, false, 0, 0);
	public static ShopItem goldenSword = new ShopItem(1, "Gold Sword", "Gold Sword", UpgradePath.Sword, ItemType.Upgrade, Material.GOLD_SWORD, false, 0, 0);
	public static ShopItem woodenSword = new ShopItem(2, "Wood Sword", "Wood Sword", UpgradePath.Sword, ItemType.Upgrade, Material.WOOD_SWORD, false, 100, 1);
	public static ShopItem stoneSword = new ShopItem(3, "Stone Sword", "Stone Sword", UpgradePath.Sword, ItemType.Upgrade, Material.STONE_SWORD, false, 250, 2);
	public static ShopItem ironSword = new ShopItem(4, "Iron Sword", "Iron Sword", UpgradePath.Sword, ItemType.Upgrade, Material.IRON_SWORD, false, 1000, 3);
	public static ShopItem diamondSword = new ShopItem(5, "Diamond Sword", "Diamond Sword", UpgradePath.Sword, ItemType.Upgrade, Material.DIAMOND_SWORD, false, 2000, 4);
	public static ShopItem leatherHelm = new ShopItem(6, "Leather Helmet", "Leather Helmet", UpgradePath.Helmet, ItemType.Upgrade, Material.LEATHER_HELMET, false, 0, 0);
	public static ShopItem leatherChest = new ShopItem(7, "Leather Chest", "Leather Chest", UpgradePath.Chestplate, ItemType.Upgrade, Material.LEATHER_CHESTPLATE, false, 0, 0);
	public static ShopItem leatherLegs = new ShopItem(8, "Leather Legs", "Leather Legs", UpgradePath.Leggings, ItemType.Upgrade, Material.LEATHER_LEGGINGS, false, 0, 0);
	public static ShopItem leatherBoots = new ShopItem(9, "Leather Boots", "Leather Boots", UpgradePath.Boots, ItemType.Upgrade, Material.LEATHER_BOOTS, false, 0, 0);
	public static ShopItem goldHelm = new ShopItem(10, "Gold Helmet", "Gold Helmet", UpgradePath.Helmet, ItemType.Upgrade, Material.GOLD_HELMET, false, 300, 1);
	public static ShopItem goldChest = new ShopItem(11, "Gold Chest", "Gold Chest", UpgradePath.Chestplate, ItemType.Upgrade, Material.GOLD_CHESTPLATE, false, 375, 1);
	public static ShopItem goldLegs = new ShopItem(12, "Gold Legs", "Gold Legs", UpgradePath.Leggings, ItemType.Upgrade, Material.GOLD_LEGGINGS, false, 350, 1);
	public static ShopItem goldBoots = new ShopItem(13, "Gold Boots", "Gold Boots", UpgradePath.Boots, ItemType.Upgrade, Material.GOLD_BOOTS, false, 275, 1);
	public static ShopItem chainHelm = new ShopItem(14, "Chain Helmet", "Chain Helmet", UpgradePath.Helmet, ItemType.Upgrade, Material.CHAINMAIL_HELMET, false, 525, 2);
	public static ShopItem chainChest = new ShopItem(15, "Chain Chest", "Chain Chest", UpgradePath.Chestplate, ItemType.Upgrade, Material.CHAINMAIL_CHESTPLATE, false, 600, 2);
	public static ShopItem chainLegs = new ShopItem(16, "Chain Legs", "Chain Legs", UpgradePath.Leggings, ItemType.Upgrade, Material.CHAINMAIL_LEGGINGS, false, 575, 2);
	public static ShopItem chainBoots = new ShopItem(17, "Chain Boots", "Chain Boots", UpgradePath.Boots, ItemType.Upgrade, Material.CHAINMAIL_BOOTS, false, 500, 2);
	public static ShopItem ironHelm = new ShopItem(18, "Iron Helmet", "Iron Helmet", UpgradePath.Helmet, ItemType.Upgrade, Material.IRON_HELMET, false, 950, 3);
	public static ShopItem ironChest = new ShopItem(19, "Iron Chest", "Iron Chest", UpgradePath.Chestplate, ItemType.Upgrade, Material.IRON_CHESTPLATE, false, 1200, 3);
	public static ShopItem ironLegs = new ShopItem(20, "Iron Legs", "Iron Legs", UpgradePath.Leggings, ItemType.Upgrade, Material.IRON_LEGGINGS, false, 1100, 3);
	public static ShopItem ironBoots = new ShopItem(21, "Iron Boots", "Iron Boots", UpgradePath.Boots, ItemType.Upgrade, Material.IRON_BOOTS, false, 850, 3);
	public static ShopItem diamondHelm = new ShopItem(22, "Diamond Helmet", "Diamond Helmet", UpgradePath.Helmet, ItemType.Upgrade, Material.DIAMOND_HELMET, false, 1800, 4);
	public static ShopItem diamondChest = new ShopItem(23, "Diamond Chest", "Diamond Chest", UpgradePath.Chestplate, ItemType.Upgrade, Material.DIAMOND_CHESTPLATE, false, 2200, 4);
	public static ShopItem diamondLegs = new ShopItem(24, "Diamond Legs", "Diamond Legs", UpgradePath.Leggings, ItemType.Upgrade, Material.DIAMOND_LEGGINGS, false, 2000, 4);
	public static ShopItem diamondBoots = new ShopItem(25, "Diamond Boots", "Diamond Boots", UpgradePath.Boots, ItemType.Upgrade, Material.DIAMOND_BOOTS, false, 1700, 4);
	public static ShopItem potato = new ShopItem(26, "Potato", "Potato", UpgradePath.Food, ItemType.Upgrade, Material.POTATO_ITEM, false, 0, 0);
	public static ShopItem cookie = new ShopItem(27, "Cookie", "Cookie", UpgradePath.Food, ItemType.Upgrade, Material.COOKIE, false, 50, 1);
	public static ShopItem melon = new ShopItem(28, "Melon", "Melon", UpgradePath.Food, ItemType.Upgrade, Material.MELON, false, 100, 2);
	public static ShopItem apple = new ShopItem(29, "Apple", "Apple", UpgradePath.Food, ItemType.Upgrade, Material.APPLE, false, 200, 3);
	public static ShopItem carrot = new ShopItem(30, "Carrot", "Carrot", UpgradePath.Food, ItemType.Upgrade, Material.CARROT_ITEM, false, 250, 4);
	public static ShopItem bread = new ShopItem(31, "Bread", "Bread", UpgradePath.Food, ItemType.Upgrade, Material.BREAD, false, 300, 5);
	public static ShopItem bakedPotato = new ShopItem(32, "Baked Potato", "Baked Potato", UpgradePath.Food, ItemType.Upgrade, Material.BAKED_POTATO, false, 350, 6);
	public static ShopItem goldenCarrot = new ShopItem(33, "Golden Carrot", "Golden Carrot", UpgradePath.Food, ItemType.Upgrade, Material.GOLDEN_CARROT, false, 400, 7);
	public static ShopItem steak = new ShopItem(34, "Steak", "Steak", UpgradePath.Food, ItemType.Upgrade, Material.COOKED_BEEF, false, 500, 8);
	public static ShopItem bow = new ShopItem(35, "Bow", "Bow", UpgradePath.Bow, ItemType.Upgrade, Material.BOW, false, 1000, 0);
	public static ShopItem foodAmount = new ShopItemFoodAmount(36, "Amount", "Amount", UpgradePath.None, ItemType.Upgrade, Material.AIR, true, 100, 0);
	public static ShopItem arrowCount = new ShopItemArrowCount(37, "Arrows", "Arrow", UpgradePath.Arrow, ItemType.AddToLoadout, Material.ARROW, true, 250, 0);
	public static ShopItem mine = new ShopItemMine(38, "Mine", "Mine", UpgradePath.Offensive, ItemType.AddToLoadout, Material.STONE_PLATE, true, 0, 0);
	public static ShopItem pearl = new ShopItemEnderPearl(39, "Ender Pearl", "Ender Pearl", UpgradePath.Tactical, ItemType.AddToLoadout, Material.ENDER_PEARL, true, 0, 0);
	public static ShopItem lightningRod = new ShopItemLightningRod(40, "Lightning Rod", ChatColor.GOLD + "Lightning Rod", UpgradePath.Offensive, ItemType.AddToLoadout, Material.BLAZE_ROD, true, 0, 0);
	public static ShopItem milkBucket = new ShopItemMilkBucket(41, "Milk Bucket", "Milk Bucket", UpgradePath.Tactical, ItemType.AddToLoadout, Material.MILK_BUCKET, true, 0, 0);
	public static ShopItem explosiveArrow = new ShopItemExplosiveArrow(42, "Explosive", "Explosive Arrow", UpgradePath.Arrow, ItemType.AddToLoadout, Material.ARROW, true, 0, 0);
	public static ShopItem speedPotion = new ShopItemSpeedPotion(43, "Speed", ChatColor.YELLOW + "Stamin-Up", UpgradePath.Potion_Positive, ItemType.AddToLoadout, Material.POTION, true, 0, 0);
	public static ShopItem slownessPotion = new ShopItemSlowPotion(44, "Slow", "Sludge", UpgradePath.Potion_Negative, ItemType.AddToLoadout, Material.POTION, true, 0, 0);
	public static ShopItem strengthPotion = new ShopItemStrengthPotion(45, "Strength", ChatColor.GRAY + "Deadshot Daiquiri", UpgradePath.Potion_Positive, ItemType.AddToLoadout, Material.POTION, true, 0, 0);
	public static ShopItem weaknessPotion = new ShopItemWeaknessPotion(46, "Weakness", "Weakness", UpgradePath.Potion_Negative, ItemType.AddToLoadout, Material.POTION, true, 0, 0);
	public static ShopItem healthPotion = new ShopItemHealthPotion(47, "Health", ChatColor.AQUA + "Quick Revive", UpgradePath.Potion_Positive, ItemType.AddToLoadout, Material.POTION, true, 0, 0);
	public static ShopItem harmingPotion = new ShopItemHarmingPotion(48, "Harming", "Grenade", UpgradePath.Potion_Negative, ItemType.AddToLoadout, Material.POTION, true, 0, 0);
	public static ShopItem regenPotion = new ShopItemRegenerationPotion(49, "Regen", ChatColor.DARK_RED + "Jugger Nog", UpgradePath.Potion_Positive, ItemType.AddToLoadout, Material.POTION, true, 0, 0);
	public static ShopItem witherPotion = new ShopItemWitherPotion(50, "Wither", "Wither", UpgradePath.Potion_Negative, ItemType.AddToLoadout, Material.POTION, true, 0, 0);
	public static ShopItem knockbackArrow = new ShopItemKnockbackArrow(51, "Knockback", "Knockback Arrow", UpgradePath.Arrow, ItemType.AddToLoadout, Material.ARROW, true, 0, 0);
	public static ShopItem timeStopper = new ShopItemTimeStopper(52, "Time Stopper", ChatColor.GOLD + "" + ChatColor.BOLD + "Time Stopper", UpgradePath.Tactical, ItemType.AddToLoadout, Material.WATCH, true, 0, 0);
	
	public int id;
	
	public String name;
	public String itemDisplayName;
	public UpgradePath upgradeType;
	public ItemType itemType;
	public Material material;
	public boolean canBuyMultiple;
	protected int cost;
	protected int requiredTier;
	public int tier;
	
	protected String failedReqMsg;
	
	public ShopItem(int itemId, String itemName, String displayName, UpgradePath path, ItemType type, Material itemMat, boolean multiBuy, int itemCost, int itemTier) {
		id = itemId;
		name = itemName;
		itemDisplayName = displayName;
		upgradeType = path;
		itemType = type;
		material = itemMat;
		canBuyMultiple = multiBuy;
		cost = itemCost;
		tier = itemTier;
		
		if(shopItems[itemId] == null) {
			shopItems[itemId] = this;
		} else {
			throw new IllegalArgumentException("Tried to make duplicate Shop Item id: " + itemId);
		}
	}
	
	public static ShopItem getShopItemForName(String itemName) {
		for(ShopItem si : shopItems) {
			if(si != null) {
				if(si.name.equalsIgnoreCase(itemName)) {
					return si;
				}
			}
		}
		return null;
	}

	public static ShopItem getShopItemForTypeAndTier(UpgradePath type, int tier) {
		for(ShopItem si : shopItems) {
			if(si != null) {
				if(si.upgradeType.equals(type) && si.tier == tier) {
					return si;
				}
			}
		}
		return null;
	}
	
	public static ShopItem getShopItemForId(int itemId) {
		return shopItems[itemId];
	}

	public void onItemBought(PlayerProfile playerProfile) {
		
	}

	//Override these methods to make special Shop Items
	public String getFailedSpecialRequirementsMessage() {
		return failedReqMsg;
	}

	/**
	 * Set the would-be failed requirements message and the possible cost for the item using the PlayerProfile
	 * @return Whether the PlayerProfile meets the specific requirements for this item
	 */
	public boolean setupSpecialRequirements(PlayerProfile pp) {
		return true;
	}
	
	public int getCost() {
		return cost;
	}
	
	public int getRequiredTier() {
		return requiredTier;
	}
}
