package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.kit.ItemStackKey;
import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.util.SerennoDataSource;

public class VaultMapReinforcementBlueprintDataSource implements SerennoDataSource<ReinforcementBlueprint, ItemStack> {

	private static final String CREATE_MATERIAL = "CREATE TABLE IF NOT EXISTS MATERIAL("
			+ "ReinforcementBlueprint TEXT, "
			+ "MaterialName TEXT, "
			+ "Reinforceable INTEGER, "
			+ "PRIMARY KEY(ReinforcementBlueprint, MaterialName)"
			+ ")";
	
	private static final String INSERT_MATERIAL = "INSERT INTO MATERIAL("
			+ "ReinforcementBlueprint, MaterialName, Reinforceable"
			+ ") VALUES ("
			+ "?, ?, ?"
			+ ")";
	
	private static final String SELECT_MATERIAL = "SELECT MaterialName, Reinforceable FROM MATERIAL WHERE ReinforcementBlueprint = ?";
	
	private static final String DELETE_MATERIAL = "DELETE FROM MATERIAL WHERE ReinforcementBlueprint = ?";
	
	private static final String CREATE_REIN_BLUEPRINT = "CREATE TABLE IF NOT EXISTS REINFORCEMENT_BLUEPRINT("
			+ "Name TEXT PRIMARY KEY,"
			+ "DisplayName TEXT, "
			+ "ItemStackMaterial TEXT UNIQUE, "
			+ "ItemStackAmount INTEGER, "
			+ "RegenRateAmount REAL, "
			+ "RegenRateInterval INTEGER, "
			+ "Health REAL, "
			+ "MaturationTime INTEGER, "
			+ "MaturationScale REAL, "
			+ "AcidTime INTEGER, "
			+ "DamageCooldown INTEGER, "
			+ "DefaultDamage REAL"
			+ ")";
	
	private static final String INSERT_REIN_BLUEPRINT = "INSERT INTO REINFORCEMENT_BLUEPRINT("
			+ "Name, DisplayName, ItemStackMaterial, ItemStackAmount, RegenRateAmount, RegenRateInterval, Health, MaturationTime, MaturationScale, AcidTime, DamageCooldown, DefaultDamage"
			+ ") VALUES ("
			+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
			+ ")";
	
	private static final String UPDATE_REIN_BLUEPRINT = "UPDATE REINFORCEMENT_BLUEPRINT SET "
			+ "DisplayName = ?, "
			+ "RegenRateAmount = ?, "
			+ "RegenRateInterval = ?, "
			+ "Health = ?, "
			+ "MaturationTime = ?, "
			+ "MaturationScale = ?, "
			+ "AcidTime = ?, "
			+ "DamageCooldown = ?, "
			+ "DefaultDamage = ? "
			+ "WHERE ItemStackMaterial = ? AND ItemStackAmount = ?";
	
	private static final String SELECT_REIN_NAME_BLUEPRINT = "SELECT DisplayName, ItemStackMaterial, ItemStackAmount, RegenRateAmount, RegenRateInterval, Health, MaturationTime, MaturationScale, AcidTime, DamageCooldown, DefaultDamage "
			+ "FROM REINFORCEMENT_BLUEPRINT WHERE Name = ?";
	
	private static final String SELECT_REIN_BLUEPRINT = "SELECT Name, DisplayName, RegenRateAmount, RegenRateInterval, Health, MaturationTime, MaturationScale, AcidTime, DamageCooldown, DefaultDamage "
			+ "FROM REINFORCEMENT_BLUEPRINT WHERE ItemStackMaterial = ? AND ItemStackAmount = ?";
	
	private static final String SELECT_ALL_REIN_BLUEPRINT = "SELECT * FROM REINFORCEMENT_BLUEPRINT";
	
	private static final String DELETE_REIN_BLUEPRINT = "DELETE FROM REINFORCEMENT_BLUEPRINT WHERE ItemStackMaterial = ? AND ItemStackAmount = ?";
	
