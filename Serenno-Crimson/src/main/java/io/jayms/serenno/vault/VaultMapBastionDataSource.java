package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionDataSource;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;

public class VaultMapBastionDataSource implements BastionDataSource {

	private static final String CREATE_BASTION = "CREATE TABLE IF NOT EXISTS BASTION("
			+ "ReinforcementID TEXT PRIMARY KEY, "
			+ "Blueprint TEXT"
			+ ")";
	
	private static final String INSERT_BASTION = "INSERT INTO BASTION"
			+ "("
			+ "ReinforcementID, Blueprint"
			+ ") VALUES("
			+ "?, ?"
			+ ")";
	
	private static final String SELECT_BASTION = "SELECT Blueprint FROM BASTION WHERE ReinforcementID = ?";
	
	private static final String SELECT_ALL_BASTION = "SELECT * FROM BASTION";
	
	private static final String DELETE_BASTION = "DELETE FROM BASTION WHERE ReinforcementID = ?";
	
	private VaultMapDatabase db;
	
	public VaultMapBastionDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(CREATE_BASTION);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void create(Bastion value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(INSERT_BASTION);
			ps.setString(1, value.getReinforcement().getID().toString());
			ps.setString(2, value.getBlueprint().getName());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Bastion value) {
		throw new UnsupportedOperationException("Vault Map Bastion doesn't support update operation.");
	}
	
	@Override
	public boolean exists(Reinforcement rein) {
		return get(rein) != null;
	}

	@Override
	public Bastion get(Reinforcement rein) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_BASTION);
			ps.setString(1, rein.getID().toString());
			ResultSet rs = ps.executeQuery();

			Bastion bastion = new Bastion(rein, db.getBastionBlueprintSource().get(rs.getString("Blueprint")));
			ps.close();
			return bastion;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Set<Bastion> getAll() {
		Set<Bastion> all = new HashSet<>();
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_ALL_BASTION);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Bastion bastion = new Bastion(db.getReinforcementSource().get(UUID.fromString(rs.getString("ReinforcementID"))), db.getBastionBlueprintSource().get(rs.getString("Blueprint")));
				all.add(bastion);
			}
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return all;
	}

	@Override
	public void delete(Bastion value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(DELETE_BASTION);
			ps.setString(1, value.getReinforcement().getID().toString());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	
}
