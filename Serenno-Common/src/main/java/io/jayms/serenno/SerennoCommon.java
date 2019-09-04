package io.jayms.serenno;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;

import io.jayms.serenno.armourtype.ArmorListener;
import io.jayms.serenno.db.DBManager;
import vg.civcraft.mc.civmodcore.ACivMod;

public class SerennoCommon extends ACivMod {

	public static File getSchematicsFolder() {
		File schemFolder = new File(get().getDataFolder(), "schematics");
		if (!schemFolder.exists()) {
			if (schemFolder.mkdir()) {
				get().getLogger().info("Created new schematics folder.");
			}
		}
		return schemFolder;
	}
	
	private static SerennoCommon instance;
	
	public static SerennoCommon get() {
		return instance;
	}
	
	private SerennoCommonConfigManager configManager;
	
	public SerennoCommonConfigManager getConfigManager() {
		return configManager;
	}
	
	private DBManager dbManager;
	
	public DBManager getDBManager() {
		return dbManager;
	}
	
	@Override
	public void onEnable() {
		super.onEnable();
		instance = this;
		
		configManager = new SerennoCommonConfigManager(this);
		if (!configManager.parse()) {
			getLogger().severe("Errors in config file, shutting down");
			Bukkit.shutdown();
			return;
		}
		
		dbManager = new DBManager();
		dbManager.establishConnection();
		
		getServer().getPluginManager().registerEvents(new ArmorListener(new ArrayList<>()), this);
	}
	
	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	protected String getPluginName() {
		return "Serenno-Common";
	}
}
