package io.jayms.serenno.vault;

import io.jayms.serenno.game.vaultbattle.VaultBattle;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import net.md_5.bungee.api.ChatColor;

import java.util.UUID;

public class Core {

	private UUID reinforcementID;
	private ChatColor teamColor;
	private VaultBattle battle;

	public Core(ChatColor teamColor, Reinforcement reinforcement) {
		this(teamColor, reinforcement.getID());
	}

	public Core(ChatColor teamColor, UUID reinforcementID) {
		this.reinforcementID = reinforcementID;
		this.teamColor = teamColor;
	}

	public UUID getReinforcementID() {
		return reinforcementID;
	}

	public void setBattle(VaultBattle battle) {
		this.battle = battle;
	}

	public VaultBattle getBattle() {
		return battle;
	}

	public Reinforcement getReinforcement(ReinforcementWorld world) {
		return world.getReinforcement(reinforcementID);
	}
	
	public ChatColor getTeamColor() {
		return teamColor;
	}
	
	@Override
	public String toString() {
		return "Core[teamColor=" + teamColor.name() + ", reinforcement=" + reinforcementID + "]";
	}
	
}
