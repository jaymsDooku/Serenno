package io.jayms.serenno.vault;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import io.jayms.serenno.db.sql.Database;

public class VaultMapDatabase {

	private Database database;
	
	private Vector gotoLocation;
	private Map<ChatColor, Location> cores;
	private VaultMapReinforcementDataSource reinforcementSource;
	private VaultMapBastionDataSource bastionSource;
	
	public VaultMapDatabase(Database database) {
		this.database = database;
	}
	
	public Vector getGotoLocation() {
		return gotoLocation;
	}
	
	public Map<ChatColor, Location> getCores() {
		return cores;
	}
	
	public VaultMapReinforcementDataSource getReinforcementSource() {
		return reinforcementSource;
	}
	
	public VaultMapBastionDataSource getBastionSource() {
		return bastionSource;
	}
	
}
