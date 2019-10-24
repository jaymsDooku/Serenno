package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.kit.ItemStackKey;
import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.util.ItemUtil;
import io.jayms.serenno.util.SerennoDataSource;

public class VaultMapReinforcementBlueprintDataSource implements SerennoDataSource<ReinforcementBlueprint, ItemStack> {

	private static final String CREATE_REIN_BLUEPRINT = "CREATE TABLE IF NOT EXISTS REINFORCEMENT_BLUEPRINT("
			+ "Name TEXT PRIMARY KEY,"
			+ "DisplayName TEXT, "
			+ "ItemStackMaterial TEXT UNIQUE, "
			+ "ItemStackAmount INTEGER, "
			+ "RegenRateAmount REAL, "
			+ "RegenRateInterval INTEGER, "
			+ "Health REAL, "
			+ "MaturationTime INTEGEER, "
			+ "AcidTime INTEGER, "
			+ "DamageCooldown INTEGER, "
			+ "DefaultDamage REAL"
			+ ")";
	
	private static final String INSERT_REIN_BLUEPRINT = "INSERT INTO REINFORCEMENT_BLUEPRINT("
			+ "Name, DisplayName, ItemStackMaterial, ItemStackAmount, RegenRateAmount, RegenRateInterval, Health, MaturationTime, AcidTime, DamageCooldown, DefaultDamage"
			+ ") VALUES ("
			+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
			+ ")";
	
	private static final String UPDATE_REIN_BLUEPRINT = "UPDATE REINFORCEMENT_BLUEPRINT SET "
			+ "DisplayName = ?, "
			+ "RegenRateAmount = ?, "
			+ "RegenRateInterval = ?, "
			+ "Health = ?, "
			+ "MaturationTime = ?, "
			+ "AcidTime = ?, "
			+ "DamageCooldown = ?, "
			+ "DefaultDamage = ? "
			+ "WHERE ItemStackMaterial = ? AND ItemStackAmount = ?";
	
	private static final String SELECT_REIN_NAME_BLUEPRINT = "SELECT DisplayName, ItemStackMaterial, ItemStackAmount, RegenRateAmount, RegenRateInterval, Health, MaturationTime, AcidTime, DamageCooldown, DefaultDamage "
			+ "FROM REINFORCEMENT_BLUEPRINT WHERE Name = ?";
	
	private static final String SELECT_REIN_BLUEPRINT = "SELECT Name, DisplayName, RegenRateAmount, RegenRateInterval, Health, MaturationTime, AcidTime, DamageCooldown, DefaultDamage "
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
		db.getDatabase().modifyQuery(CREATE_REIN_BLUEPRINT, true);
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
			ps.setString(3, ItemUtil.getName(value.getItemStack()));
			ps.setInt(4, value.getItemStack().getAmount());
			ps.setDouble(5, value.getRegenRate().getAmount());
			ps.setLong(6, value.getRegenRate().getInterval());
			ps.setDouble(7, value.getMaxHealth());
			ps.setLong(8, value.getMaturationTime());
			ps.setLong(9, value.getAcidTime());
			ps.setLong(10, value.getDamageCooldown());
			ps.setDouble(11, value.getDefaultDamage());
			ps.executeUpdate();
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
			ps.setLong(6, value.getAcidTime());
			ps.setLong(7, value.getDamageCooldown());
			ps.setDouble(8, value.getDefaultDamage());
			ps.setString(9, ItemUtil.getName(value.getItemStack()));
			ps.setInt(10, value.getItemStack().getAmount());
			ps.executeUpdate();
			ps.close();
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
			
			ReinforcementBlueprint rb = ReinforcementBlueprint.builder()
					.name(name)
					.displayName(rs.getString("DisplayName"))
					.itemStack(new ItemStack(Material.valueOf(rs.getString("ItemStackMaterial")), rs.getInt("ItemStackAmount")))
					.regenRate(new RegenRate(rs.getInt("RegenRateAmount"), rs.getInt("RegenRateInterval")))
					.maxHealth(rs.getDouble("MaxHealth"))
					.maturationTime(rs.getLong("MaturationTime"))
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
					.maxHealth(rs.getDouble("MaxHealth"))
					.maturationTime(rs.getLong("MaturationTime"))
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
		if (!blueprints.isEmpty()) {
			return blueprints.values();
		}
		
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_ALL_REIN_BLUEPRINT);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				ReinforcementBlueprint rb = ReinforcementBlueprint.builder()
						.name(rs.getString("Name"))
						.displayName(rs.getString("DisplayName"))
						.itemStack(new ItemStack(Material.valueOf(rs.getString("ItemStackMaterial")), rs.getInt("ItemStackAmount")))
						.regenRate(new RegenRate(rs.getInt("RegenRateAmount"), rs.getInt("RegenRateInterval")))
						.maxHealth(rs.getDouble("MaxHealth"))
						.maturationTime(rs.getLong("MaturationTime"))
						.acidTime(rs.getLong("AcidTime"))
						.damageCooldown(rs.getLong("DamageCooldown"))
						.defaultDamage(rs.getDouble("DefaultDamage"))
						.build();
				
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
