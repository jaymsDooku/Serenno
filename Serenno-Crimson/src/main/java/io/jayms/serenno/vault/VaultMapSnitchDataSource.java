package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.snitch.Snitch;
import io.jayms.serenno.model.citadel.snitch.SnitchDataSource;

public class VaultMapSnitchDataSource implements SnitchDataSource {

	private static final String CREATE_SNITCH = "CREATE TABLE IF NOT EXISTS SNITCH("
			+ "ReinforcementID TEXT PRIMARY KEY, "
			+ "Name TEXT, "
			+ "Radius INTEGER"
			+ ")";
	
	private static final String INSERT_SNITCH = "INSERT INTO SNITCH"
			+ "("
			+ "ReinforcementID, Name, Radius"
			+ ") VALUES("
			+ "?, ?"
			+ ")";
	
	private static final String SELECT_SNITCH = "SELECT Name, Radius FROM SNITCH WHERE ReinforcementID = ?";
	
	private static final String SELECT_ALL_SNITCH = "SELECT * FROM SNITCH";
	
	private static final String DELETE_SNITCH = "DELETE FROM SNITCH WHERE ReinforcementID = ?";
	
	private VaultMapDatabase db;
	
	public VaultMapSnitchDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(CREATE_SNITCH);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void create(Snitch value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(INSERT_SNITCH);
			ps.setString(1, value.getReinforcement().getID().toString());
			ps.setString(2, value.getName());
			ps.setInt(3, value.getRadius());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Snitch value) {
		throw new UnsupportedOperationException("Vault Map Bastion doesn't support update operation.");
	}
	
	@Override
	public boolean exists(Reinforcement rein) {
		return get(rein) != null;
	}

	@Override
	public Snitch get(Reinforcement rein) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_SNITCH);
			ps.setString(1, rein.getID().toString());
			ResultSet rs = ps.executeQuery();
			
			Snitch snitch = new Snitch(rein, rs.getString("Name"), rs.getInt("Radius"));
			ps.close();
			return snitch;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<Snitch> getAll() {
		Set<Snitch> all = new HashSet<>();
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_ALL_SNITCH);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Snitch bastion = new Snitch(db.getReinforcementSource().get(UUID.fromString(rs.getString("ReinforcementID"))), rs.getString("Name"), rs.getInt("Radius"));
				all.add(bastion);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return all;
	}

	@Override
	public void delete(Snitch value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(DELETE_SNITCH);
			ps.setString(1, value.getReinforcement().getID().toString());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	
}
