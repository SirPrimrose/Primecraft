package com.sonicjumper.primecraft.main.effects;

import org.bukkit.entity.Entity;

import com.sonicjumper.primecraft.main.util.ReflectionUtil;

public class EffectUtil {
	public enum SwirlColor {
		LIGHTBLUE(65535), 
	    BLUE(255), 
	    DARKBLUE(204), 
	    RED(16724736), 
	    DARKRED(6684672), 
	    GREEN(65280), 
	    DARKGREEN(3381504), 
	    YELLOW(16750848), 
	    ORANGE(16737792), 
	    GRAY(13421772), 
	    BLACK(3355443), 
	    WHITE(16777215), 
	    PURPLE(10027212), 
	    PINK(16711884);

	    private int colorValue;

	    private SwirlColor(int value) {
	      colorValue = value;
	    }

	    public int getColorValue() {
	      return colorValue;
	    }
	}
	
	/*Not working, just ignore this part
	private static Bat mockEntity;
	
	public static void playSwirlAtLocation(Location loc, SwirlColor color) {
		if(mockEntity == null || mockEntity.isDead()) {
			mockEntity = (Bat) loc.getWorld().spawnEntity(loc, EntityType.BAT);
			mockEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 5000000, 1000), true);
		}
		mockEntity.teleport(loc);
		try {
			Object nmsEntity = mockEntity.getClass().getMethod("getHandle", new Class[0]).invoke(mockEntity, new Object[0]);
			
			Object dw = ReflectionUtil.getMethod(nmsEntity.getClass(), "getDataWatcher").invoke(nmsEntity, new Object[0]);

	        ReflectionUtil.getMethod(dw.getClass(), "watch").invoke(dw, new Object[] { Integer.valueOf(7), Integer.valueOf(color.getColorValue()) });
		} catch (Exception e) {
			e.printStackTrace();
		}
		mockEntity.teleport(new Location(loc.getWorld(), 0.0D, 0.0D, 0.0D));
        
		//PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles("mobSpell", (float)loc.getX(), (float)loc.getY(), (float)loc.getZ(), 0.5F, 1.0F, 0.5F, 0.0F, color.getColorValue());
		
		//ReflectionUtil.sendPacket(loc, packet);
	}*/
	
	//Still broken
	
	public static void playSwirlAtEntity(Entity entity, SwirlColor color) {
		try {
			Object nmsEntity = entity.getClass().getMethod("getHandle", new Class[0]).invoke(entity, new Object[0]);
			
			Object dw = ReflectionUtil.getMethod(nmsEntity.getClass(), "getDataWatcher").invoke(nmsEntity, new Object[0]);

	        ReflectionUtil.getMethod(dw.getClass(), "watch").invoke(dw, new Object[] { Integer.valueOf(7), Integer.valueOf(color.getColorValue()) });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stopSwirlAtEntity(Entity entity) {
		try {
			Object nmsEntity = entity.getClass().getMethod("getHandle", new Class[0]).invoke(entity, new Object[0]);

	        Object dw = ReflectionUtil.getMethod(nmsEntity.getClass(), "getDataWatcher").invoke(nmsEntity, new Object[0]);

	        ReflectionUtil.getMethod(dw.getClass(), "watch").invoke(dw, new Object[] { Integer.valueOf(8), Integer.valueOf(0) });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
