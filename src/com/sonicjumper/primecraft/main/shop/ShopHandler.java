package com.sonicjumper.primecraft.main.shop;

import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.shop.ShopItem.ItemType;

public class ShopHandler {
	public void handleShopSign(ShopItem itemBuying, PlayerProfile pp) {
		if(pp.getPlayer().isSneaking()) {
			itemBuying.setupSpecialRequirements(pp);
			pp.getPlayer().sendMessage("This item costs " + itemBuying.cost + " money.");
			if(itemBuying.requiredTier > 0) {
				pp.getPlayer().sendMessage("This item requires tier " + itemBuying.requiredTier + ".");
			}
			return;
		}
		//Sets up special requirements for the tiem and checks if player can buy the item.
		if(!itemBuying.setupSpecialRequirements(pp)) {
			pp.getPlayer().sendMessage(itemBuying.getFailedSpecialRequirementsMessage());
			return;
		}
		//Only need to check for replacement if the item is Upgrade type
		if(itemBuying.itemType.equals(ItemType.Upgrade)) {
			ShopItem itemReplacing = pp.getItemsForUpgradeType(itemBuying.upgradeType).get(0);
			//If player already has item
			if(!itemBuying.canBuyMultiple && itemReplacing.equals(itemBuying)) {
				pp.getPlayer().sendMessage("You already have this item!");
				return;
			}
			//If item is lower tier than Player's item
			if(itemReplacing.tier > itemBuying.tier) {
				pp.getPlayer().sendMessage("The item you own is a higher upgrade than this item.");
				return;
			}
			//If item is more than 1 tier above currently owned item
			if(itemBuying.tier > itemReplacing.tier + 1) {
				ShopItem si = ShopItem.getShopItemForTypeAndTier(itemReplacing.upgradeType, itemReplacing.tier + 1);
				pp.getPlayer().sendMessage("You must buy the next upgrade, " + si.name + ", first");
				return;
			}
		}
		//If Player doesn't have enough money
		if(itemBuying.getCost() > pp.getMoneyAmount()) {
			pp.getPlayer().sendMessage("You don't have enough money. You need: " + itemBuying.getCost());
			return;
		}
		pp.setItemInLoadout(itemBuying);
		pp.getPlayer().sendMessage("You bought " + itemBuying.name + " for " + itemBuying.getCost() + " money.");
		pp.redeemMoney(itemBuying.getCost());
	}
}
