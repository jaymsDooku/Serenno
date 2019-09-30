package io.jayms.serenno;

import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import vg.civcraft.mc.civmodcore.ACivMod;
import vg.civcraft.mc.civmodcore.CoreConfigManager;

public class SerennoCommonConfigManager extends CoreConfigManager {
	
	public SerennoCommonConfigManager(ACivMod plugin) {
		super(plugin);
	}

	@Override
	protected boolean parseInternal(ConfigurationSection config) {
		host = config.getString("database.host");
		port = config.getInt("database.port");
		user = config.getString("database.user");
		pass = config.getString("database.pass");
		db = config.getString("database.db");
		creds = config.getBoolean("database.creds");
		
		maintenanceEnabled = config.getBoolean("maintenance.enabled");
		maintenanceWhitelist = config.getStringList("maintenance.whitelist");
		
		return true;
	}
	
	private boolean maintenanceEnabled;
	private List<String> maintenanceWhitelist;
	
	public boolean isMaintenanceEnabled() {
		return maintenanceEnabled;
	}
	
	public List<String> getMaintenanceWhitelist() {
		return maintenanceWhitelist;
	}
	
	private String host;
	private int port;
	private String user;
	private String pass;
	private String db;
	private boolean creds;

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public String getDb() {
		return db;
	}

	public boolean isCreds() {
		return creds;
	}

}
