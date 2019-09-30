package io.jayms.serenno.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.common.collect.Lists;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.game.menu.ArenaSelectMenu;
import io.jayms.serenno.game.menu.DuelTypeMenu;
import io.jayms.serenno.game.menu.KitSlotMenu;
import io.jayms.serenno.menu.MenuController;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.Team;
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
	
	private SimpleDuelListener duelListener;
	
	public GameManager() {
		kitSlotMenu = new KitSlotMenu();
		ksController = new MenuController(kitSlotMenu);
		
		duelTypeMenu = new DuelTypeMenu();
		dtController = new MenuController(duelTypeMenu);
		
		arenaSelectMenu = new ArenaSelectMenu();
		asController = new MenuController(arenaSelectMenu);
		
		duelListener = new SimpleDuelListener();
		Bukkit.getPluginManager().registerEvents(duelListener, SerennoCrimson.get());
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
		System.out.println("color1: " + color1.name());
		System.out.println("color2: " + color2.name());
		DuelTeam duelTeam1 = new DuelTeam(color1, team1, team1Temp);
		DuelTeam duelTeam2 = new DuelTeam(color2, team2, team2Temp);
		
		Duel duel = new SimpleDuel(id++, request.getMap(), request.getDuelType(), duelTeam1, duelTeam2);
		duel.start();
		games.put(duel.getID(), duel);
		return duel;
	}
	
	public void finishGame(Game game) {
		games.remove(game.getID());
		SerennoCrimson.get().getLogger().info("Game " + game.getID() + " has finished.");
	}
	
	public List<Game> listGames() {
		return Lists.newArrayList(games.values());
	}
	
}
