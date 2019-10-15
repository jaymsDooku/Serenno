package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.model.citadel.RegenRate;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint.PearlConfig;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.util.ItemUtil;
import io.jayms.serenno.util.SerennoDataSource;

public class VaultMapBastionBlueprintDataSource implements SerennoDataSource<BastionBlueprint, String> {

	private static final String CREATE_BASTION_BLUEPRINT = "CREATE TABLE IF NOT EXISTS BASTION_BLUEPRINT("
			+ "Name TEXT PRIMARY KEY,"
			+ "DisplayName TEXT, "
			+ "ItemStackMaterial TEXT, "
			+ "ItemStackAmount INTEGER, "
			+ "BastionShape TEXT, "
			+ "Radius INTEGER, "
			+ "RequiresMaturity INTEGER, "
			+ "PearlBlock INTEGEER, "
			+ "PearlBlockMidAir INTEGER, "
			+ "PearlConsumeOnBlock INTEGER, "
			+ "PearlDamage REAL"
			+ ")";
	
	private static final String INSERT_BASTION_BLUEPRINT = "INSERT INTO BASTION_BLUEPRINT("
			+ "Name, DisplayName, ItemStackMaterial, ItemStackAmount, BastionShape, Radius, RequiresMaturity, PearlBlock, PearlBlockMidAir, PearlConsumeOnBlock, PearlDamage"
			+ ") VALUES ("
			+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
			+ ")";
	
	private static final String UPDATE_BASTION_BLUEPRINT = "UPDATE BASTION_BLUEPRINT SET "
			+ "DisplayName = ?, "
			+ "ItemStackMaterial = ?, "
			+ "ItemStackAmount = ?, "
			+ "BastionShape = ?, "
			+ "Radius = ?, "
			+ "RequiresMaturity = ?, "
			+ "PearlBlock = ?, "
			+ "PearlBlockMidAir = ?, "
			+ "PearlConsumeOnBlock = ?, "
			+ "PearlDamage = ? "
			+ "WHERE Name = ?";
	
	private static final String SELECT_BASTION_BLUEPRINT = "SELECT DisplayName, ItemStackMaterial, ItemStackAmount, RegenRateAmount, RegenRateInterval, Health, MaturationTime, AcidTime, DamageCooldown, DefaultDamage "
			+ "FROM BASTION_BLUEPRINT WHERE Name = ?";
	
	private static final String DELETE_BASTION_BLUEPRINT = "DELETE FROM BASTION_BLUEPRINT WHERE Name = ?";
	
	private VaultMapDatabase db;
	
	public VaultMapBastionBlueprintDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		db.getDatabase().modifyQuery(CREATE_BASTION_BLUEPRINT, true);
	}
	
	@Override
	public void create(BastionBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(INSERT_BASTION_BLUEPRINT);
			ps.setString(1, value.getName());
			ps.setString(2, value.getDisplayName());
			ps.setString(3, ItemUtil.getName(value.getItemStack()));
			ps.setInt(4, value.getItemStack().getAmount());
			ps.setString(5, value.getShape().toString());
			ps.setInt(6, value.getRadius());
			ps.setBoolean(7, value.requiresMaturity());
			ps.setBoolean(8, value.getPearlConfig().block());
			ps.setBoolean(9, value.getPearlConfig().blockMidAir());
			ps.setBoolean(10, value.getPearlConfig().consumeOnBlock());
			ps.setDouble(11, value.getPearlConfig().getDamage());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(BastionBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(UPDATE_BASTION_BLUEPRINT);
			ps.setString(1, value.getDisplayName());
			ps.setString(2, ItemUtil.getName(value.getItemStack()));
			ps.setInt(3, value.getItemStack().getAmount());
			ps.setString(4, value.getShape().toString());
			ps.setInt(5, value.getRadius());
			ps.setBoolean(6, value.requiresMaturity());
			ps.setBoolean(7, value.getPearlConfig().block());
			ps.setBoolean(8, value.getPearlConfig().blockMidAir());
			ps.setBoolean(9, value.getPearlConfig().consumeOnBlock());
			ps.setDouble(10, value.getPearlConfig().getDamage());
			ps.setString(11, value.getName());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public BastionBlueprint get(String key) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_BASTION_BLUEPRINT);
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			BastionBlueprint bb = BastionBlueprint.builder()
					.name(key)
					.displayName(rs.getString("DisplayName"))
					.itemStack(new ItemStack(Material.valueOf(rs.getString("ItemStackMaterial")), rs.getInt("ItemStackAmount")))
					.radius(rs.getInt("Radius"))
					.requiresMaturity(rs.getBoolean("RequiresMaturity"))
					.pearlConfig(PearlConfig.builder()
								.block(rs.getBoolean("PearlBlock"))
								.blockMidAir(rs.getBoolean("PearlBlockMidAir"))
								.consumeOnBlock(rs.getBoolean("PearlConsumeOnBlock"))
								.damage(rs.getDouble("PearlDamage"))
								.build()
							)
					.build();
			ps.close();
			return bb;
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
	public Set<BastionBlueprint> getAll() {
		Set<BastionBlueprint> all = new HashSet<>();
		return all;
	}

	@Override
	public void delete(BastionBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(DELETE_BASTION_BLUEPRINT);
			ps.setString(1, value.getName());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}

