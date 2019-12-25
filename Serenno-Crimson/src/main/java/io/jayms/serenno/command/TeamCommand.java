package io.jayms.serenno.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.player.SerennoPlayer;
import io.jayms.serenno.player.SerennoPlayerManager;
import io.jayms.serenno.rank.Permissions;
import io.jayms.serenno.team.Team;
import io.jayms.serenno.team.TeamManager;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("team|party|p|t")
public class TeamCommand extends BaseCommand {

	private SerennoPlayerManager pm = SerennoCrimson.get().getPlayerManager();
	private TeamManager tm = SerennoCrimson.get().getTeamManager();
	
	@CommandPermission(Permissions.TEAM_CREATE)
	@Subcommand("create")
	public void create(Player player) {
		SerennoPlayer sp = pm.get(player);
		tm.createTeam(sp);
	}
	
	@CommandPermission(Permissions.TEAM_INVITE)
	@Subcommand("invite")
	public void invite(Player player, String invitee) {
		SerennoPlayer sp = pm.get(player);
		Team team = tm.getTeam(sp);
		if (team == null) {
			player.sendMessage(ChatColor.RED + "You aren't apart of a team.");
			return;
		}
		
		Player inviteeP = Bukkit.getPlayer(invitee);
		if (inviteeP == null) {
			player.sendMessage(ChatColor.RED + "That player isn't online.");
			return;
		}
		SerennoPlayer inviteeSp = pm.get(inviteeP);
		Team inviteeTeam = tm.getTeam(inviteeSp);
		if (inviteeTeam != null) {
			player.sendMessage(ChatColor.RED + "That player is already part of a team.");
			return;
		}
		
		tm.invite(inviteeSp, team);
		player.sendMessage(ChatColor.LIGHT_PURPLE + "You have invited " + ChatColor.DARK_PURPLE + inviteeP.getName() + ChatColor.LIGHT_PURPLE + " to your team!");
	}
	
	@CommandPermission(Permissions.TEAM_JOIN)
	@Subcommand("join")
	public void join(Player player) {
		SerennoPlayer sp = pm.get(player);
		Team team = tm.getTeam(sp);
		if (team != null) {
			player.sendMessage(ChatColor.RED + "You are already apart of a team.");
			return;
		}
		
		tm.acceptInvite(sp);
	}
	
	@CommandPermission(Permissions.TEAM_INFO)
	@Subcommand("info")
	public void info(Player player, @Optional String otherPlayer) {
		SerennoPlayer target = pm.get(player);
		if (otherPlayer != null) {
			Player otherPlayerP = Bukkit.getPlayer(otherPlayer);
			if (otherPlayerP == null) {
				player.sendMessage(ChatColor.RED + "That player isn't online.");
				return;
			}
			target = pm.get(otherPlayerP);
		}
		
		Team team = tm.getTeam(target);
		if (team == null) {
			player.sendMessage(ChatColor.RED + "That player isn't apart of a team.");
			return;
		}
		
		team.showInformation(player);
	}
	
	@CommandPermission(Permissions.TEAM_LEAVE)
	@Subcommand("leave")
	public void leave(Player player) {
		SerennoPlayer sp = pm.get(player);
		Team team = tm.getTeam(sp);
		if (team == null) {
			player.sendMessage(ChatColor.RED + "You can't leave your team because you don't have one.");
			return;
		}
		
		team.leave(sp);
	}
	
	@CommandPermission(Permissions.TEAM_DISBAND)
	@Subcommand("disband")
	public void disband(Player player) {
		SerennoPlayer sp = pm.get(player);
		Team team = tm.getTeam(sp);
		if (team == null) {
			player.sendMessage(ChatColor.RED + "You can't disband your team because you don't have one.");
			return;
		}
		
		if (!team.isLeader(sp)) {
			player.sendMessage(ChatColor.RED + "Only the leader of your team can disband it.");
			return;
		}
		
		team.disband();
	}
	
	@CommandPermission(Permissions.TEAM_PROMOTE)
	@Subcommand("promote")
	public void promote(Player player, String otherPlayer) {
		SerennoPlayer sp = pm.get(player);
		
		Player otherPlayerP = Bukkit.getPlayer(otherPlayer);
		if (otherPlayerP == null) {
			player.sendMessage(ChatColor.RED + "That player isn't online.");
			return;
		}
		SerennoPlayer osp = pm.get(otherPlayerP);
		Team team = tm.getTeam(sp);
		if (team == null) {
			player.sendMessage(ChatColor.RED + "You can't leave your team because you don't have one.");
			return;
		}
		
		if (!team.isLeader(sp)) {
			player.sendMessage(ChatColor.RED + "Only the leader of your team can promote members.");
			return;
		}
		
		Team oTeam = tm.getTeam(osp);
		if (oTeam == null) {
			player.sendMessage(ChatColor.RED + "That player isn't apart of a team.");
			return;
		}
		
		if (!team.equals(oTeam)) {
			player.sendMessage(ChatColor.RED + "That player isn't on the same team as you.");
			return;
		}
		
		team.promoteMember(osp);
	}
	
	@CommandPermission(Permissions.TEAM_KICK)
	@Subcommand("kick")
	public void kick(Player player, String kickee) {
		SerennoPlayer sp = pm.get(player);
		Team team = tm.getTeam(sp);
		if (team == null) {
			player.sendMessage(ChatColor.RED + "You can't leave your team because you don't have one.");
			return;
		}
		
		if (!team.isLeader(sp)) {
			player.sendMessage(ChatColor.RED + "Only the leader of your team can kick members.");
			return;
		}
		
		Player kickeeP = Bukkit.getPlayer(kickee);
		if (kickeeP == null) {
			player.sendMessage(ChatColor.RED + "That player isn't online.");
			return;
		}
		SerennoPlayer osp = pm.get(kickeeP);
		if (sp.equals(osp)) {
			player.sendMessage(ChatColor.RED + "You can't kick yourself.");
			return;
		}
		
		Team oTeam = tm.getTeam(osp);
		if (oTeam == null) {
			player.sendMessage(ChatColor.RED + "That player isn't apart of a team.");
			return;
		}
		
		if (!team.equals(oTeam)) {
			player.sendMessage(ChatColor.RED + "That player isn't on the same team as you.");
			return;
		}
		
		team.kickMember(osp);
	}
	
	@CommandPermission(Permissions.TEAM_LIST)
	@Subcommand("list")
	public void list(CommandSender sender) {
		List<Team> teams = tm.listTeams();
		sender.sendMessage(ChatColor.LIGHT_PURPLE + "Teams");
		sender.sendMessage(ChatColor.DARK_PURPLE + "" + ChatColor.STRIKETHROUGH + "---------------");
		for (Team team : teams) {
			sender.sendMessage(ChatColor.DARK_PURPLE + "- " + ChatColor.LIGHT_PURPLE + team.getLeader().getName() + "'s Team");
		}
	}
	
}
