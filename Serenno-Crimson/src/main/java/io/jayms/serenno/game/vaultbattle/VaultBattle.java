package io.jayms.serenno.game.vaultbattle;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.DuelTeam;
import io.jayms.serenno.game.DuelType;
import io.jayms.serenno.game.SimpleDuel;
import io.jayms.serenno.game.vaultbattle.pearling.SpectatorPearlManager;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.vault.VaultMap;
import io.jayms.serenno.vault.VaultMapDatabase;
import net.md_5.bungee.api.ChatColor;

public class VaultBattle extends SimpleDuel {

	private SpectatorPearlManager pearlManager;
	private World activeWorld;
	
	public VaultBattle(int id, VaultMap map, DuelTeam team1, DuelTeam team2) {
		super(id, map, DuelType.VAULTBATTLE, team1, team2);
		pearlManager = new SpectatorPearlManager(this);
	}
	
	public SpectatorPearlManager getPearlManager() {
		return pearlManager;
	}
	
	public VaultMapDatabase getDatabase() {
		VaultMap vaultMap = getVaultMap();
		return vaultMap.getDatabase(activeWorld); 
	}
	
	@Override
	protected void initGame() {
		VaultMap vaultMap = getVaultMap();
		activeWorld = vaultMap.activateWorld();
		
		VaultMapDatabase database = vaultMap.getDatabase(activeWorld);
		database.setBattle(this);
		List<SerennoPlayer> playing = getPlaying();
		for (SerennoPlayer player : playing) {
			if (!database.isAllowed(player.getBukkitPlayer())) {
				broadcast(ChatColor.DARK_RED + player.getName() + ChatColor.RED + " isn't allowed to play on this vault map - game cancelled.");
				return;
			}
		}
		
		addToGroup(database, getTeam1());
		addToGroup(database, getTeam2());
		
		super.initGame();
	}
	
	private void addToGroup(VaultMapDatabase database, DuelTeam team) {
		ChatColor teamColour = team.getTeamColor();
		System.out.println("teamColour: " + teamColour);
		String groupName = database.getGroupNameFromColour(team.getTeamColor());
		System.out.println("groupName: " + groupName);
		Group group = database.getGroupSource().get(groupName.toLowerCase());
		for (SerennoPlayer player : team.getAlive()) {
			player.sendMessage(ChatColor.YELLOW + "You are fighting for the " + team.getTeamColor() + group.getName());
			group.addMember(player.getBukkitPlayer());
		}
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
		new BukkitRunnable() {
			
			@Override
			public void run() {
				vaultMap.deactivateWorld(activeWorld);
			}
			
		}.runTaskLater(SerennoCrimson.get(), 20L);
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
	
	@Override
	public void die(SerennoPlayer deadPlayer) {
		super.die(deadPlayer);
		
		pearlManager.pearl(deadPlayer);
	}
	
}
