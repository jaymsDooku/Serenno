package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.util.ItemUtil;
import io.jayms.serenno.util.SerennoDataSource;

public class VaultMapReinforcementBlueprintDataSource implements SerennoDataSource<ReinforcementBlueprint, String> {

	private static final String CREATE_REIN_BLUEPRINT = "CREATE TABLE IF NOT EXISTS REINFORCEMENT_BLUEPRINT("
			+ "Name TEXT PRIMARY KEY,"
			+ "DisplayName TEXT, "
			+ "ItemStackMaterial TEXT, "
			+ "ItemStackAmount INTEGER, "
			+ "RegenRateAmount INTEGER, "
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
			+ "ItemStackMaterial = ?, "
			+ "ItemStackAmount = ?, "
			+ "RegenRateAmount = ?, "
			+ "RegenRateInterval = ?, "
			+ "Health = ?, "
			+ "MaturationTime = ?, "
			+ "AcidTime = ?, "
			+ "DamageCooldown = ?, "
			+ "DefaultDamage = ? "
			+ "WHERE Name = ?";
	
	private static final String SELECT_REIN_BLUEPRINT = "SELECT DisplayName, ItemStackMaterial, ItemStackAmount, RegenRateAmount, RegenRateInterval, Health, MaturationTime, AcidTime, DamageCooldown, DefaultDamage "
			+ "FROM REINFORCEMENT_BLUEPRINT WHERE Name = ?";
	
	private static final String DELETE_REIN_BLUEPRINT = "DELETE FROM REINFORCEMENT_BLUEPRINT WHERE Name = ?";
	
	private VaultMapDatabase db;
	
	public VaultMapReinforcementBlueprintDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		db.getDatabase().modifyQuery(CREATE_REIN_BLUEPRINT, true);
	}
	
	@Override
	public void create(ReinforcementBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(INSERT_REIN_BLUEPRINT);
			ps.setString(1, value.getName());
			ps.setString(2, value.getDisplayName());
			ps.setString(3, ItemUtil.getName(value.getItemStack()));
			ps.setInt(4, value.getItemStack().getAmount());
			ps.setInt(5, value.getRegenRate().getAmount());
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
	}

	@Override
	public void update(ReinforcementBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(UPDATE_REIN_BLUEPRINT);
			ps.setString(1, value.getDisplayName());
			ps.setString(2, ItemUtil.getName(value.getItemStack()));
			ps.setInt(3, value.getItemStack().getAmount());
			ps.setInt(4, value.getRegenRate().getAmount());
			ps.setLong(5, value.getRegenRate().getInterval());
			ps.setDouble(6, value.getMaxHealth());
			ps.setLong(7, value.getMaturationTime());
			ps.setLong(8, value.getAcidTime());
			ps.setLong(9, value.getDamageCooldown());
			ps.setDouble(10, value.getDefaultDamage());
			ps.setString(11, value.getName());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ReinforcementBlueprint get(String key) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_REIN_BLUEPRINT);
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			ReinforcementBlueprint rb = ReinforcementBlueprint.builder()
					.name(key)
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
			return rb;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public boolean exists(String key) {
		return get(key) != null;
	}

	@Override
	public Set<ReinforcementBlueprint> getAll() {
		Set<ReinforcementBlueprint> all = new HashSet<>();
		return all;
	}

	@Override
	public void delete(ReinforcementBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(DELETE_REIN_BLUEPRINT);
			ps.setString(1, value.getName());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}
