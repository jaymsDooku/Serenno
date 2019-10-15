package io.jayms.serenno.game;

import org.bukkit.Location;
import org.bukkit.World;

import io.jayms.serenno.vault.VaultMap;
import net.md_5.bungee.api.ChatColor;

public class VaultBattle extends SimpleDuel {

	private World activeWorld;
	
	public VaultBattle(int id, VaultMap map, DuelTeam team1, DuelTeam team2) {
		super(id, map, DuelType.VAULTBATTLE, team1, team2);
	}
	
	@Override
	protected void initGame() {
		VaultMap vaultMap = getVaultMap();
		activeWorld = vaultMap.activateWorld();
		
		super.initGame();
	}
	
	public VaultMap getVaultMap() {
		return (VaultMap) getMap();
	}

	@Override
	public Location getSpawnPoint(ChatColor teamColor) {
		Location spawnPoint = super.getSpawnPoint(teamColor);
		spawnPoint.setWorld(activeWorld);
		return spawnPoint;
	}
	
}
