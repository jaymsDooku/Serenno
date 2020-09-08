package io.jayms.serenno.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.jayms.serenno.game.menu.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.common.collect.Lists;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.game.vaultbattle.VaultBattle;
import io.jayms.serenno.game.vaultbattle.VaultBattleListener;
import io.jayms.serenno.game.vaultbattle.VaultBattleRequest;
import io.jayms.serenno.menu.MenuController;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.Team;
import io.jayms.serenno.vault.VaultMap;
import net.md_5.bungee.api.ChatColor;

public class GameManager {

	private int id = 0;
	private Map<Integer, Game> games = new HashMap<>();
	
	private KitSlotMenu kitSlotMenu;
	private MenuController ksController;
	
	private DuelTypeMenu duelTypeMenu;
	private MenuController dtController;
	
	private ArenaSelectMenu arenaSelectMenu;
	private MenuController asController;
	
	private VaultSideMenu vaultSideMenu;
	private MenuController vsController;

	private VaultBattleScalingMenu vaultBattleScalingMenu;
	private MenuController vbsController;
	
	private SimpleDuelListener duelListener;
	private VaultBattleListener vbListener;
	
	public GameManager() {
		kitSlotMenu = new KitSlotMenu();
		ksController = new MenuController(kitSlotMenu);
		
		duelTypeMenu = new DuelTypeMenu();
		dtController = new MenuController(duelTypeMenu);
		
		arenaSelectMenu = new ArenaSelectMenu();
		asController = new MenuController(arenaSelectMenu);
		
		vaultSideMenu = new VaultSideMenu();
		vsController = new MenuController(vaultSideMenu);

		vaultBattleScalingMenu = new VaultBattleScalingMenu();
		vbsController = new MenuController(vaultBattleScalingMenu);
		
		duelListener = new SimpleDuelListener();
		vbListener = new VaultBattleListener();
		Bukkit.getPluginManager().registerEvents(duelListener, SerennoCrimson.get());
		Bukkit.getPluginManager().registerEvents(vbListener, SerennoCrimson.get());
	}
	
	public KitSlotMenu getKitSlotMenu() {
		return kitSlotMenu;
	}
	
	public DuelTypeMenu getDuelTypeMenu() {
		return duelTypeMenu;
	}
	
	public ArenaSelectMenu getArenaSelectMenu() {
		return arenaSelectMenu;
	}
	
	public VaultSideMenu getVaultSideMenu() {
		return vaultSideMenu;
	}

	public VaultBattleScalingMenu getVaultBattleScalingMenu() {
		return vaultBattleScalingMenu;
	}

	public Game getGame(int id) {
		return games.get(id);
	}
	
	public Duel duel(Duelable receiver, DuelRequest request) {
		Arena arena = request.getMap();
		
		Map<ChatColor, Location> spawns = arena.getSpawnPoints();
		List<ChatColor> teamColors = Lists.newArrayList(spawns.keySet());
		
		if (teamColors.size() < 2) {
			receiver.sendMessage(ChatColor.RED + "Failed to start duel. Map doesn't have enough spawn points.");
			request.getSender().sendMessage(ChatColor.RED + "Failed to start duel. Map doesn't have enough spawn points.");
			return null;
		}
		
		Duelable sender = request.getSender();
		
		Team team1 = receiver instanceof Team ? (Team) receiver : SerennoCrimson.get().getTeamManager().getTeam((SerennoPlayer) receiver);
		Team team2 = sender instanceof Team ? (Team) sender : SerennoCrimson.get().getTeamManager().getTeam((SerennoPlayer) sender);
		boolean team1Temp = team1 == null;
		boolean team2Temp = team2 == null;
		
		if (team1Temp) {
			team1 = SerennoCrimson.get().getTeamManager().createTeam((SerennoPlayer) receiver);
		}
		if (team2Temp) {
			team2 = SerennoCrimson.get().getTeamManager().createTeam((SerennoPlayer) sender);
		}
		
		ChatColor color1 = teamColors.get(0);
		ChatColor color2 = teamColors.get(1);
		DuelTeam duelTeam1 = new DuelTeam(color1, team1, team1Temp);
		DuelTeam duelTeam2 = new DuelTeam(color2, team2, team2Temp);

		Duel duel;
		int gameId = id++;

		duel = new SimpleDuel(gameId, request.getMap(), request.getDuelType(), duelTeam1, duelTeam2) {};
			
		duel.start();
		games.put(duel.getID(), duel);
		return duel;
	}
	
	public VaultBattle vaultBattle(Duelable receiver, VaultBattleRequest request) {
		VaultMap vaultMap = request.getVaultMap();
		Map<ChatColor, Location> spawns = vaultMap.getSpawnPoints();
		List<ChatColor> teamColors = Lists.newArrayList(spawns.keySet());
		
		if (teamColors.size() < 2) {
			receiver.sendMessage(ChatColor.RED + "Failed to start vault battle. Map doesn't have enough spawn points.");
			request.getSender().sendMessage(ChatColor.RED + "Failed to start vault battle. Map doesn't have enough spawn points.");
			return null;
		}
		
		if (vaultMap.getDatabase().getGroupSource().size() < 2) {
			receiver.sendMessage(ChatColor.RED + "Failed to start vault battle. Map doesn't have enough groups.");
			request.getSender().sendMessage(ChatColor.RED + "Failed to start vault battle. Map doesn't have enough groups.");
			return null;
		}
		
		Duelable sender = request.getSender();
		
		Team team1 = receiver instanceof Team ? (Team) receiver : SerennoCrimson.get().getTeamManager().getTeam((SerennoPlayer) receiver);
		Team team2 = sender instanceof Team ? (Team) sender : SerennoCrimson.get().getTeamManager().getTeam((SerennoPlayer) sender);
		boolean team1Temp = team1 == null;
		boolean team2Temp = team2 == null;
		
		if (team1Temp) {
			team1 = SerennoCrimson.get().getTeamManager().createTeam((SerennoPlayer) receiver);
		}
		if (team2Temp) {
			team2 = SerennoCrimson.get().getTeamManager().createTeam((SerennoPlayer) sender);
		}
		
		ChatColor color2 = request.getTeamColor();
		ChatColor color1 = teamColors.stream().filter(c -> c != color2).findFirst().orElse(null);
		
		if (color1 == null || color2 == null) {
			return null;
		}
		
		DuelTeam duelTeam1 = new DuelTeam(color1, team1, team1Temp);
		DuelTeam duelTeam2 = new DuelTeam(color2, team2, team2Temp);
		
		VaultBattle battle;
		int gameId = id++;
		
		battle = new VaultBattle(gameId, vaultMap, duelTeam1, duelTeam2, request.getScaling());
			
		battle.start();
		games.put(battle.getID(), battle);
		return battle;
	}
	
	public void finishGame(Game game) {
		games.remove(game.getID());
		SerennoCrimson.get().getLogger().info("Game " + game.getID() + " has finished.");
	}
	
	public void shutdown() {
		if (games.isEmpty()) {
			return;
		}
		
		for (Game game : games.values()) {
			game.stop(ChatColor.RED + "Games shutting down");
		}
	}
	
	public List<Game> listGames() {
		return Lists.newArrayList(games.values());
	}
	
}
