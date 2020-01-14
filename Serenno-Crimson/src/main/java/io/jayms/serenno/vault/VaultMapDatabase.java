package io.jayms.serenno.vault;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import io.jayms.serenno.db.MongoAPI;
import io.jayms.serenno.vault.data.mongodb.*;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.Files;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.db.sql.Database;
import io.jayms.serenno.db.sql.SQLite;
import io.jayms.serenno.game.vaultbattle.VaultBattle;
import io.jayms.serenno.model.finance.company.Company;
import io.jayms.serenno.model.finance.company.ServerCompany;
import io.jayms.serenno.model.group.Group;
import net.md_5.bungee.api.ChatColor;

public class VaultMapDatabase {

	public static final String GOTO_X = "goto_x";
	public static final String GOTO_Y = "goto_y";
	public static final String GOTO_Z = "goto_z";
	public static final String GOTO_YAW = "goto_yaw";
	public static final String GOTO_PITCH = "goto_pitch";
	public static final String PLAYER_LIST = "player_list";
	public static final String PLAYER_LIST_TYPE = "player_list_type";
	public static final String ATTACKERS_COLOUR = "attackers_colour";
	public static final String DEFENDERS_COLOUR = "defenders_colour";

	private static final String ATTACKERS = "Attackers";
	private static final String DEFENDERS = "Defenders";
	
	private static final String CREATE_INFO = "CREATE TABLE IF NOT EXISTS INFO("
			+ "InformationID INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "GotoX REAL,"
			+ "GotoY REAL,"
			+ "GotoZ REAL, "
			+ "GotoPitch REAL, "
			+ "GotoYaw REAL, "
			+ "PlayerListType TEXT, "
			+ "AttackersColour TEXT, "
			+ "DefendersColour TEXT"
			+ ");";
	
	private static final String INSERT_INFO = "INSERT INTO INFO"
			+ "("
			+ "GotoX, GotoY, GotoZ, GotoPitch, GotoYaw, PlayerListType, AttackersColour, DefendersColour"
			+ ") VALUES("
			+ "?, ?, ?, ?, ?, ?, ?, ?"
			+ ")";
	
	private static final String UPDATE_INFO = "UPDATE INFO "
			+ "SET GotoX = ?, "
			+ "GotoY = ?, "
			+ "GotoZ = ?, "
			+ "GotoPitch = ?, "
			+ "GotoYaw = ?, "
			+ "PlayerListType = ?, "
			+ "AttackersColour = ?, "
			+ "DefendersColour = ? "
			+ "WHERE InformationID = ?";
	
	private static final String SELECT_INFO = "SELECT * FROM INFO LIMIT 1";
	
	private static final String DELETE_INFO = "DELETE FROM INFO WHERE ReinforcementID = ?";

	private MongoDatabase database;

	private String worldName;
	private World world;
	private VaultMap vaultMap;

	private double gotoX;
	private double gotoY;
	private double gotoZ;
	private float gotoYaw;
	private float gotoPitch;
	
	private VaultMapPlayerListType playerListType = VaultMapPlayerListType.NONE;
	private List<UUID> playerList;
	private MongoVaultMapCoreDataSource coreSource;
	private MongoVaultMapReinforcementDataSource reinforcementSource;
	private MongoVaultMapBastionDataSource bastionSource;
	private MongoVaultMapReinforcementBlueprintDataSource reinforcementBlueprintSource;
	private MongoVaultMapBastionBlueprintDataSource bastionBlueprintSource;
	private MongoVaultMapSnitchDataSource snitchSource;
	private Map<String, Group> groupSource;
	private BiMap<ChatColor, String> groupColours;
	private Map<String, Company> companySource;
	
	private boolean loaded;
	
	private VaultBattle battle;
	
	public VaultMapDatabase(String worldName, VaultMap vaultMap) {
		this.worldName = worldName;
		this.vaultMap = vaultMap;
		this.database = MongoAPI.getDatabase(worldName);
		this.playerList = new ArrayList<>();
		this.coreSource = new MongoVaultMapCoreDataSource(this);
		this.reinforcementSource = new MongoVaultMapReinforcementDataSource(this);
		this.bastionSource = new MongoVaultMapBastionDataSource(this);
		this.reinforcementBlueprintSource = new MongoVaultMapReinforcementBlueprintDataSource(this);
		this.bastionBlueprintSource = new MongoVaultMapBastionBlueprintDataSource(this);
		this.snitchSource = new MongoVaultMapSnitchDataSource(this);
		this.groupSource = new HashMap<>();
		this.groupColours = HashBiMap.create();
		this.companySource = new HashMap<>();
		
		init();
	}

	public MongoCollection<Document> getInfoCollection() {
		return database.getCollection("information");
	}

	public MongoDatabase getDatabase() {
		return database;
	}

	public String getWorldName() {
		return worldName;
	}
	
	public World getWorld() {
		if (world == null) {
			world = Bukkit.getWorld(worldName);
		}
		return world;
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
		
		database = MongoAPI.getDatabase(worldName);
	}
	
