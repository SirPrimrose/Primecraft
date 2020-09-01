package com.sonicjumper.primecraft.main.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigUtility {
	private JavaPlugin plugin;
	
	private HashMap<String, FileConfiguration> customConfigMap = null;
	private HashMap<String, File> customConfigFileMap = null;
	
	public ConfigUtility(JavaPlugin context) {
		plugin = context;
		
		customConfigMap = new HashMap<String, FileConfiguration>();
		customConfigFileMap = new HashMap<String, File>();
	}
	
	public void reloadCustomConfig(String configName) {
		if(customConfigFileMap.get(configName) == null) {
			customConfigFileMap.put(configName, new File(plugin.getDataFolder(), configName + ".yml"));
		}
		customConfigMap.put(configName, YamlConfiguration.loadConfiguration(customConfigFileMap.get(configName)));
		
		// Not looking for defaults
	}
	
	public FileConfiguration getCustomConfig(String configName) {
		if(customConfigMap.get(configName) == null) {
			reloadCustomConfig(configName);
		}
		return customConfigMap.get(configName);
	}
	
	public void saveCustomConfig(String configName) {
		if(customConfigFileMap.get(configName) == null || customConfigMap.get(configName) == null) {
			return;
		}
		try {
			getCustomConfig(configName).save(customConfigFileMap.get(configName));
		} catch(IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to " + customConfigFileMap.get(configName), e);
		}
	}
}
