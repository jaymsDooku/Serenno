package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementKey;

public class VaultMapReinforcementDataSource implements ReinforcementDataSource {

	private static final String CREATE_REIN = "CREATE TABLE IF NOT EXISTS REINFORCEMENT"
			+ "("
			+ "ReinforcementID TEXT PRIMARY KEY, "
			+ "World TEXT, "
			+ "X INTEGEER, "
			+ "Y INTEGER, "
			+ "Z INTEGER, "
			+ "Blueprint TEXT, "
			+ "CitadelGroup TEXT, "
			+ "CreationTime INTEGER, "
			+ "Health REAL"
			+ ")";
	
	private static final String INSERT_REIN = "INSERT INTO REINFORCEMENT"
			+ "("
			+ "ReinforcementID, World, X, Y, Z, Blueprint, CitadelGroup, CreationTime, Health"
			+ ") VALUES("
			+ "?, ?, ?, ?, ?, ?, ?, ?, ?"
			+ ")";
	
	private static final String UPDATE_REIN = "UPDATE REINFORCEMENT SET "
			+ "Health = ? "
			+ "WHERE ReinforcementID = ?";
	
	private static final String SELECT_ALL_REIN = "SELECT * FROM REINFORCEMENT";
	
	private static final String SELECT_REIN = "SELECT ReinforcementID, World, Blueprint, CitadelGroup, CreationTime, Health FROM REINFORCEMENT WHERE X = ? AND Y = ? AND Z = ?";
	
	private static final String DELETE_REIN = "DELETE FROM REINFORCEMENT WHERE ReinforcementID = ?";
	
	private VaultMapDatabase db;
	
	public VaultMapReinforcementDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		db.getDatabase().modifyQuery(CREATE_REIN, true);
	}

	@Override
	public void create(Reinforcement value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(INSERT_REIN);
			ps.setString(1, value.getID().toString());
			ps.setString(2, value.getLocation().getWorld().getName());
			ps.setDouble(3, value.getLocation().getX());
			ps.setDouble(4, value.getLocation().getY());
			ps.setDouble(5, value.getLocation().getZ());
			ps.setString(6, value.getBlueprint().getName());
			ps.setString(7, value.getGroup().getName());
			ps.setLong(8, value.getCreationTime());
			ps.setDouble(9, value.getHealth());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
	
	@Override
	public Reinforcement get(ReinforcementKey key) {
		try {
			int x = key.getCoords().getX();
			int y = key.getCoords().getY();
			int z = key.getCoords().getZ();
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_REIN);
			ps.setInt(1, x);
			ps.setInt(2, y);
			ps.setInt(3, z);
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			String worldName = rs.getString("World");
			World world = Bukkit.getWorld(worldName);
			if (world == null) {
				new IllegalStateException("World for that reinforcement doesn't exist anymore.");
			}
			
			String groupName = rs.getString("CitadelGroup");
			String blueprint = rs.getString("Blueprint");
			
			UUID id = UUID.fromString(rs.getString("ReinforcementID"));
			
			Reinforcement rein = Reinforcement.builder()
					.id(id)
					.loc(new Location(world, x, y, z))
					.creationTime(rs.getLong("CreationTime"))
					.group(db.getGroupSource().get(groupName))
					.blueprint(db.getReinforcementBlueprintSource().get(blueprint))
					.health(rs.getDouble("Health"))
					.build();
			
			ps.close();
			return rein;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean exists(ReinforcementKey key) {
		return get(key) != null;
	}

	@Override
	public Set<Reinforcement> getAll() {
		Set<Reinforcement> all = new HashSet<>();
		/*try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_ALL_REIN);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String worldName = rs.getString("World");
				World world = Bukkit.getWorld(worldName);
				if (world == null) {
					new IllegalStateException("World for that reinforcement doesn't exist anymore.");
				}
				
				int x = rs.getInt("X");
				int y = rs.getInt("Y");
				int z = rs.getInt("Z");
				
				VaultMapReinforcement rein = new VaultMapReinforcement(UUID.fromString(rs.getString("ReinforcementID")), rs.getString("CitadelGroup"), rs.getString("Blueprint"),
						new Location(world, x, y, z));
				all.add(rein);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
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

	
	
}