	public void load() {
		FindIterable<Document> query = getInfoCollection().find();
		Document doc = query.first();
		if (doc == null) {
			return;
		}
		gotoX = doc.getDouble(GOTO_X);
		gotoY = doc.getDouble(GOTO_Y);
		gotoZ = doc.getDouble(GOTO_Z);
		double dGotoYaw = doc.getDouble(GOTO_YAW);
		double dGotoPitch = doc.getDouble(GOTO_PITCH);
		gotoYaw = (float) dGotoYaw;
		gotoPitch = (float) dGotoPitch;
		List<String> uuidList = doc.getList(PLAYER_LIST, String.class);
		playerList = uuidList.stream().map(u -> UUID.fromString(u)).collect(Collectors.toList());
		playerListType = VaultMapPlayerListType.valueOf(doc.getString(PLAYER_LIST_TYPE));
		String attackerColour = doc.getString(ATTACKERS_COLOUR);
		if (attackerColour != null) {
			groupColours.put(ChatColor.valueOf(attackerColour), ATTACKERS.toLowerCase());
		}
		String defenderColour = doc.getString(DEFENDERS_COLOUR);
		if (defenderColour != null) {
			groupColours.put(ChatColor.valueOf(defenderColour), DEFENDERS.toLowerCase());
		}

		SerennoCrimson.get().getLogger().info("Loaded information for vault map: " + vaultMap.getArena().getName());
		loaded = true;
	}

	public Document infoDocument() {
		Document doc = new Document();
		doc.put(GOTO_X, gotoX);
		doc.put(GOTO_Y, gotoY);
		doc.put(GOTO_Z, gotoZ);
		doc.put(GOTO_YAW, gotoYaw);
		doc.put(GOTO_PITCH, gotoPitch);
		doc.put(PLAYER_LIST, playerList);
		doc.put(PLAYER_LIST_TYPE, playerListType.toString());
		ChatColor attackersColour = groupColours.inverse().get(ATTACKERS.toLowerCase());
		if (attackersColour != null) {
			doc.put(ATTACKERS_COLOUR, attackersColour.name());
		}
		ChatColor defendersColour = groupColours.inverse().get(DEFENDERS.toLowerCase());
		if (defendersColour != null) {
			doc.put(DEFENDERS_COLOUR, defendersColour.name());
		}
		return doc;
	}
	
	public void updateInfo() {
		Document doc = infoDocument();
		if (getInfoCollection().countDocuments() > 0) {
			getInfoCollection().drop();
		}
		getInfoCollection().insertOne(doc);
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
				return !playerList.contains(player.getUniqueId());
			case WHITE:
				return playerList.contains(player.getUniqueId());
			default:
				break;
		}
		return playerListType == VaultMapPlayerListType.NONE;
	}
	
	public Location getGotoLocation() {
		return new Location(vaultMap.getOriginalWorld(), gotoX, gotoY, gotoZ, gotoYaw, gotoPitch);
	}
	
	public void delete() {
		database.drop();
		SerennoCrimson.get().getLogger().info("Deleted Mongo database for vault map: " + vaultMap.getArena().getRegion().getName());
	}
	
	public VaultMapDatabase copy(String worldName, File file) {
		VaultMapDatabase db = new VaultMapDatabase(worldName, vaultMap);
		MongoAPI.copy(getInfoCollection(), db.getInfoCollection());
		MongoAPI.copy(getReinforcementBlueprintSource().getCollection(), db.getReinforcementBlueprintSource().getCollection());
		MongoAPI.copy(getBastionBlueprintSource().getCollection(), db.getBastionBlueprintSource().getCollection());
		MongoAPI.copy(getReinforcementSource().getCollection(), db.getReinforcementSource().getCollection());
		MongoAPI.copy(getBastionSource().getCollection(), db.getBastionSource().getCollection());
		MongoAPI.copy(getSnitchSource().getCollection(), db.getSnitchSource().getCollection());
		MongoAPI.copy(getCoreSource().getCollection(), db.getCoreSource().getCollection());
		return db;
	}
	
	public List<UUID> getPlayerList() {
		return playerList;
	}
	
	public MongoVaultMapCoreDataSource getCoreSource() {
		return coreSource;
	}
	
	public MongoVaultMapReinforcementDataSource getReinforcementSource() {
		return reinforcementSource;
	}
	
	public MongoVaultMapBastionDataSource getBastionSource() {
		return bastionSource;
	}
	
	public MongoVaultMapReinforcementBlueprintDataSource getReinforcementBlueprintSource() {
		return reinforcementBlueprintSource;
	}
	
	public MongoVaultMapBastionBlueprintDataSource getBastionBlueprintSource() {
		return bastionBlueprintSource;
	}
	
	public MongoVaultMapSnitchDataSource getSnitchSource() {
		return snitchSource;
	}
	
	public Map<String, Group> getGroupSource() {
		return groupSource;
	}
	
	public BiMap<ChatColor, String> getGroupColours() {
		return groupColours;
	}
	
	public void setGroupColour(ChatColor colour, String groupName) {
		groupName = groupName.toLowerCase();
		if (!groupSource.containsKey(groupName)) {
			return;
		}
		
		groupColours.forcePut(colour, groupName);
		updateInfo();
	}
	
	public String getGroupNameFromColour(ChatColor colour) {
		return groupColours.get(colour);
	}
	
	public ChatColor getTeamColourFromGroupName(String groupName) {
		return groupColours.inverse().get(groupName.toLowerCase());
	}
	
	public void removeGroupColour(ChatColor colour) {
		if (!groupColours.containsKey(colour)) {
			return;
		}
		
		groupColours.remove(colour);
		updateInfo();
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
