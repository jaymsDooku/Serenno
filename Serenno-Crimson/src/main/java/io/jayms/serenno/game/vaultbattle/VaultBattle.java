package io.jayms.serenno.game.vaultbattle;

import org.bukkit.Location;
import org.bukkit.World;

import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.game.SimpleDuel;
import io.jayms.serenno.vault.VaultMap;
import io.jayms.serenno.vault.VaultMapDatabase;
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
	
	@Override
	public void broadcast(String message) {
		super.broadcast(ChatColor.BLACK + "[" + ChatColor.DARK_RED + "VaultBattle" + ChatColor.BLACK + "]: " + ChatColor.RESET + message);
	}
	
	@Override
	protected void disposeGame() {
		super.disposeGame();
		
		VaultMap vaultMap = getVaultMap();
		VaultMapDatabase vaultDatabase = vaultMap.getDatabase(activeWorld);
		vaultDatabase.delete();
		vaultMap.deactivateWorld(activeWorld);
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
