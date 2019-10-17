package io.jayms.serenno.vault;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.Files;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.db.sql.Database;
import io.jayms.serenno.db.sql.SQLite;
import io.jayms.serenno.game.vaultbattle.VaultBattle;
import io.jayms.serenno.model.finance.company.Company;
import io.jayms.serenno.model.finance.company.ServerCompany;
import io.jayms.serenno.model.group.Group;

public class VaultMapDatabase {
	
	private static final String ATTACKERS = "Attackers";
	private static final String DEFENDERS = "Defenders";
	
	private static final String CREATE_INFO = "CREATE TABLE IF NOT EXISTS INFO("
			+ "InformationID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "GotoWorld TEXT,"
			+ "GotoX REAL,"
			+ "GotoY REAL,"
			+ "GotoZ REAL, "
			+ "GotoPitch REAL, "
			+ "GotoYaw REAL"
			+ ");";
	
	private static final String INSERT_INFO = "INSERT INTO INFO"
			+ "("
			+ "GotoWorld, GotoX, GotoY, GotoZ, GotoPitch, GotoYaw"
			+ ") VALUES("
			+ "?, ?, ?, ?, ?, ?"
			+ ")";
	
	private static final String UPDATE_INFO = "UPDATE INFO "
			+ "SET GotoWorld = ?, "
			+ "GotoX = ?, "
			+ "GotoY = ?, "
			+ "GotoZ = ?, "
			+ "GotoPitch = ?, "
			+ "GotoYaw = ? "
			+ "WHERE InformationID = ?";
	
	private static final String SELECT_INFO = "SELECT * FROM INFO LIMIT 1";
	
	private static final String DELETE_INFO = "DELETE FROM INFO WHERE ReinforcementID = ?";

	private SQLite database;

	private String worldName;
	private VaultMap vaultMap;
	
	private int informationID;
	private Location gotoLocation;
	private VaultMapCoreDataSource coreSource;
	private VaultMapReinforcementDataSource reinforcementSource;
	private VaultMapBastionDataSource bastionSource;
	private VaultMapReinforcementBlueprintDataSource reinforcementBlueprintSource;
	private VaultMapBastionBlueprintDataSource bastionBlueprintSource;
	private VaultMapSnitchDataSource snitchSource;
	private Map<String, Group> groupSource;
	private Map<String, Company> companySource;
	
	private boolean loaded;
	
	private VaultBattle battle;
	
	public VaultMapDatabase(String worldName, VaultMap vaultMap, SQLite database) {
		this.worldName = worldName;
		this.vaultMap = vaultMap;
		this.database = database;
		this.coreSource = new VaultMapCoreDataSource(this);
		this.reinforcementSource = new VaultMapReinforcementDataSource(this);
		this.bastionSource = new VaultMapBastionDataSource(this);
		this.reinforcementBlueprintSource = new VaultMapReinforcementBlueprintDataSource(this);
		this.bastionBlueprintSource = new VaultMapBastionBlueprintDataSource(this);
		this.snitchSource = new VaultMapSnitchDataSource(this);
		this.groupSource = new HashMap<>();
		this.companySource = new HashMap<>();
		
		init();
	}
	
	public Database getDatabase() {
		return database;
	}
	
