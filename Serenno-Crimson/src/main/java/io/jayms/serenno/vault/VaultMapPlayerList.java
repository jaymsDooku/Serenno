package io.jayms.serenno.vault;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

public class VaultMapPlayerList {

	private static final String CREATE_PLAYER = "CREATE TABLE IF NOT EXISTS PLAYER("
			+ "PlayerID TEXT PRIMARY KEY"
			+ ");";
	
	private static final String INSERT_PLAYER = "INSERT INTO PLAYER"
			+ "("
			+ "PlayerID"
			+ ") VALUES("
			+ "?"
			+ ")";
	
	private static final String SELECT_ALL = "SELECT * FROM PLAYER";
	
	private static final String DELETE_PLAYER = "DELETE FROM PLAYER WHERE PlayerID = ?";
	
	private VaultMapDatabase db;
	
	private Set<UUID> playerList;
	
	public VaultMapPlayerList(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void add(Player player) {
		add(player.getUniqueId());
	}
	
	public void add(UUID id) {
		if (inPlayerList(id)) {
			return;
		}
		
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(INSERT_PLAYER);
			ps.setString(1, id.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		getPlayerList().add(id);
	}
	
	public void remove(Player player) {
		remove(player.getUniqueId());
	}
	
	public void remove(UUID id) {
		if (!inPlayerList(id)) {
			return;
		}
		
		try {
			PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(DELETE_PLAYER);
			ps.setString(1, id.toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		getPlayerList().remove(id);
	}

	public boolean inPlayerList(Player player) {
		return getPlayerList().contains(player.getUniqueId());
	}
	
	public boolean inPlayerList(UUID id) {
		return getPlayerList().contains(id);
	}
	
	public Set<UUID> getPlayerList() {
		if (playerList == null) {
			playerList = new HashSet<>();
			
			try {
				PreparedStatement ps = db.getDatabase().getConnection().prepareStatement(SELECT_ALL);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					playerList.add(UUID.fromString(rs.getString("PlayerID")));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return playerList;
	}
	
}
