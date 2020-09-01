package com.sonicjumper.primecraft.main.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.Sign;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.sonicjumper.primecraft.main.PrimeCraftMain;

public class PrimeUtility {
	private static Random rand = new Random();
	
	public static String noMergeTag = "noMerge:";
	
	private static Scoreboard coloringScoreboard;
	private static SimpleScoreboard gameDataScoreboard;
	
	public static Block getNearestBlockOutOfSpawn(Location locationSearchingFrom, int radius) {
		Block nearestBlock = null;
		double nearestDistance = Math.pow(radius, 2);
		for (int x = -(radius); x <= radius; x++) {
			for (int y = -(radius); y <= radius; y++) {
				for (int z = -(radius); z <= radius; z++) {
					Location loc = locationSearchingFrom.getBlock().getRelative(x, y, z).getLocation().add(0.5D, 0.5D, 0.5D);
					if(!PrimeCraftMain.instance.game.isLocationInSpawnRegion(loc)) {
						double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
						if(distance < nearestDistance) {
							nearestDistance = distance;
							nearestBlock = loc.getBlock();
						}
					}
				}
			}
		}
		return nearestBlock;
	}
	
	public static Block getNearestBlock(Location locationSearchingFrom, Material materialToLookFor, int radius) {
		Block nearestBlock = null;
		double nearestDistance = Math.pow(radius, 2);
		for (int x = -(radius); x <= radius; x++) {
			for (int y = -(radius); y <= radius; y++) {
				for (int z = -(radius); z <= radius; z++) {
					Location loc = locationSearchingFrom.getBlock().getRelative(x, y, z).getLocation();
					if (materialToLookFor.equals(loc.getBlock().getType())) {
						double distance = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
						if(distance < nearestDistance) {
							nearestDistance = distance;
							nearestBlock = loc.getBlock();
						}
					}
				}
			}
		}
		return nearestBlock;
	}
	
	public static Block getRandomNearbyBlock(Location locationSearchingFrom, Material materialToLookFor, int radius, boolean atSameY) {
		ArrayList<Block> availableBlocks = new ArrayList<Block>();
		for (int x = -(radius); x <= radius; x++) {
			int yRadius = atSameY ? 0 : radius;
			for (int y = -(yRadius); y <= yRadius; y++) {
				for (int z = -(radius); z <= radius; z++) {
					Location loc = locationSearchingFrom.getBlock().getRelative(x, y, z).getLocation();
					if (materialToLookFor.equals(loc.getBlock().getType())) {
						availableBlocks.add(loc.getBlock());
					}
				}
			}
		}
		return availableBlocks.get(rand.nextInt(availableBlocks.size()));
	}
	
