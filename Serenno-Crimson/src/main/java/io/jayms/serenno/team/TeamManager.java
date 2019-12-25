package io.jayms.serenno.team;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.google.common.collect.Lists;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.event.TeamCreationEvent;
import net.md_5.bungee.api.ChatColor;

public class TeamManager implements Listener {

	private Set<Team> teams = new HashSet<>();
	private Map<SerennoPlayer, Team> invites = new HashMap<>();
	
	private TeamListener teamListener;
	
	public TeamManager() {	
		Bukkit.getPluginManager().registerEvents(this, SerennoCrimson.get());
		Bukkit.getPluginManager().registerEvents(teamListener = new TeamListener(), SerennoCrimson.get());
	}
	
	public void acceptInvite(SerennoPlayer joiner) {
		if (!invites.containsKey(joiner)) {
			return;
		}
		
		Team team = invites.remove(joiner);
		team.addMember(joiner);
	}
	
	public void invite(SerennoPlayer invitee, Team invitedTeam) {
		invites.put(invitee, invitedTeam);
		invitee.sendMessage(ChatColor.DARK_PURPLE + invitedTeam.getLeader().getName() + ChatColor.LIGHT_PURPLE + " has invited you to their team!");
	}
	
	public List<Team> listTeams() {
		return Lists.newArrayList(teams);
	}
	
	public void disbandTeam(Team team) {
		teams.remove(team);
	}
	
	public Team createTeam(SerennoPlayer player) {
		player.getCommonPlayer();
		
		Team curTeam = getTeam(player);
		if (curTeam != null) {
			player.sendMessage(ChatColor.RED + "Can't create new team. You're already apart of one.");
			return null;
		}
		
		Team newTeam = new Team(player);
		teams.add(newTeam);
		
		TeamCreationEvent event = new TeamCreationEvent(player, newTeam);
		Bukkit.getPluginManager().callEvent(event);
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
		SerennoPlayer sPlayer = SerennoCrimson.get().getPlayerManager().get(player);
		Team team = getTeam(sPlayer);
		if (team == null) {
			return;
		}
		team.leave(sPlayer);
	}
	
}
