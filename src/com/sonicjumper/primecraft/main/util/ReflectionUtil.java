package com.sonicjumper.primecraft.main.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

public class ReflectionUtil {
	public static Method getMethod(Class<?> cl, String method) {
		for (Method m : cl.getMethods()) if (m.getName().equals(method)) return m;
		return null;
	}

	public static Field getField(Class<?> cl, String field) {
		for (Field f : cl.getFields()) if (f.getName().equals(field)) return f;
		return null;
	}

	public static Field getDeclaredField(Class<?> cl, String field_name) {
		try {
			return cl.getDeclaredField(field_name);
		}
		catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setValue(Object instance, String fieldName, Object value) throws Exception {
		Field field = instance.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		field.set(instance, value);
	}

	public static void sendPacket(Location l, Object packet) {
		sendPacket(l, packet, 20);
	}

	public static void sendPacket(Location l, Object packet, int radius) {
		if(!GeometryUtil.getNearbyEntities(l, radius).isEmpty())
			for (Entity e : GeometryUtil.getNearbyEntities(l, radius))
				if ((e != null) && ((e instanceof Player))) {
					Player p = (Player)e;
					sendPacket(p, packet);
				}
	}

	public static void sendPacket(Player p, Object packet) {
		Object nmsPlayer = null;
		try {
			nmsPlayer = getMethod(p.getClass(), "getHandle").invoke(p, new Object[0]);
			Object con = nmsPlayer.getClass().getField("playerConnection").get(nmsPlayer);
			getMethod(con.getClass(), "sendPacket").invoke(con, new Object[] { packet });
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

	public static void spawnFirework(Location l, FireworkEffect fe) {
		Firework fw = (Firework)l.getWorld().spawn(l, Firework.class);
		FireworkMeta fwm = fw.getFireworkMeta();
		fwm.clearEffects();
		fwm.addEffect(fe);
		try {
			Field f = fwm.getClass().getDeclaredField("power");
			f.setAccessible(true);
			f.set(fwm, Integer.valueOf(-2));
		} catch (Exception e) {
		}
		fw.setFireworkMeta(fwm);
	}
}