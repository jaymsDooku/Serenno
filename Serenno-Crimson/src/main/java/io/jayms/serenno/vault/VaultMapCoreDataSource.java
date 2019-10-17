package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.util.SerennoDataSource;
import net.md_5.bungee.api.ChatColor;

public class VaultMapCoreDataSource implements SerennoDataSource<Core, Reinforcement> {

	private static final String CREATE_CORE = "CREATE TABLE IF NOT EXISTS CORE("
			+ "ReinforcementID TEXT PRIMARY KEY, "
			+ "TeamColor TEXT"
			+ ");";
	
	private static final String INSERT_CORE = "INSERT INTO CORE"
			+ "("
			+ "ReinforcementID, TeamColor"
			+ ") VALUES("
			+ "?, ?, ?, ?"
			+ ")";
	
	private static final String UPDATE_CORE = "UPDATE CORE "
			+ "SET TeamColor = ? "
			+ "WHERE ReinforcementID = ?";
	
	private static final String SELECT_CORE = "SELECT TeamColor FROM CORE WHERE ReinforcementID = ?";
	
	private static final String DELETE_CORE = "DELETE FROM CORE WHERE ReinforcementID = ?";
	
	private VaultMapDatabase db;
	
	public VaultMapCoreDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		db.getDatabase().modifyQuery(CREATE_CORE, true);
	}
	
	@Override
	public void create(Core value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(INSERT_CORE);
			ps.setString(1, value.getTeamColor().name());
			ps.setString(2, value.getReinforcement().getID().toString());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Core value) {
		throw new UnsupportedOperationException("Vault Map Bastion doesn't support update operation.");
	}
	
	@Override
	public boolean exists(Reinforcement rein) {
		return get(rein) != null;
	}

	@Override
	public Core get(Reinforcement rein) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_CORE);
			ps.setString(1, rein.getID().toString());
			
			ResultSet rs = ps.executeQuery();
			ps.close();
			
			Core core = new Core(db, ChatColor.valueOf(rs.getString("TeamColor")), rein);
			return core;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Set<Core> getAll() {
		Set<Core> all = new HashSet<>();
		/*try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_ALL_BASTION);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				
				VaultMapBastion bastion = new VaultMapBastion();
				all.add(bastion);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}*/
		return all;
	}

	@Override
	public void delete(Core value) {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(DELETE_CORE);
			ps.setString(1, value.getReinforcement().getID().toString());
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	
	
}
