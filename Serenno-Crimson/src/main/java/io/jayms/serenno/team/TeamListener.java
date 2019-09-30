package io.jayms.serenno.team;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.event.PlayerJoinTeamEvent;
import io.jayms.serenno.team.event.PlayerLeaveTeamEvent;
import io.jayms.serenno.team.event.TeamCreationEvent;
import io.jayms.serenno.team.event.TeamDisbandEvent;
import io.jayms.serenno.team.event.TeamSetLeaderEvent;
import net.md_5.bungee.api.ChatColor;

public class TeamListener implements Listener {

	@EventHandler
	public void onCreate(TeamCreationEvent e) {
		SerennoPlayer player = e.getCreator();
		player.sendMessage(ChatColor.LIGHT_PURPLE + "You've created a new team!");
		SerennoCrimson.get().getLobby().giveItems(player);
	}
	
	@EventHandler
	public void onSetLeader(TeamSetLeaderEvent e) {
		SerennoPlayer oldLeader = e.getOldLeader();
		SerennoPlayer newLeader = e.getNewLeader();
		
		Team team = e.getTeam();
		if (oldLeader != null) {
			team.sendMessage(ChatColor.DARK_PURPLE + newLeader.getName() + ChatColor.LIGHT_PURPLE + " is now team leader.");
			
			SerennoCrimson.get().getLobby().giveItems(oldLeader);
		}
	
		SerennoCrimson.get().getLobby().giveItems(newLeader);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinTeamEvent e) {
		SerennoPlayer joining = e.getJoining();
		
		Team team = e.getTeam();
		team.sendMessage(ChatColor.DARK_PURPLE + joining.getName() + ChatColor.LIGHT_PURPLE + " has joined the team.");
		
		SerennoCrimson.get().getLobby().giveItems(joining);
	}
	
	@EventHandler
	public void onLeave(PlayerLeaveTeamEvent e) {
		SerennoPlayer leaving = e.getLeaving();
		
		Team team = e.getTeam();
		team.sendMessage(ChatColor.DARK_PURPLE + leaving.getName() + ChatColor.LIGHT_PURPLE + " has left the team.");
		
		SerennoCrimson.get().getLobby().giveItems(leaving);
	}
	
	@EventHandler
	public void onDisband(TeamDisbandEvent e) {
		Team team = e.getTeam();
		team.sendMessage(ChatColor.LIGHT_PURPLE + "Team disbanded.");
		
		for (SerennoPlayer p : team.getAvailableMembers()) {
			SerennoCrimson.get().getLobby().giveItems(p);
		}
	}
	
}
