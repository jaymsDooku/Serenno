package io.jayms.serenno.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import io.jayms.serenno.player.ui.LobbyTeam;
import io.jayms.serenno.ui.UI;
import io.jayms.serenno.ui.UIManager;
import io.jayms.serenno.ui.UIScoreboard;
import io.jayms.serenno.ui.UITeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duelable;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.team.event.PlayerJoinTeamEvent;
import io.jayms.serenno.team.event.PlayerLeaveTeamEvent;
import io.jayms.serenno.team.event.TeamDisbandEvent;
import io.jayms.serenno.team.event.TeamSetLeaderEvent;
import io.jayms.serenno.util.PlayerTools;
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatColor;

public class Team implements Duelable {

	private UUID id;
	private SerennoPlayer leader;
	private List<SerennoPlayer> members = new ArrayList<>();
	private boolean inDuel = false;
	private boolean disbanded = false;
	
	public Team(SerennoPlayer leader) {
		this.id = UUID.randomUUID();
		this.leader = leader;
	}
	
	@Override
	public String getName() {
		return leader.getName();
	}
	
	public void showInformation(Player player) {
		SerennoPlayer leader = getLeader();
		List<SerennoPlayer> members = getMembers();
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Team Info of: "  + ChatColor.DARK_PURPLE + leader.getName());
		player.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.STRIKETHROUGH + "--------------");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Leader: " + ChatColor.DARK_PURPLE + leader.getName());
		player.sendMessage(ChatColor.LIGHT_PURPLE + "Members: " + ChatColor.DARK_PURPLE + (members.isEmpty() ? "None" : members.stream()
		.map(m -> m.getName())
		.collect(Collectors.joining(ChatColor.LIGHT_PURPLE + ", "))));
	}
	
	public void sendMessage(FancyMessage message) {
		getAll().forEach(p -> {
			p.sendMessage(message);
		});
	}
	
	public void sendMessage(String message) {
		getAll().forEach(p -> {
			p.sendMessage(message);
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
		
		PlayerJoinTeamEvent event = new PlayerJoinTeamEvent(member, this);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	public void memberLeave(SerennoPlayer member) {
		if (!(members.contains(member))) {
			return;
		}
		members.remove(member);
		
		PlayerLeaveTeamEvent event = new PlayerLeaveTeamEvent(member, this);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	public void kickMember(SerennoPlayer member) {
		if (!(members.contains(member))) {
			return;
		}
		sendMessage(ChatColor.DARK_PURPLE + member.getName() + ChatColor.LIGHT_PURPLE + " was kicked from the team.");
		members.remove(member);
	}
	
	public void promoteMember(SerennoPlayer member) {
		if (!(members.contains(member))) {
			return;
		}
		members.add(leader);
		setLeader(member);
		sendMessage(ChatColor.DARK_PURPLE + member.getName() + ChatColor.LIGHT_PURPLE + " is now team leader.");
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
		SerennoPlayer oldLeader = leader;
		leader = player;
		
		TeamSetLeaderEvent event = new TeamSetLeaderEvent(oldLeader, leader, this);
		Bukkit.getPluginManager().callEvent(event);
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
		
		TeamDisbandEvent event = new TeamDisbandEvent(this);
		Bukkit.getPluginManager().callEvent(event);
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
	
	public List<SerennoPlayer> getAvailableMembers() {
		List<SerennoPlayer> all = getAll();
		return all.stream()
			.filter(u -> {
				Player player = u.getBukkitPlayer();
				return player != null && player.isOnline();
			}).collect(Collectors.toList());
	}
	
	public int size() {
		return getAll().size();
	}

	public void name(List<SerennoPlayer> players, UITeam team) {
		for (SerennoPlayer ally : getAll()) {
			Player allyPlayer = ally.getBukkitPlayer();
			UI ui = UIManager.getUIManager().getScoreboard(allyPlayer);
			UIScoreboard scoreboard = ui.getScoreboard();

			for (SerennoPlayer lobbyPlayer : players) {
				scoreboard.setTeam(lobbyPlayer.getBukkitPlayer(), team);
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Team)) {
			return false;
		}
		
		Team team = (Team) obj;
		return team.getID().equals(id);
	}
	
	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public UUID getID() {
		return id;
	}

	@Override
	public boolean inDuel() {
		return false;
	}

	@Override
	public boolean isSpectating() {
		return false;
	}

	@Override
	public void showPlayer(Duelable duelable) {
		getAvailableMembers().forEach(m -> {
			m.showPlayer(duelable);
		});
	}

	@Override
	public void hidePlayer(Duelable duelable) {
		getAvailableMembers().forEach(m -> {
			m.hidePlayer(duelable);
		});
	}
	
}
