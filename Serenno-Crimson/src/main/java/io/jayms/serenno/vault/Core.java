package io.jayms.serenno.vault;

import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import net.md_5.bungee.api.ChatColor;

public class Core {

	private VaultMapDatabase vaultMapDatabase;
	private Reinforcement reinforcement;
	private ChatColor teamColor;
	
	public Core(VaultMapDatabase vaultMapDatabase, ChatColor teamColor, Reinforcement reinforcement) {
		this.vaultMapDatabase = vaultMapDatabase;
		this.reinforcement = reinforcement;
		this.teamColor = teamColor;
	}
	
	public VaultMapDatabase getVaultMapDatabase() {
		return vaultMapDatabase;
	}
	
	public Reinforcement getReinforcement() {
		return reinforcement;
	}
	
	public ChatColor getTeamColor() {
		return teamColor;
	}
	
}
