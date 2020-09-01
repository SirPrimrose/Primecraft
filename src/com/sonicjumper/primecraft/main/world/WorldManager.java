package com.sonicjumper.primecraft.main.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import com.sonicjumper.primecraft.main.PrimeCraftMain;
import com.sonicjumper.primecraft.main.generators.VoidWorldGenerator;

public class WorldManager {
	private ArrayList<String> extraWorldNames;
	
	private ArrayList<PlayerWorld> playableWorlds;
	
	public void setupWorlds() {
		playableWorlds = new ArrayList<PlayerWorld>();
		extraWorldNames = new ArrayList<String>();
		
		List<String> worldsToLoad = PrimeCraftMain.instance.getWorldConfig().getStringList("World.PlayableWorlds");
		for(String s : worldsToLoad) {
			String[] options = s.split(",");
			playableWorlds.add(new PlayerWorld(options[0], options[1], options[2], options[3], Integer.parseInt(options[4])));
			createPlayableWorld(options[0], false);
		}
		List<String> extraWorldsToLoad = PrimeCraftMain.instance.getWorldConfig().getStringList("World.BuildWorlds");
		for(String s : extraWorldsToLoad) {
			createPlayableWorld(s, true);
		}
	}
	
	private void createPlayableWorld(String worldName, boolean extraWorld) {
		if(Bukkit.getWorld(worldName) == null) {
			WorldCreator wc = new WorldCreator(worldName);
			wc.type(WorldType.FLAT);
			VoidWorldGenerator cg = new VoidWorldGenerator();
			wc.generator(cg);
			World w = wc.createWorld();
			w.setSpawnFlags(false, false);
			w.setAutoSave(false);
			if(extraWorld) {
				extraWorldNames.add(w.getName());
			}
		}
	}
	
	public void enableNewWorld(String worldName, String desc, String author, String gameMode, int preferrredTime) {
		playableWorlds.add(new PlayerWorld(worldName, desc, author, gameMode, preferrredTime));
		createPlayableWorld(worldName, false);
	}
	
	public void disableWorld(String worldName) {
		for(int i = 0; i < playableWorlds.size(); i++) {
			PlayerWorld pw = playableWorlds.get(i);
			if(pw.getWorldName().equalsIgnoreCase(worldName)) {
				playableWorlds.remove(pw);
			}
		}
	}
	
	public ArrayList<String> getTravelableWorlds() {
		ArrayList<String> travelableWorlds = new ArrayList<String>();
		travelableWorlds.addAll(extraWorldNames);
		for(PlayerWorld pw : playableWorlds) {
			travelableWorlds.add(pw.getWorldName());
		}
		return travelableWorlds;
	}

	public ArrayList<PlayerWorld> getRandomPlayableMaps(int mapsToReturn, PlayerWorld lastWinningMap) {
		ArrayList<PlayerWorld> worldList = new ArrayList<PlayerWorld>();
		for(PlayerWorld pw : playableWorlds) {
			if(!pw.equals(lastWinningMap)) {
				worldList.add(pw);
			}
		}
		Collections.shuffle(worldList);
		ArrayList<PlayerWorld> returnedMaps = new ArrayList<PlayerWorld>();
		for(int i = 0; i < mapsToReturn && i < worldList.size(); i++) {
			if(worldList.get(i) != null) {
				returnedMaps.add(worldList.get(i));
			}
		}
		return returnedMaps;
	}
	
	public void saveWorlds() {
		ArrayList<String> worldStrings = new ArrayList<String>();
		for(PlayerWorld world : playableWorlds) {
			worldStrings.add(world.getWorldName() + "," + world.getDescription() + "," + world.getAuthor() + "," + world.getGamemode().getName() + "," + world.getPreferredTime());
		}
		PrimeCraftMain.instance.getWorldConfig().set("World.PlayableWorlds", worldStrings);
	}
}
