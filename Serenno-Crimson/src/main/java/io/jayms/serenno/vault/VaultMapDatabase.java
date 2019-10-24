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
import org.bukkit.entity.Player;
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
import io.jayms.serenno.util.LocationTools;

public class VaultMapDatabase {
	
	private static final String ATTACKERS = "Attackers";
	private static final String DEFENDERS = "Defenders";
	
	private static final String CREATE_INFO = "CREATE TABLE IF NOT EXISTS INFO("
			+ "InformationID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "GotoX REAL,"
			+ "GotoY REAL,"
			+ "GotoZ REAL, "
			+ "GotoPitch REAL, "
			+ "GotoYaw REAL, "
			+ "PlayerListType TEXT"
			+ ");";
	
	private static final String INSERT_INFO = "INSERT INTO INFO"
			+ "("
			+ "GotoX, GotoY, GotoZ, GotoPitch, GotoYaw, PlayerListType"
			+ ") VALUES("
			+ "?, ?, ?, ?, ?, ?"
			+ ")";
	
	private static final String UPDATE_INFO = "UPDATE INFO "
			+ "SET GotoX = ?, "
			+ "GotoY = ?, "
			+ "GotoZ = ?, "
			+ "GotoPitch = ?, "
			+ "GotoYaw = ?,"
			+ "PlayerListType = ?"
			+ "WHERE InformationID = ?";
	
	private static final String SELECT_INFO = "SELECT * FROM INFO LIMIT 1";
	
	private static final String DELETE_INFO = "DELETE FROM INFO WHERE ReinforcementID = ?";

	private SQLite database;

	private String worldName;
	private VaultMap vaultMap;
	
	private int informationID;
	private double gotoX;
	private double gotoY;
	private double gotoZ;
	private float gotoYaw;
	private float gotoPitch;
	
	private VaultMapPlayerListType playerListType = VaultMapPlayerListType.NONE;
	private VaultMapPlayerList playerList;
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
		this.playerList = new VaultMapPlayerList(this);
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
		companySource.put(attackerCompany.getName().toLowerCase(), attackerCompany);
		companySource.put(defenderCompany.getName().toLowerCase(), defenderCompany);
		
		Group attackerGroup = new Group(ATTACKERS, attackerCompany);
		Group defenderGroup = new Group(DEFENDERS, defenderCompany);
		groupSource.put(attackerGroup.getName().toLowerCase(), attackerGroup);
		groupSource.put(defenderGroup.getName().toLowerCase(), defenderGroup);
		
		database.open();
		database.modifyQuery(CREATE_INFO, true);
		coreSource.createTables();
		reinforcementSource.createTables();
		bastionSource.createTables();
		reinforcementBlueprintSource.createTables();
		bastionBlueprintSource.createTables();
		snitchSource.createTables();
	}
	
	public void load() {
		try {
			PreparedStatement ps = database.getConnection().prepareStatement(SELECT_INFO);
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return;
			}
			
			informationID = rs.getInt("InformationID");
			
			gotoX = rs.getDouble("GotoX");
			gotoY = rs.getDouble("GotoY");
			gotoZ = rs.getDouble("GotoZ");
			gotoYaw = rs.getFloat("GotoYaw");
			gotoPitch = rs.getFloat("GotoPitch");
			playerListType = VaultMapPlayerListType.valueOf(rs.getString("PlayerListType"));
			SerennoCrimson.get().getLogger().info("Loaded information for vault map: " + vaultMap.getArena().getName());
			ps.close();
			
			loaded = true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateInfo() {
		try {
			PreparedStatement ps = loaded ? database.getConnection().prepareStatement(UPDATE_INFO) :
				database.getConnection().prepareStatement(INSERT_INFO, Statement.RETURN_GENERATED_KEYS);
			ps.setDouble(1, gotoX);
			ps.setDouble(2, gotoY);
			ps.setDouble(3, gotoZ);
			ps.setDouble(4, gotoPitch);
			ps.setDouble(5, gotoYaw);
			ps.setString(6, playerListType.toString());
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
	}
	
	public void setGotoLocation(Location gotoLocation) {
		this.gotoX = gotoLocation.getX();
		this.gotoY = gotoLocation.getY();
		this.gotoZ = gotoLocation.getZ();
		this.gotoPitch = gotoLocation.getPitch();
		this.gotoYaw = gotoLocation.getYaw();
		updateInfo();
	}
	
	public void setPlayerListType(VaultMapPlayerListType playerListType) {
		this.playerListType = playerListType;
		updateInfo();
	}
	
	public boolean isAllowed(Player player) {
		switch (playerListType) {
			case BLACK:
				return !playerList.inPlayerList(player);
			case WHITE:
				return playerList.inPlayerList(player);
			default:
				break;
		}
		return playerListType == VaultMapPlayerListType.NONE;
	}
	
	public Location getGotoLocation() {
		return new Location(vaultMap.getOriginalWorld(), gotoX, gotoY, gotoZ, gotoYaw, gotoPitch);
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
	
	public VaultMapPlayerList getPlayerList() {
		return playerList;
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
