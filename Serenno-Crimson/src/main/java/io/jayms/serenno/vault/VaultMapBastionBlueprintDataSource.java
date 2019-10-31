package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.kit.ItemStackKey;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint.PearlConfig;
import io.jayms.serenno.model.citadel.bastion.BastionShape;
import io.jayms.serenno.util.ItemUtil;
import io.jayms.serenno.util.SerennoDataSource;

public class VaultMapBastionBlueprintDataSource implements SerennoDataSource<BastionBlueprint, ItemStack> {

	private static final String CREATE_BASTION_BLUEPRINT = "CREATE TABLE IF NOT EXISTS BASTION_BLUEPRINT("
			+ "Name TEXT PRIMARY KEY,"
			+ "DisplayName TEXT UNIQUE, "
			+ "ItemStackMaterial TEXT UNIQUE, "
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
			+ "BastionShape = ?, "
			+ "Radius = ?, "
			+ "RequiresMaturity = ?, "
			+ "PearlBlock = ?, "
			+ "PearlBlockMidAir = ?, "
			+ "PearlConsumeOnBlock = ?, "
			+ "PearlDamage = ? "
			+ "WHERE ItemStackMaterial = ? AND ItemStackAmount = ?";
	
	private static final String SELECT_BASTION_NAME_BLUEPRINT = "SELECT DisplayName, ItemStackMaterial, ItemStackAmount, BastionShape, Radius, RequiresMaturity, PearlBlock, PearlBlockMidAir, PearlConsumeOnBlock, PearlDamage "
			+ "FROM BASTION_BLUEPRINT WHERE Name = ?";
	
	private static final String SELECT_BASTION_BLUEPRINT = "SELECT Name, ItemStackMaterial, ItemStackAmount, BastionShape, Radius, RequiresMaturity, PearlBlock, PearlBlockMidAir, PearlConsumeOnBlock, PearlDamage "
			+ "FROM BASTION_BLUEPRINT WHERE ItemStackMaterial = ? AND ItemStackAmount = ? AND DisplayName = ?";
	
	private static final String SELECT_ALL_BASTION_BLUEPRINT = "SELECT * FROM BASTION_BLUEPRINT";
	
	private static final String DELETE_BASTION_BLUEPRINT = "DELETE FROM BASTION_BLUEPRINT WHERE Name = ?";
	
	private VaultMapDatabase db;
	private Map<ItemStackKey, BastionBlueprint> blueprints = new HashMap<>();
	private Map<String, BastionBlueprint> namedBlueprints = new HashMap<>();
	
	public VaultMapBastionBlueprintDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(CREATE_BASTION_BLUEPRINT);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void cache(BastionBlueprint value) {
		blueprints.put(new ItemStackKey(value.getItemStack()), value);
		namedBlueprints.put(value.getName(), value);
	}
	
	@Override
	public void create(BastionBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(INSERT_BASTION_BLUEPRINT);
			ps.setString(1, value.getName());
			ps.setString(2, value.getDisplayName());
			ps.setString(3, value.getItemStack().getType().toString());
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
		cache(value);
	}

	@Override
	public void update(BastionBlueprint value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(UPDATE_BASTION_BLUEPRINT);
			ps.setString(1, value.getDisplayName());
			ps.setString(2, value.getItemStack().getType().toString());
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
		cache(value);
	}
	
	public BastionBlueprint get(String name) {
		if (namedBlueprints.containsKey(name)) {
			return namedBlueprints.get(name);
		}
		
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_BASTION_NAME_BLUEPRINT);
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			String displayName = rs.getString("DisplayName");
			
			BastionBlueprint bb = BastionBlueprint.builder()
					.name(name)
					.displayName(displayName)
					.itemStack(new ItemStackBuilder(Material.valueOf(rs.getString("ItemStackMaterial")), rs.getInt("ItemStackAmount"))
							.meta(new ItemMetaBuilder()
									.name(displayName))
							.build())
					.shape(BastionShape.valueOf(rs.getString("BastionShape")))
					.radius(rs.getInt("Radius"))
					.requiresMaturity(rs.getBoolean("RequiresMaturity"))
					.pearlConfig(PearlConfig.builder()
								.block(rs.getBoolean("PearlBlock"))
								.blockMidAir(rs.getBoolean("PearlBlockMidAir"))
								.consumeOnBlock(rs.getBoolean("PearlConsumeOnBlock"))
								.damage(rs.getDouble("PearlDamage"))
								.build())
					.build();
			ps.close();
			
			cache(bb);
			
			return bb;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public BastionBlueprint get(ItemStack key) {
		ItemStackKey itemStackKey = new ItemStackKey(key);
		if (blueprints.containsKey(itemStackKey)) {
			return blueprints.get(itemStackKey);
		}
		
		try {
			String displayName = ItemUtil.getName(key);
			
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_BASTION_BLUEPRINT);
			ps.setString(1, key.getType().toString());
			ps.setInt(2, key.getAmount());
			ps.setString(3, displayName);
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			BastionBlueprint bb = BastionBlueprint.builder()
					.name(rs.getString("Name"))
					.displayName(displayName)
					.itemStack(key)
					.shape(BastionShape.valueOf(rs.getString("BastionShape")))
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

			cache(bb);
			
			return bb;
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
	public Collection<BastionBlueprint> getAll() {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_ALL_BASTION_BLUEPRINT);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				String displayName = rs.getString("DisplayName");
				
				BastionBlueprint bb = BastionBlueprint.builder()
						.name(rs.getString("Name"))
						.displayName(displayName)
						.itemStack(new ItemStackBuilder(Material.valueOf(rs.getString("ItemStackMaterial")), rs.getInt("ItemStackAmount"))
								.meta(new ItemMetaBuilder()
										.name(displayName))
								.build())
						.shape(BastionShape.valueOf(rs.getString("BastionShape")))
						.radius(rs.getInt("Radius"))
						.requiresMaturity(rs.getBoolean("RequiresMaturity"))
						.pearlConfig(PearlConfig.builder()
									.block(rs.getBoolean("PearlBlock"))
									.blockMidAir(rs.getBoolean("PearlBlockMidAir"))
									.consumeOnBlock(rs.getBoolean("PearlConsumeOnBlock"))
									.damage(rs.getDouble("PearlDamage"))
									.build())
						.build();
				cache(bb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return blueprints.values();
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
		blueprints.remove(new ItemStackKey(value.getItemStack()));
		namedBlueprints.remove(value.getName());
	}
	
	@Override
	public void deleteAll() {
		List<BastionBlueprint> blueprints = Lists.newArrayList(getAll());
		for (BastionBlueprint bb : blueprints) {
			delete(bb);
		}
		blueprints.clear();
	}

}