	private VaultMapDatabase db;
	private Map<ItemStackKey, ReinforcementBlueprint> blueprints = new HashMap<>();
	private Map<String, ReinforcementBlueprint> namedBlueprints = new HashMap<>();
	
	public VaultMapReinforcementBlueprintDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(CREATE_REIN_BLUEPRINT);
			ps.execute();
			ps.close();
			
			ps = db.getDatabase().getConnection().prepareStatement(CREATE_MATERIAL);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void cache(ReinforcementBlueprint value) {
		blueprints.put(new ItemStackKey(value.getItemStack()), value);
		namedBlueprints.put(value.getName(), value);
	}
	
	@Override
	public void create(ReinforcementBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(INSERT_REIN_BLUEPRINT);
			ps.setString(1, value.getName());
			ps.setString(2, value.getDisplayName());
			ps.setString(3, value.getItemStack().getType().toString());
			ps.setInt(4, value.getItemStack().getAmount());
			ps.setDouble(5, value.getRegenRate().getAmount());
			ps.setLong(6, value.getRegenRate().getInterval());
			ps.setDouble(7, value.getMaxHealth());
			ps.setLong(8, value.getMaturationTime());
			ps.setDouble(9, value.getMaturationScale());
			ps.setLong(10, value.getAcidTime());
			ps.setLong(11, value.getDamageCooldown());
			ps.setDouble(12, value.getDefaultDamage());
			ps.executeUpdate();
			ps.close();
			
			ps = db.getDatabase().getConnection().prepareStatement(INSERT_MATERIAL);
			for (Material reinforceable : value.getReinforceableMaterials()) {
				ps.setString(1, value.getName());
				ps.setString(2, reinforceable.name());
				ps.setBoolean(3, true);
				ps.addBatch();
			}
			for (Material unreinforceable : value.getUnreinforceableMaterials()) {
				ps.setString(1, value.getName());
				ps.setString(2, unreinforceable.name());
				ps.setBoolean(3, false);
				ps.addBatch();
			}
			ps.executeBatch();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cache(value);
	}

	@Override
	public void update(ReinforcementBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(UPDATE_REIN_BLUEPRINT);
			ps.setString(1, value.getDisplayName());
			ps.setDouble(2, value.getRegenRate().getAmount());
			ps.setLong(3, value.getRegenRate().getInterval());
			ps.setDouble(4, value.getMaxHealth());
			ps.setLong(5, value.getMaturationTime());
			ps.setDouble(6, value.getMaturationScale());
			ps.setLong(7, value.getAcidTime());
			ps.setLong(8, value.getDamageCooldown());
			ps.setDouble(9, value.getDefaultDamage());
			ps.setString(10, value.getItemStack().getType().toString());
			ps.setInt(11, value.getItemStack().getAmount());
			ps.executeUpdate();
			ps.close();
			
			ps = db.getDatabase().getConnection().prepareStatement(DELETE_MATERIAL);
			ps.setString(1, value.getName());
			ps.executeUpdate();
			ps.close();
			
			ps = db.getDatabase().getConnection().prepareStatement(INSERT_MATERIAL);
			for (Material reinforceable : value.getReinforceableMaterials()) {
				ps.setString(1, value.getName());
				ps.setString(2, reinforceable.name());
				ps.setBoolean(3, true);
				ps.addBatch();
			}
			for (Material unreinforceable : value.getUnreinforceableMaterials()) {
				ps.setString(1, value.getName());
				ps.setString(2, unreinforceable.name());
				ps.setBoolean(3, false);
				ps.addBatch();
			}
			ps.executeBatch();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		cache(value);
	}
	
