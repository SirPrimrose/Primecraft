package com.sonicjumper.primecraft.main.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

public class GeometryUtil {
	/*public static float generateRandomFloat(float min, float max) {
    float f = min + StringUtil.r().nextFloat() * (1.0F + max - min);
    return StringUtil.r().nextBoolean() ? f : -f;
  }

  public static float generateRandomFloat() {
    float f = StringUtil.r().nextFloat();
    return StringUtil.r().nextBoolean() ? f : -f;
  }*/

	public static List<Location> getCircle(Location loc, int radius, int h, boolean hollow, boolean sphere, boolean includeAir) {
		List<Location> blocks = new ArrayList<Location>();
		int cx = loc.getBlockX();
		int cy = loc.getBlockY();
		int cz = loc.getBlockZ();
		for (int x = cx - radius; x <= cx + radius; x++)
			for (int z = cz - radius; z <= cz + radius; z++)
				for (int y = sphere ? cy - radius : cy; y < (sphere ? cy + radius : cy + h); y++) {
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
					if ((dist < radius * radius) && ((!hollow) || (dist >= (radius - 1) * (radius - 1)))) {
						Location l = new Location(loc.getWorld(), x, y, z);
						if (includeAir || l.getBlock().getType() != Material.AIR) {
							blocks.add(l);
						}
					}
				}
		return blocks;
	}

	public static List<Entity> getNearbyEntities(Location l, int range) {
		List<Entity> entities = new ArrayList<Entity>();
		for (Entity entity : l.getWorld().getEntities()) {
			if (isInBorder(l, entity.getLocation(), range)) {
				entities.add(entity);
			}
		}
		return entities;
	}

	public static boolean isInBorder(Location center, Location l, int range) {
		int x = center.getBlockX(); int z = center.getBlockZ();
		int x1 = l.getBlockX(); int z1 = l.getBlockZ();
		if ((x1 >= x + range) || (z1 >= z + range) || (x1 <= x - range) || (z1 <= z - range)) {
			return false;
		}
		return true;
	}
}