	public static String getRomanFromArabic(int number) {
	    String riman[] = {"M","XM","CM","D","XD","CD","C","XC","L","XL","X","IX","V","IV","I"};
	    int arab[] = {1000, 990, 900, 500, 490, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
	    StringBuilder result = new StringBuilder();
	    int i = 0;
	    while (number > 0 || arab.length == (i - 1)) {
	        while ((number - arab[i]) >= 0) {
	            number -= arab[i];
	            result.append(riman[i]);
	        }
	        i++;
	    }
	    return result.toString();
	}
	
	public static String getOrdinalFromCardinal(int cardinal) {
	    String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
	    switch (cardinal % 100) {
	    case 11:
	    case 12:
	    case 13:
	        return cardinal + "th";
	    default:
	        return cardinal + sufixes[cardinal % 10];
	    }
	}
	
	public static ItemStack getNonStackingItemStack(Material m) {
		ItemStack is = new ItemStack(m);
		ItemMeta im = is.getItemMeta();
		List<String> l = im.getLore();
		if(l == null) {
			l = new LinkedList<String>();
		}
		l.add(noMergeTag + rand.nextFloat());
		im.setLore(l);
		is.setItemMeta(im);
		return is;
	}
	
	public static ItemStack getNamedStack(Material m, String displayName, int stackSize) {
		if(!m.equals(Material.AIR)) {
			ItemStack is = new ItemStack(m, stackSize);
			ItemMeta im = is.getItemMeta();
			im.setDisplayName(displayName);
			is.setItemMeta(im);
			return is;
		}
		return null;
	}
	
	public static void removeItemsFromPlayerInventory(Player player, Material removeMaterial) {
		for(ItemStack is : player.getInventory().getContents()) {
			if(is != null && is.getType().equals(removeMaterial)) {
				player.getInventory().remove(removeMaterial);
			}
		}
	}
	
	public static void updateGameDataScoreboard() {
		/*try {
			if(PrimeCraftMain.instance.game.isMapLoaded) {
				gameDataScoreboard = new SimpleScoreboard(ChatColor.BOLD + "" + ChatColor.GOLD + "Ascended PvP");
				gameDataScoreboard.reset();
				gameDataScoreboard.blankLine();
				for(int i = 0; i < PrimeCraftMain.instance.game.currentGamemode.numberOfTeams; i++) {
					TeamDetails details = TeamDetails.getDetailsForID(i);
					gameDataScoreboard.add(details.name() + ": " + details.getTeamObject().getTeamPoints());
				}
				gameDataScoreboard.build();
				gameDataScoreboard.send((Player[]) Bukkit.getOnlinePlayers().toArray());
			}
		} catch(Exception e) {
			PrimeCraftMain.instance.getLogger().warning("Ran into an error updating the scoreboard...");
			//e.printStackTrace();
		}*/
	}

	public static void setPlayerPrefix(Player player, String prefix) {
		if(coloringScoreboard == null) {
			coloringScoreboard = PrimeCraftMain.instance.getServer().getScoreboardManager().getNewScoreboard();
		}
		Team team = coloringScoreboard.getTeam(player.getName());
		if(team == null) {
			team = coloringScoreboard.registerNewTeam(player.getName());
		}
	    team.setDisplayName(player.getName());
	    team.setPrefix(prefix);
	    if(team.hasPlayer(player)) {
	    	team.removePlayer(player);
	    }
	    team.addPlayer(player);
	    player.setScoreboard(coloringScoreboard);
	}
	
	public static Block getBlockSignAttachedTo(Block block) {
		if(block.getType().equals(Material.WALL_SIGN)) {
			Sign s = (Sign) block.getState().getData();
			return block.getRelative(s.getAttachedFace());
		} else {
			return null;
		}
	}

	public static void spawnFireworks(Location location, Color color, int numberOfFireworks, float maxDistance) {
		double incement = (Math.PI * 2) / numberOfFireworks;
		for(int i = 0; i < numberOfFireworks; i++) {
			double x = Math.sin(incement * i);
			double z = Math.cos(incement * i);
			Location loc = new Location(location.getWorld(), location.getX() + x * maxDistance, location.getY(), location.getZ() + z * maxDistance);
			Firework fw = (Firework) location.getWorld().spawnEntity(loc, EntityType.FIREWORK);
			FireworkMeta meta = fw.getFireworkMeta();
			FireworkEffect effect = FireworkEffect.builder().flicker(true).withColor(color).withFade(color).trail(true).build();
			meta.addEffect(effect);
			meta.setPower(0);
			fw.setFireworkMeta(meta);
		}
	}

	/**
	 * Starting with A=0, B=1, C=2, etc. return a letter matching the given number.
	 */
	public static char getLetterFromNumber(int i) {
		char[] letters = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
		return letters[i];
	}

	public static void addLoreToItem(ItemStack item, String string) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = null;
		if(meta.hasLore()) {
			lore = meta.getLore();
		} else {
			lore = new ArrayList<String>();
		}
		lore.add(string);
		meta.setLore(lore);
	}

	public static void setColorOfLeatherArmor(ItemStack leatherArmor, Color color) {
		LeatherArmorMeta helmMeta = (LeatherArmorMeta) leatherArmor.getItemMeta();
		helmMeta.setColor(color);
		leatherArmor.setItemMeta(helmMeta);
	}
}