	public ReinforcementBlueprint get(String name) {
		if (namedBlueprints.containsKey(name)) {
			return namedBlueprints.get(name);
		}
		
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_REIN_NAME_BLUEPRINT);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			ReinforcementBlueprint.Builder rbb = ReinforcementBlueprint.builder()
					.name(name)
					.displayName(rs.getString("DisplayName"))
					.itemStack(new ItemStack(Material.valueOf(rs.getString("ItemStackMaterial")), rs.getInt("ItemStackAmount")))
					.regenRate(new RegenRate(rs.getInt("RegenRateAmount"), rs.getInt("RegenRateInterval")))
					.maxHealth(rs.getDouble("Health"))
					.maturationTime(rs.getLong("MaturationTime"))
					.maturationScale(rs.getDouble("MaturationScale"))
					.acidTime(rs.getLong("AcidTime"))
					.damageCooldown(rs.getLong("DamageCooldown"))
					.defaultDamage(rs.getDouble("DefaultDamage"));
			rs.close();
			ps.close();
			
			ps = db.getDatabase().getConnection().prepareStatement(SELECT_MATERIAL);
			ps.setString(1, name);
			rs = ps.executeQuery();
			while (rs.next()) {
				List<Material> reinforceable = new ArrayList<>();
				List<Material> unreinforceable = new ArrayList<>();
				
				Material material = Material.valueOf(rs.getString("MaterialName"));
				if (rs.getBoolean("Reinforceable")) {
					reinforceable.add(material);
				} else {
					unreinforceable.add(material);
				}
				
				rbb.reinforceableMaterials(reinforceable);
				rbb.unreinforceableMaterials(unreinforceable);
			}
			ps.close();
			
			ReinforcementBlueprint rb = rbb.build();
			cache(rb);
			
			return rb;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ReinforcementBlueprint get(ItemStack key) {
		ItemStackKey itemStackKey = new ItemStackKey(key);
		if (blueprints.containsKey(itemStackKey)) {
			return blueprints.get(itemStackKey);
		}
		
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_REIN_BLUEPRINT);
			ps.setString(1, key.getType().toString());
			ps.setInt(2, key.getAmount());
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			ReinforcementBlueprint rb = ReinforcementBlueprint.builder()
					.name(rs.getString("Name"))
					.displayName(rs.getString("DisplayName"))
					.itemStack(key)
					.regenRate(new RegenRate(rs.getInt("RegenRateAmount"), rs.getInt("RegenRateInterval")))
					.maxHealth(rs.getDouble("Health"))
					.maturationTime(rs.getLong("MaturationTime"))
					.maturationScale(rs.getDouble("MaturationScale"))
					.acidTime(rs.getLong("AcidTime"))
					.damageCooldown(rs.getLong("DamageCooldown"))
					.defaultDamage(rs.getDouble("DefaultDamage"))
					.build();
			ps.close();
			
			cache(rb);
			
			return rb;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean exists(ItemStack key) {
		return get(key) != null;
	}

	@Override
	public Collection<ReinforcementBlueprint> getAll() {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_ALL_REIN_BLUEPRINT);
			ResultSet rs = ps.executeQuery();
			System.out.println("fetchSize: " + rs.getFetchSize());
			
			while (rs.next()) {
				ReinforcementBlueprint rb = ReinforcementBlueprint.builder()
						.name(rs.getString("Name"))
						.displayName(rs.getString("DisplayName"))
						.itemStack(new ItemStack(Material.valueOf(rs.getString("ItemStackMaterial")), rs.getInt("ItemStackAmount")))
						.regenRate(new RegenRate(rs.getInt("RegenRateAmount"), rs.getInt("RegenRateInterval")))
						.maxHealth(rs.getDouble("Health"))
						.maturationTime(rs.getLong("MaturationTime"))
						.maturationScale(rs.getDouble("MaturationScale"))
						.acidTime(rs.getLong("AcidTime"))
						.damageCooldown(rs.getLong("DamageCooldown"))
						.defaultDamage(rs.getDouble("DefaultDamage"))
						.build();
				System.out.println("rb: " + rb);
				
				cache(rb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return blueprints.values();
	}

	@Override
	public void delete(ReinforcementBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(DELETE_REIN_BLUEPRINT);
			ps.setString(1, value.getItemStack().getType().toString());
			ps.setInt(2, value.getItemStack().getAmount());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		blueprints.remove(new ItemStackKey(value.getItemStack()));
		namedBlueprints.remove(value.getName());
	}

}
