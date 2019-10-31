package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld.UnloadCallback;
import io.jayms.serenno.util.ChunkCoord;
import io.jayms.serenno.util.Coords;

public class VaultMapReinforcementDataSource implements ReinforcementDataSource {

	private static final String CREATE_REIN = "CREATE TABLE IF NOT EXISTS REINFORCEMENT"
			+ "("
			+ "ReinforcementID TEXT PRIMARY KEY, "
			+ "X INTEGEER, "
			+ "Y INTEGER, "
			+ "Z INTEGER, "
			+ "CX INTEGER, "
			+ "CZ INTEGER, "
			+ "Blueprint TEXT, "
			+ "CitadelGroup TEXT, "
			+ "CreationTime INTEGER, "
			+ "Health REAL"
			+ ")";
	
	private static final String INSERT_REIN = "INSERT INTO REINFORCEMENT"
			+ "("
			+ "ReinforcementID, X, Y, Z, CX, CZ, Blueprint, CitadelGroup, CreationTime, Health"
			+ ") VALUES("
			+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?"
			+ ")";
	
	private static final String UPDATE_REIN = "UPDATE REINFORCEMENT SET "
			+ "Health = ? "
			+ "WHERE ReinforcementID = ?";
	
	private static final String SELECT_ALL_REIN = "SELECT * FROM REINFORCEMENT";
	
	private static final String SELECT_REIN_BY_ID = "SELECT X, Y, Z, Blueprint, CitadelGroup, CreationTime, Health FROM REINFORCEMENT WHERE ReinforcementID = ?";
	
	private static final String SELECT_REIN = "SELECT ReinforcementID, Blueprint, CitadelGroup, CreationTime, Health FROM REINFORCEMENT WHERE X = ? AND Y = ? AND Z = ?";
	
	private static final String SELECT_REIN_BY_CHUNK = "SELECT ReinforcementID, X, Y, Z, Blueprint, CitadelGroup, CreationTime, Health FROM REINFORCEMENT WHERE CX = ? AND CZ = ?";
	
	private static final String DELETE_REIN = "DELETE FROM REINFORCEMENT WHERE ReinforcementID = ?";
	
	private VaultMapDatabase db;
	
