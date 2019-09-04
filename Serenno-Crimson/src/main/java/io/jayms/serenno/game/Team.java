package io.jayms.serenno.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.player.SerennoBot;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.util.PlayerTools;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.ChatColor;

public class Team {

	private SerennoPlayer leader;
	private List<SerennoPlayer> members = new ArrayList<>();
	private boolean disbanded = false;
	
	public Team(SerennoPlayer leader) {
		this.leader = leader;
	}
	
	public void sendMessage(String message) {
		getAll().forEach(p -> {
			p.getBukkitPlayer().sendMessage(message);
		});
	}
	
	public void teleport(Location loc) {
		List<SerennoPlayer> all = getAll();
		for (SerennoPlayer sPlayer : all) {
			new BukkitRunnable() {
			
				@Override
				public void run() { 
					sPlayer.teleport(loc);
				}
			}.runTaskLater(SerennoCrimson.get(), 1L);
			if (SerennoCrimson.get().getLobby().inLobby(sPlayer)) {
				SerennoCrimson.get().getLobby().depart(sPlayer);
			}
		}
	}
	
	public void clean() {
		getAll().forEach(p -> {
			PlayerTools.clean(p.getBukkitPlayer());
		});
	}
	
	public void addMember(SerennoPlayer member) {
		if (members.contains(member)) {
			return;
		}
		members.add(member);
		sendMessage(ChatColor.GOLD + member.getName() + ChatColor.YELLOW + " has joined the team.");
	}
	
	public void memberLeave(SerennoPlayer member) {
		if (!(members.contains(member))) {
			return;
		}
		members.remove(member);
		sendMessage(ChatColor.GOLD + member.getName() + ChatColor.YELLOW + " has left the team.");
	}
	
	public void kickMember(SerennoPlayer member) {
		if (!(members.contains(member))) {
			return;
		}
		members.remove(member);
		sendMessage(ChatColor.GOLD + member.getName() + ChatColor.YELLOW + " was kicked from the team.");
	}
	
	public void promoteMember(SerennoPlayer member) {
		if (!(members.contains(member))) {
			return;
		}
		members.add(leader);
		setLeader(member);
		sendMessage(ChatColor.GOLD + member.getName() + ChatColor.YELLOW + " is now team leader.");
	}
	
	public void leave(SerennoPlayer player) {
		if (!inTeam(player)) {
			return;
		}
		
		if (isLeader(player)) {
			if (members.isEmpty()) {
				disband();
				return;
			}
			promoteMember(members.get(0));
		}
		memberLeave(player);
	}
	
	public void setLeader(SerennoPlayer player) {
		leader = player;
		sendMessage(ChatColor.GOLD + player.getName() + ChatColor.YELLOW + " is now team leader.");
	}
	
	public boolean isLeader(SerennoPlayer player) {
		return leader.equals(player);
	}
	
	public boolean inTeam(SerennoPlayer player) {
		return getAll().stream().filter(p -> p.equals(player)).findFirst().isPresent();
	}
	
	public void disband() {
		SerennoCrimson.get().getTeamManager().disbandTeam(this);
		disbanded = true;
		sendMessage(ChatColor.YELLOW + "Team disbanded.");
	}
	
	public boolean isDisbanded() {
		return disbanded;
	}
	
	public SerennoPlayer getLeader() {
		return leader;
	}
	
	public List<SerennoPlayer> getMembers() {
		return members;
	}
	
	public List<SerennoPlayer> getAll() {
		List<SerennoPlayer> all = new ArrayList<>();
		all.add(leader);
		all.addAll(members);
		return all;
	}
	
	public int size() {
		return getAll().size();
	}
	
}
