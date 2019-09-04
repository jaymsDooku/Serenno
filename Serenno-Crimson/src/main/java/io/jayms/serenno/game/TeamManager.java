package io.jayms.serenno.game;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Lists;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;

public class TeamManager implements Listener {

	private Set<Team> teams = new HashSet<>();
	
	public TeamManager() {	
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
	}
	
	public List<Team> listTeams() {
		return Lists.newArrayList(teams);
	}
	
	public void disbandTeam(Team team) {
		teams.remove(team);
	}
	
	public Team createTeam(SerennoPlayer player) {
		Team curTeam = getTeam(player);
		if (curTeam != null) {
			player.sendMessage(ChatColor.RED + "Can't create new team. You're already apart of one.");
			return curTeam;
		}
		
		Team newTeam = new Team(player);
		teams.add(newTeam);
		return newTeam;
	}
	
	public boolean hasTeam(SerennoPlayer player) {
		return getTeam(player) != null;
	}
	
	public Team getTeam(SerennoPlayer player) {
		return teams.stream().filter(t -> t.inTeam(player)).findFirst().orElse(null);
	}
	
	public Team getOrCreateTeam(SerennoPlayer player) {
		Team team = getTeam(player);
		if (team == null) {
			team = createTeam(player);
		}
		return team;
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		SerennoPlayer sPlayer = SerennoCrimson.get().getPlayerManager().getPlayer(player);
		Team team = getTeam(sPlayer);
		if (team == null) {
			return;
		}
		team.leave(sPlayer);
	}
	
}
