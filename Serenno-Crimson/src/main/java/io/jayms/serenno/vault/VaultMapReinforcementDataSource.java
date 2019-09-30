package io.jayms.serenno.vault;

import java.util.Set;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementDataSource;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementKey;

public class VaultMapReinforcementDataSource implements ReinforcementDataSource {

	private static final String CREATE_REINFORCEMENT = "CREATE TABLE IF NOT EXISTS REINFORCEMENT"
			+ "("
			+ "ReinforcementID TEXT PRIMARY KEY AUTOINCREMENT, "
			+ "Blueprint TEXT, "
			+ "Group TEXT, "
			+ "X REAL, "
			+ "Y REAL, "
			+ "Z REAL"
			+ ")";
	
	private static final String INSERT_REINFORCEMENT = "INSERT INTO REINFORCEMENT"
			+ "("
			+ "ReinforcementID, Blueprint, Group, X, Y, Z"
			+ ") VALUES ("
			+ "?, ?, ?, ?, ?, ?"
			+ ")";
	
	private static final String UPDATE_REINFORCEMENT = "UPDATE REINFORCEMENT "
			+ "SET ReinforcementID = ?, "
			+ "Blueprint = ?, "
			+ "Group = ?, "
			+ "X = ?, "
			+ "Y = ?, "
			+ "Z = ? "
			+ "WHERE ReinforcementID = ?";
	
	private static final String DELETE_REINFORCEMENT = "DELETE FROM REINFORCEMENT"
			+ "WHERE ReinforcementID = ?";
	
	private VaultMapDatabase db;
	
	public VaultMapReinforcementDataSource(VaultMapDatabase db) {
		this.db = db;
	}
	
	public void createTables() {
		
	}
	
	@Override
	public void create(Reinforcement value) {
		
	}

	@Override
	public void update(Reinforcement value) {
		
	}

	@Override
	public Reinforcement get(ReinforcementKey key) {
		return null;
	}

	@Override
	public Set<Reinforcement> getAll() {
		return null;
	}

	@Override
	public void delete(Reinforcement value) {
		
	}

	
	
}
