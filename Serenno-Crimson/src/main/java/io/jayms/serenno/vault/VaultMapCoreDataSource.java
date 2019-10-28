package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
			+ "TeamColor, ReinforcementID"
			+ ") VALUES("
			+ "?, ?"
			+ ")";
	
	private static final String UPDATE_CORE = "UPDATE CORE "
			+ "SET TeamColor = ? "
			+ "WHERE ReinforcementID = ?";
	
	private static final String SELECT_CORE = "SELECT TeamColor FROM CORE WHERE ReinforcementID = ?";
	private static final String SELECT_CORE_ALL = "SELECT * FROM CORE";
	
	private static final String DELETE_CORE = "DELETE FROM CORE WHERE ReinforcementID = ?";
	
	private VaultMapDatabase db;
	
	private Map<ChatColor, Core> cores = new HashMap<>();
	
	public VaultMapCoreDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(CREATE_CORE);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		cores.put(value.getTeamColor(), value);
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
		ChatColor teamColor = db.getTeamColourFromGroupName(rein.getGroup().getName().toLowerCase());
		if (teamColor != null) {
			if (cores.containsKey(teamColor)) {
				return cores.get(teamColor);
			}
		}
		
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_CORE);
			ps.setString(1, rein.getID().toString());
			
			ResultSet rs = ps.executeQuery();
			
			if (!rs.next()) {
				return null;
			}
			
			Core core = new Core(db, ChatColor.valueOf(rs.getString("TeamColor")), rein);
			ps.close();
			
			cores.put(core.getTeamColor(), core);
			return core;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Collection<Core> getAll() {
		if (!cores.isEmpty()) {
			return cores.values();
		}
		
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_CORE_ALL);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				Core core = new Core(db, ChatColor.valueOf(rs.getString("TeamColor")),
						db.getReinforcementSource().get(UUID.fromString(rs.getString("ReinforcementID"))));
				cores.put(core.getTeamColor(), core);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return cores.values();
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
		cores.remove(value.getTeamColor());
	}

	
	
}
