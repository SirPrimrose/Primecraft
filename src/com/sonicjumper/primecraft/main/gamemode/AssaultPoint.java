package com.sonicjumper.primecraft.main.gamemode;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.player.PlayerProfile;
import com.sonicjumper.primecraft.main.teams.PlayerTeam.TeamDetails;
import com.sonicjumper.primecraft.main.util.PrimeUtility;

public class AssaultPoint {
	private TeamDetails alliance;
	private Location location;
	private int id;

	private final int MAX_DISARMING_TIME = 50;
	private final int MAX_ARMING_TIME = 50;
	private final int MAX_ARMED_TIME = 300;
	private int disarmingTime;
	private int armingTime;
	private int armedTime;
	private boolean arming;
	private boolean disarming;
	private boolean armed;
	private boolean exploded;
	private PlayerProfile armedPlayer;
	private Block armingButton;
	private Block signInfoBlock;
	
	public AssaultPoint(Location loc, int id, TeamDetails team) {
		this.alliance = team;
		this.location = loc;
		this.armingButton = loc.getWorld().getBlockAt(loc);
		this.id = id;
		loadFromConfig(loc.getWorld().getName());
		armedTime = 0;
		armingTime = 0;
		armedPlayer = null;
		arming = false;
		disarming = false;
		armed = false;
		exploded = false;
		updatePoint();
	}

	public void updatePoint() {
		if(!armingButton.getType().equals(Material.STONE_BUTTON)) {
			armingButton.setType(Material.STONE_BUTTON);
		}
		if(signInfoBlock != null) {
			if(signInfoBlock.getState() instanceof Sign) {
				Sign signData = (Sign) signInfoBlock.getState();
				signData.setLine(0, "Assault Point " + (id + 1));
				signData.setLine(1, alliance.getChatColor() + alliance.name());
				signData.setLine(2, "");
				if(isArming()) {
					signData.setLine(3, ChatColor.RED + "Arming");
				} else if(isDisarming()) {
					signData.setLine(3, ChatColor.GREEN + "Disarming");
				} else if(isArmed()) {
					signData.setLine(3, ChatColor.RED + "Armed");
				} else {
					signData.setLine(3, ChatColor.GREEN + "Disarmed");
				}
				signData.update();
			} else {
				signInfoBlock.setType(Material.WALL_SIGN);
			}
		}
		if(isArming()) {
			armingTime++;
			if(armingTime >= MAX_ARMING_TIME) {
				armed = true;
				arming = false;
				armingTime = 0;
				armedTime = 0;
				Bukkit.broadcastMessage(ChatColor.GOLD + "Assault Point " + PrimeUtility.getLetterFromNumber(id) + " has been armed!");
			}
		}
		if(isDisarming()) {
			disarmingTime++;
			if(disarmingTime >= MAX_DISARMING_TIME) {
				armed = false;
				disarming = false;
				disarmingTime = 0;
				armedTime = 0;
				Bukkit.broadcastMessage(ChatColor.GOLD + "Assault Point " + PrimeUtility.getLetterFromNumber(id) + " has been disarmed!");
			}
		}
		if(isArmed() && !isDisarming()) {
			armedTime++;
			if(armedTime >= (MAX_ARMED_TIME - 100) && !exploded && armedTime % 10 == 0) {
				int secondsLeft = (MAX_ARMED_TIME - armedTime) / 10;
				Bukkit.broadcastMessage(ChatColor.GOLD + "Assault Point " + PrimeUtility.getLetterFromNumber(id) + " will explode in " + secondsLeft + "seconds!");
			}
			if(armedTime >= MAX_ARMED_TIME && !exploded) {
				exploded = true;
				armedPlayer.explodedAssaultPoint();
				armedPlayer.getCurrentTeam().creditPoints(1);
				Bukkit.broadcastMessage(ChatColor.GOLD + "Assault Point " + PrimeUtility.getLetterFromNumber(id) + " has been destroyed!");
			}
		}
	}

	public void setDisarming(PlayerProfile playerPressing) {
		disarming = true;
	}
	
	public void cancelDisarming(PlayerProfile playerPressing) {
		disarming = false;
		disarmingTime = 0;
	}

	public void setArming(PlayerProfile playerPressing) {
		arming = true;
		armedPlayer = playerPressing;
	}
	
	public void cancelArming(PlayerProfile playerPressing) {
		arming = false;
		armedPlayer = null;
		armingTime = 0;
	}
	
	public boolean isDisarming() {
		return disarming;
	}

	public boolean isArming() {
		return arming;
	}

	public boolean isArmed() {
		return armed;
	}

	public boolean isDestroyed() {
		return exploded;
	}
	
	public Block getArmingButton() {
		return armingButton;
	}

	public TeamDetails getAlliance() {
		return alliance;
	}

	public String getStatusString() {
		if(exploded) {
			return ChatColor.GRAY + "Destroyed";
		} else if(isArmed()) {
			return alliance.getChatColor() + "Armed";
		} else {
			return ChatColor.GOLD + "Active";
		}
	}

	private void loadFromConfig(String worldName) {
		int infoSignX = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AP" + id + ".InfoSign.X");
		int infoSignY = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AP" + id + ".InfoSign.Y");
		int infoSignZ = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AP" + id + ".InfoSign.Z");
		signInfoBlock = location.getWorld().getBlockAt(infoSignX, infoSignY, infoSignZ);
		/*int armingButtonX = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AP" + id + ".ArmingButton.X");
		int armingButtonY = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AP" + id + ".ArmingButton.Y");
		int armingButtonZ = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AP" + id + ".ArmingButton.Z");
		armingButton = location.getWorld().getBlockAt(armingButtonX, armingButtonY, armingButtonZ);*/
	}

	private void saveToConfig(String worldName) {
		PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".AP" + id + ".InfoSign.X", signInfoBlock.getX());
		PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".AP" + id + ".InfoSign.Y", signInfoBlock.getY());
		PrimeCraftMain.instance.getWorldConfig().set("World." + worldName + ".AP" + id + ".InfoSign.Z", signInfoBlock.getZ());
		/*int armingButtonX = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AP" + id + ".ArmingButton.X");
		int armingButtonY = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AP" + id + ".ArmingButton.Y");
		int armingButtonZ = PrimeCraftMain.instance.getWorldConfig().getInt("World." + worldName + ".AP" + id + ".ArmingButton.Z");
		armingButton = location.getWorld().getBlockAt(armingButtonX, armingButtonY, armingButtonZ);*/
	}
}