	public VaultMapReinforcementDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(CREATE_REIN);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void create(Reinforcement value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(INSERT_REIN);
			ps.setString(1, value.getID().toString());
			ps.setInt(2, value.getLocation().getBlockX());
			ps.setInt(3, value.getLocation().getBlockY());
			ps.setInt(4, value.getLocation().getBlockZ());
			ps.setInt(5, value.getLocation().getChunk().getX());
			ps.setInt(6, value.getLocation().getChunk().getZ());
			ps.setString(7, value.getBlueprint().getName());
			ps.setString(8, value.getGroup().getName());
			ps.setLong(9, value.getCreationTime());
			ps.setDouble(10, value.getHealth());
			ps.executeUpdate();
			ps.close();
			
			value.setInMemory(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void persistAll(Collection<Reinforcement> reinforcements, UnloadCallback callback) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					if (db.getDatabase().getConnection().isClosed()) {
						return;
					}
			
					PreparedStatement insertPs = db.getDatabase().getConnection().prepareStatement(INSERT_REIN);
					PreparedStatement updatePs = db.getDatabase().getConnection().prepareStatement(UPDATE_REIN);
					PreparedStatement deletePs = db.getDatabase().getConnection().prepareStatement(DELETE_REIN);
					for (Reinforcement reinforcement : reinforcements) {
						if (!reinforcement.isDirty()) {
							continue;
						}
						if (reinforcement.isBroken()) {
							deleteReinforcement(deletePs, reinforcement);
						} else {
							if (reinforcement.isInMemory()) {
								insertReinforcement(insertPs, reinforcement);
								reinforcement.setInMemory(false);
							} else {
								updateReinforcement(updatePs, reinforcement);
							}
						}
						reinforcement.setDirty(false);
					}
					deletePs.executeBatch();
					insertPs.executeBatch();
					updatePs.executeBatch();
					
					insertPs.close();
					updatePs.close();
					deletePs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				new BukkitRunnable() {
					
					@Override
					public void run() {
						callback.unload();
					}
					
				}.runTask(SerennoCobalt.get());
			}
		}.runTaskAsynchronously(SerennoCobalt.get());
	}
	
	private void insertReinforcement(PreparedStatement ps, Reinforcement value) throws SQLException {
		ps.setString(1, value.getID().toString());
		ps.setInt(2, value.getLocation().getBlockX());
		ps.setInt(3, value.getLocation().getBlockY());
		ps.setInt(4, value.getLocation().getBlockZ());
		ps.setInt(5, value.getLocation().getChunk().getX());
		ps.setInt(6, value.getLocation().getChunk().getZ());
		ps.setString(7, value.getBlueprint().getName());
		ps.setString(8, value.getGroup().getName());
		ps.setLong(9, value.getCreationTime());
		ps.setDouble(10, value.getHealth());
		ps.addBatch();
	}
	
	private void updateReinforcement(PreparedStatement ps, Reinforcement value) throws SQLException {
		ps.setDouble(1, value.getHealth());
		ps.setString(2, value.getID().toString());
		ps.addBatch();
	}

	private void deleteReinforcement(PreparedStatement ps, Reinforcement value) throws SQLException {
		ps.setString(1, value.getID().toString());
		ps.addBatch();
	}

	@Override
	public void update(Reinforcement value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(UPDATE_REIN);
			ps.setDouble(1, value.getHealth());
			ps.setString(2, value.getID().toString());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Reinforcement get(UUID id) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_REIN_BY_ID);
			ps.setString(1, id.toString());
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			String groupName = rs.getString("CitadelGroup");
			String blueprint = rs.getString("Blueprint");
			
			int x = rs.getInt("X");
			int y = rs.getInt("Y");
			int z = rs.getInt("Z");
			
			Reinforcement rein = Reinforcement.builder()
					.id(id)
					.loc(new Location(db.getWorld(), x, y, z))
					.creationTime(rs.getLong("CreationTime"))
					.group(db.getGroupSource().get(groupName.toLowerCase()))
					.blueprint(db.getReinforcementBlueprintSource().get(blueprint))
					.health(rs.getDouble("Health"))
					.inMemory(false)
					.build();
			
			ps.close();
			return rein;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public Reinforcement get(Coords key) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_REIN);
			ps.setInt(1, key.getX());
			ps.setInt(2, key.getY());
			ps.setInt(3, key.getZ());
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			String groupName = rs.getString("CitadelGroup");
			String blueprint = rs.getString("Blueprint");
			
			UUID id = UUID.fromString(rs.getString("ReinforcementID"));
			
			Reinforcement rein = Reinforcement.builder()
					.id(id)
					.loc(new Location(db.getWorld(), key.getX(), key.getY(), key.getZ()))
					.creationTime(rs.getLong("CreationTime"))
					.group(db.getGroupSource().get(groupName.toLowerCase()))
					.blueprint(db.getReinforcementBlueprintSource().get(blueprint))
					.health(rs.getDouble("Health"))
					.inMemory(false)
					.build();
			
			ps.close();
			return rein;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean exists(Coords key) {
		return get(key) != null;
	}
	
	@Override
	public Map<Coords, Reinforcement> getAll(ChunkCoord coord) {
		Map<Coords, Reinforcement> all = new HashMap<>();
		
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_REIN_BY_CHUNK);
			ps.setInt(1, coord.getX());
			ps.setInt(2, coord.getZ());
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				String groupName = rs.getString("CitadelGroup");
				String blueprint = rs.getString("Blueprint");
				
				UUID id = UUID.fromString(rs.getString("ReinforcementID"));
				Location loc = new Location(db.getWorld(), rs.getInt("X"), rs.getInt("Y"), rs.getInt("Z"));
				
				Reinforcement rein = Reinforcement.builder()
						.id(id)
						.loc(loc)
						.creationTime(rs.getLong("CreationTime"))
						.group(db.getGroupSource().get(groupName.toLowerCase()))
						.blueprint(db.getReinforcementBlueprintSource().get(blueprint))
						.health(rs.getDouble("Health"))
						.inMemory(false)
						.build();
				
				all.put(Coords.fromLocation(loc), rein);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return all;
	}

	@Override
	public Set<Reinforcement> getAll() {
		Set<Reinforcement> all = new HashSet<>();
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_ALL_REIN);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String groupName = rs.getString("CitadelGroup");
				String blueprint = rs.getString("Blueprint");
				
				UUID id = UUID.fromString(rs.getString("ReinforcementID"));
				
				Reinforcement rein = Reinforcement.builder()
						.id(id)
						.loc(new Location(db.getWorld(), rs.getInt("X"), rs.getInt("Y"), rs.getInt("Z")))
						.creationTime(rs.getLong("CreationTime"))
						.group(db.getGroupSource().get(groupName.toLowerCase()))
						.blueprint(db.getReinforcementBlueprintSource().get(blueprint))
						.health(rs.getDouble("Health"))
						.inMemory(false)
						.build();
				
				all.add(rein);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return all;
	}

	@Override
	public void delete(Reinforcement value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(DELETE_REIN);
			ps.setString(1, value.getID().toString());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isAcidBlock(Material type) {
		return type == Material.GOLD_BLOCK;
	}
	
}