	public void init() {
		UUID serverID = SerennoCobalt.get().getFinanceManager().getServerID();
		Company attackerCompany = new ServerCompany(ATTACKERS, serverID);
		Company defenderCompany = new ServerCompany(DEFENDERS, serverID);
		companySource.put(attackerCompany.getName(), attackerCompany);
		companySource.put(defenderCompany.getName(), defenderCompany);
		
		Group attackerGroup = new Group(ATTACKERS, attackerCompany);
		Group defenderGroup = new Group(DEFENDERS, defenderCompany);
		groupSource.put(attackerGroup.getName(), attackerGroup);
		groupSource.put(defenderGroup.getName(), defenderGroup);
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				database.open();
				database.modifyQuery(CREATE_INFO, true);
				coreSource.createTables();
				reinforcementSource.createTables();
				bastionSource.createTables();
				reinforcementBlueprintSource.createTables();
				bastionBlueprintSource.createTables();
				snitchSource.createTables();
			}
			
		}.runTask(SerennoCrimson.get());
	}
	
	public void load() {
		try {
			PreparedStatement ps = database.getConnection().prepareStatement(SELECT_INFO);
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return;
			}
			
			informationID = rs.getInt("InformationID");
			
			String gotoWorldName = rs.getString("GotoWorld");
			World gotoWorld = Bukkit.getWorld(gotoWorldName);
			if (gotoWorld == null) {
				throw new IllegalStateException("The world to go to doesn't exist anymore.");
			}
			
			double x = rs.getDouble("GotoX");
			double y = rs.getDouble("GotoY");
			double z = rs.getDouble("GotoZ");
			float yaw = rs.getFloat("GotoYaw");
			float pitch = rs.getFloat("GotoPitch");
			gotoLocation = new Location(gotoWorld, x, y, z, yaw, pitch);
			SerennoCrimson.get().getLogger().info("Loaded information for vault map: " + vaultMap.getArena().getName());
			ps.close();
			
			loaded = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void setGotoLocation(Location gotoLocation) {
		try {
			PreparedStatement ps = loaded ? ps = database.getConnection().prepareStatement(UPDATE_INFO) :
				database.getConnection().prepareStatement(INSERT_INFO, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, gotoLocation.getWorld().getName());
			ps.setDouble(2, gotoLocation.getX());
			ps.setDouble(3, gotoLocation.getY());
			ps.setDouble(4, gotoLocation.getZ());
			ps.setDouble(5, gotoLocation.getPitch());
			ps.setDouble(6, gotoLocation.getYaw());
			if (loaded) {
				ps.setInt(7, informationID);
			}
			ps.executeUpdate();
			if (!loaded) {
				ResultSet rs = ps.getGeneratedKeys();
				informationID = rs.getInt(1);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		this.gotoLocation = gotoLocation;
	}
	
	public Location getGotoLocation() {
		if (gotoLocation == null) {
			gotoLocation = new Location(vaultMap.getOriginalWorld(), 0, 70, 0);
		}
		return gotoLocation;
	}
	
	public void delete() {
		database.close();
		if (database.getSQLfile().delete()) {
			SerennoCrimson.get().getLogger().info("Deleted SQLite database for vault map: " + vaultMap.getArena().getRegion().getName());
		}
	}
	
	public VaultMapDatabase copy(String worldName, File file) {
		try {
			Files.copy(database.getSQLfile(), file);
			SQLite sqlite = new SQLite(SerennoCrimson.get(), SerennoCrimson.get().getLogger(), "[VaultMap - " + worldName + "]", file.getName(), file.getParentFile().getAbsolutePath());
			VaultMapDatabase db = new VaultMapDatabase(worldName, vaultMap, sqlite);
			return db;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public VaultMapCoreDataSource getCoreSource() {
		return coreSource;
	}
	
	public VaultMapReinforcementDataSource getReinforcementSource() {
		return reinforcementSource;
	}
	
	public VaultMapBastionDataSource getBastionSource() {
		return bastionSource;
	}
	
	public VaultMapReinforcementBlueprintDataSource getReinforcementBlueprintSource() {
		return reinforcementBlueprintSource;
	}
	
	public VaultMapBastionBlueprintDataSource getBastionBlueprintSource() {
		return bastionBlueprintSource;
	}
	
	public VaultMapSnitchDataSource getSnitchSource() {
		return snitchSource;
	}
	
	public Map<String, Group> getGroupSource() {
		return groupSource;
	}
	
	public Map<String, Company> getCompanySource() {
		return companySource;
	}
	
	public void setBattle(VaultBattle battle) {
		this.battle = battle;
	}
	
	public VaultBattle getBattle() {
		return battle;
	}
	
}
