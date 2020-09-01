package com.sonicjumper.primecraft.main.gamemode;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;

public class GamemodeTeamDeathmatch extends Gamemode {
	public GamemodeTeamDeathmatch(int index, String name, String shortName, int number) {
		super(index, name, shortName, number);
	}

	private PlayerProfile champion;
	
	private Random rand = new Random();
	
	private int chapionTime;
	
	@Override
	public void preGame() {
		super.preGame();
	}
	
	@Override
	public void startGame() {
		super.startGame();
	}
	
	@Override
	public void updateGame() {
		super.updateGame();
		
		if(champion == null || !champion.getPlayer().isOnline()) {
			selectNewChampion(PrimeCraftMain.instance.game.getRandomPlayer(true));
		}

		if(champion != null) {
			champion.getPlayer().getWorld().playEffect(champion.getPlayer().getEyeLocation().add(1.0D - (rand.nextDouble() * 2.0D), 1.0D - (rand.nextDouble() * 2.0D), 1.0D - (rand.nextDouble() * 2.0D)), Effect.HAPPY_VILLAGER, 0);
		}
		
		if(chapionTime > 1200) {
			//Bukkit.getServer().broadcastMessage(champion.getCurrentTeam().getTeamDetails().getChatColor() + champion.getPlayer().getDisplayName() + ChatColor.GOLD + " has lost the championship.");
			//champion = null;
			//selectNewChampion(PrimeCraftMain.instance.game.getRandomPlayer(true));
		} else {
			chapionTime++;
		}
	}
	
	@Override
	public void pauseGame() {
		super.pauseGame();
		
		champion = null;
	}
	
	@Override
	public void endGame() {
		super.endGame();
		
		champion = null;
	}

	@Override
	public boolean onPlayerTeleport(Player player, TeleportCause cause) {
		return true;
	}
	
	@Override
	public void onPlayerDeath(Player player, Player killer) {
		PlayerProfile killedPlayerProfile = PrimeCraftMain.instance.game.getProfileForPlayer(player);
		if(killer != null) {
			PlayerProfile killerProfile = PrimeCraftMain.instance.game.getProfileForPlayer(killer);
			killerProfile.getCurrentTeam().creditPoints(1);
			if(killedPlayerProfile.equals(champion)) {
				killerProfile.getCurrentTeam().creditPoints(1);
				selectNewChampion(killerProfile);
			}
		} else {
			selectNewChampion(PrimeCraftMain.instance.game.getRandomPlayer(true));
		}
	}

	@Override
	public void onButtonPress(Block pressedBlock, PlayerProfile playerPressing) {
	}
	
	private void selectNewChampion(PlayerProfile newChampion) {
		if(newChampion != null) {
			if(champion != null && champion.getPlayer().isOnline()) {
				Bukkit.getServer().broadcastMessage(newChampion.getCurrentTeam().getTeamDetails().getChatColor() + newChampion.getPlayer().getDisplayName() + ChatColor.GOLD + " has taken the Championship from " + champion.getCurrentTeam().getTeamDetails().getChatColor() + champion.getPlayer().getDisplayName() + "!");
			} else {
				Bukkit.getServer().broadcastMessage(newChampion.getCurrentTeam().getTeamDetails().getChatColor() + newChampion.getPlayer().getDisplayName() + ChatColor.GOLD + " has been selected as the new Champion!");
			}
			champion = newChampion;
			chapionTime = 0;
			if(newChampion.getPlayer().getInventory().getItem(0) != null) {
				newChampion.getPlayer().getInventory().getItem(0).addEnchantment(Enchantment.DAMAGE_ALL, 1);
				newChampion.getPlayer().getInventory().getItem(0).addEnchantment(Enchantment.KNOCKBACK, 2);
				newChampion.getPlayer().getInventory().getItem(0).addEnchantment(Enchantment.DURABILITY, 1);
			}
			for(ItemStack is : newChampion.getPlayer().getInventory().getArmorContents()) {
				if(is != null) {
					is.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
					is.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
					is.addEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 1);
					is.addEnchantment(Enchantment.THORNS, 1);
				}
			}
		} else {
			champion = null;
		}
	}
	
	@Override
	protected int getMaxPoints() {
		return Math.min(50, (PrimeCraftMain.instance.game.getPlayers().size() * 8 + 5) / numberOfTeams);
	}
}
