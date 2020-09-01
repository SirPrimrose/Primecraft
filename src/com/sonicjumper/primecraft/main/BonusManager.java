package com.sonicjumper.primecraft.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BonusManager {
	private int doubleCoinsTime;
	
	public BonusManager() {
		doubleCoinsTime = -1;
	}
	
	public void addTimeToDoubleCoins(int timeInSeconds) {
		doubleCoinsTime += timeInSeconds * 10;
		if(!(doubleCoinsTime % 3000 == 0 || (doubleCoinsTime <= 6000 && doubleCoinsTime % 600 == 0))) {
			broadcastDoubleCoinTime();
		}
	}
	
	public void updateBonuses() {
		if(doubleCoinsTime > 0) {
			if(doubleCoinsTime % 3000 == 0 || (doubleCoinsTime <= 6000 && doubleCoinsTime % 600 == 0)) {
				broadcastDoubleCoinTime();
			}
			doubleCoinsTime--;
		} else if(doubleCoinsTime == 0) {
			broadcastDoubleCoinTimeEnd();
			doubleCoinsTime--;
		}
	}
	
	private void broadcastDoubleCoinTimeEnd() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(ChatColor.GREEN + "<<***Double Coin time has ended***>>");
		}
	}

	private void broadcastDoubleCoinTime() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			sendMessageDoubleCoinTime(p);
		}
	}

	public void sendMessageDoubleCoinTime(Player p) {
		int minutes = doubleCoinsTime / 600;
		int hours = 0;
		while(minutes >= 60) {
			minutes -= 60;
			hours += 1;
		}
		if(hours > 0) {
			if(minutes != 0) {
				p.sendMessage(ChatColor.GREEN + "<<***" + hours + " hours and " + minutes + " minutes left to earn Double Coins!***>>");
			} else {
				p.sendMessage(ChatColor.GREEN + "<<***" + hours + " hours left to earn Double Coins!***>>");
			}
		} else {
			p.sendMessage(ChatColor.GREEN + "<<***" + minutes + " minutes left to earn Double Coins!***>>");
		}
	}

	public boolean isDoubleCoinsActive() {
		return doubleCoinsTime > 0;
	}
}
