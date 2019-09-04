package io.jayms.serenno.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.common.collect.Lists;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.arena.Arena;
import io.jayms.serenno.bot.Bot;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;

public class GameManager {

	private int id = 0;
	private Map<Integer, Game> games = new HashMap<>();
	
	private SimpleDuelListener duelListener;
	
	public GameManager() {
		duelListener = new SimpleDuelListener();
		Bukkit.getPluginManager().registerEvents(duelListener, SerennoCrimson.get());
	}
	
	public Game getGame(int id) {
		return games.get(id);
	}
	
	public Duel duel(SerennoPlayer receiver, DuelRequest request) {
		Arena arena = request.getMap();
		
		Map<ChatColor, Location> spawns = arena.getSpawnPoints();
		List<ChatColor> teamColors = Lists.newArrayList(spawns.keySet());
		
		if (teamColors.size() < 2) {
			receiver.getBukkitPlayer().sendMessage(ChatColor.RED + "Failed to start duel. Map doesn't have enough spawn points.");
			request.getSender().sendMessage(ChatColor.RED + "Failed to start duel. Map doesn't have enough spawn points.");
			return null;
		}
		
		Team team1 = SerennoCrimson.get().getTeamManager().getTeam(receiver);
		Team team2 = SerennoCrimson.get().getTeamManager().getTeam(request.getSender());
		boolean team1Temp = team1 == null;
		boolean team2Temp = team2 == null;
		
		if (team1Temp) {
			team1 = SerennoCrimson.get().getTeamManager().createTeam(receiver);
			team2 = SerennoCrimson.get().getTeamManager().createTeam(request.getSender());
		}
		
		DuelTeam duelTeam1 = new DuelTeam(teamColors.get(0), team1, team1Temp);
		DuelTeam duelTeam2 = new DuelTeam(teamColors.get(1), team2, team2Temp);
		
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
