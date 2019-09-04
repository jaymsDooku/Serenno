package io.jayms.serenno.command;

import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.CitadelManager;
import io.jayms.serenno.manager.FinanceManager;
import io.jayms.serenno.manager.GroupManager;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.finance.FinancialPlayer;
import io.jayms.serenno.model.group.Group;
import io.jayms.serenno.model.group.GroupMember;
import io.jayms.serenno.model.group.GroupRank;
import net.md_5.bungee.api.ChatColor;

@CommandAlias("group")
public class GroupCommand extends BaseCommand {
	
	private GroupManager gm = SerennoCobalt.get().getGroupManager();
	private FinanceManager fm = SerennoCobalt.get().getFinanceManager();
	private CitadelManager cm = SerennoCobalt.get().getCitadelManager();
	
	@Subcommand("create")
	public void create(Player player, String groupName) {
		FinancialPlayer fp = fm.getPlayer(player);
		if (gm.ownsAGroupCalled(fp, groupName)) {
			player.sendMessage(ChatColor.RED + "You already own a group with this name.");
			return;
		}
		Group group = gm.createGroup(fp, groupName);
		player.sendMessage(ChatColor.YELLOW + "You have created a new group: " + ChatColor.GOLD + group.getName());
	}
	
	@Subcommand("invite")
	public void invite(Player player, String groupName, Player invitee) {
		Group group = gm.getGroup(player, groupName);
		if (group == null) {
			player.sendMessage(ChatColor.RED + "You aren't apart of that group.");
			return;
		}
		try {
			gm.inviteToGroup(group, invitee);
			player.sendMessage(ChatColor.BLUE + "You have invited " + ChatColor.AQUA + invitee.getName() + ChatColor.BLUE + " to " + ChatColor.AQUA + group.getName());
		} catch (IllegalArgumentException e) {
			player.sendMessage(ChatColor.RED + e.getMessage());
		}
	}
	
	@Subcommand("info")
	public void info(Player player, String groupName) {
		FinancialPlayer fp = fm.getPlayer(player);
		Group group = gm.getGroup(fp, groupName);
		if (group == null) {
			player.sendMessage(ChatColor.RED + "You aren't apart of that group.");
			return;
		}
		player.sendMessage(ChatColor.YELLOW + group.getName());
		player.sendMessage(ChatColor.GOLD + "---------------");
		player.sendMessage(ChatColor.GOLD + "Name: " + ChatColor.YELLOW + group.getName());
		player.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.YELLOW + group.getOwner().getDisplayName());
		player.sendMessage(ChatColor.GOLD + "Ranks: " + ChatColor.GOLD + "[" 
				+ ChatColor.YELLOW + group.getRanks().stream()
				.map(r -> r.getName())
				.collect(Collectors.joining(ChatColor.GOLD + ", ")) + ChatColor.GOLD + "]");
		player.sendMessage(ChatColor.GOLD + "Members: " + ChatColor.GOLD + "[" 
				+ ChatColor.YELLOW + group.getMembers().stream()
				.map(m -> m.getPlayer().getName())
				.collect(Collectors.joining(ChatColor.GOLD + ", ")) + ChatColor.GOLD + "]");
	}
	
	@Subcommand("list")
	public void list(Player player) {
		FinancialPlayer fp = fm.getPlayer(player);
		Collection<Group> groups = gm.listGroups(fp);
		player.sendMessage(ChatColor.YELLOW + "Groups");
		player.sendMessage(ChatColor.GOLD + "---------------");
		groups.stream().forEach(g -> {
			player.sendMessage(ChatColor.GOLD + "- " + ChatColor.YELLOW + g.getName());
		});
	}
	
	@Subcommand("kick")
	public void kick(Player player, String groupName, Player kickee) {
		Group group = gm.getGroup(player, groupName);
		if (group == null) {
			player.sendMessage(ChatColor.RED + "You aren't apart of that group.");
			return;
		}
		if (!group.isMember(kickee)) {
			player.sendMessage(ChatColor.RED + "That player isn't apart of that group.");
			return;
		}
		group.removeMember(kickee);
		player.sendMessage(ChatColor.YELLOW + "You have kicked " + ChatColor.RED + kickee.getName() + ChatColor.YELLOW + " from " + ChatColor.GOLD + group.getName());
	}
	
	@Subcommand("leave")
	public void leave(Player player, String groupName) {
		Group group = gm.getGroup(player, groupName);
		if (group == null) {
			player.sendMessage(ChatColor.RED + "You aren't apart of that group.");
			return;
		}
		group.removeMember(player);
		player.sendMessage(ChatColor.YELLOW + "You have left " + ChatColor.GOLD + group.getName());
	}
	
	@Subcommand("rank displayname")
	public void setRankDisplayName(Player player, String groupName, String rankName, String displayName) {
		Group group = gm.getGroup(player, groupName);
		if (group == null) {
			player.sendMessage(ChatColor.RED + "You aren't apart of that group.");
			return;
		}
		GroupRank rank = group.getRank(rankName);
		if (rank == null) {
			player.sendMessage(ChatColor.RED + "That rank doesn't exist.");
			return;
		}
		displayName = ChatColor.translateAlternateColorCodes('&', displayName);
		rank.setDisplayName(displayName);
		player.sendMessage(ChatColor.YELLOW + "You have set " + ChatColor.GOLD + rank + ChatColor.YELLOW + "'s display name to " + displayName);
	}
	
	@Subcommand("rank create")
	public void createRank(Player player, String groupName, Player member, String rankName, @Optional Integer order) {
		Group group = gm.getGroup(player, groupName);
		if (group == null) {
			player.sendMessage(ChatColor.RED + "You aren't apart of that group.");
			return;
		}
		if (group.isRank(rankName)) {
			player.sendMessage(ChatColor.RED + "A rank with that name already exists.");
			return;
		}
		GroupRank rank = group.createRank(rankName, order);
		player.sendMessage(ChatColor.YELLOW + "You have created a new rank: " + ChatColor.GOLD + rank.getDisplayName() + ChatColor.YELLOW + " in " + ChatColor.GOLD + group.getName());
	}
	
	@Subcommand("rank order")
	public void setRankOrder(Player player, String groupName, String rankName, int order) {
		Group group = gm.getGroup(player, groupName);
		if (group == null) {
			player.sendMessage(ChatColor.RED + "You aren't apart of that group.");
			return;
		}
		GroupRank rank = group.getRank(rankName);
		if (rank == null) {
			player.sendMessage(ChatColor.RED + "That rank doesn't exist.");
			return;
		}
		
		rank.setOrder(order);
		player.sendMessage(ChatColor.YELLOW + "You have set " + ChatColor.GOLD + rank + ChatColor.YELLOW + "'s order to " + order);
	}
	
	@Subcommand("rank")
	public void setRank(Player player, String groupName, Player member, String rankName) {
		Group group = gm.getGroup(player, groupName);
		if (group == null) {
			player.sendMessage(ChatColor.RED + "You aren't apart of that group.");
			return;
		}
		if (!group.isMember(member)) {
			player.sendMessage(ChatColor.RED + "That player isn't apart of that group.");
			return;
		}
		GroupMember gMember = group.getMember(member);
		GroupRank rank = group.getRank(rankName);
		if (rank == null) {
			player.sendMessage(ChatColor.RED + "That rank doesn't exist.");
			return;
		}
		
		gMember.setRank(rank);
		player.sendMessage(ChatColor.YELLOW + "You have set " + ChatColor.GOLD + member.getName() + ChatColor.YELLOW + "'s rank to " + ChatColor.GOLD + rankName);
	}
	
	@Subcommand("default")
	public void setDefault(Player player, String groupName) {
		Group group = gm.getGroup(player, groupName);
		if (group == null) {
			player.sendMessage(ChatColor.RED + "You aren't apart of that group.");
			return;
		}
		CitadelPlayer cp = cm.getCitadelPlayer(player);
		cp.setDefaultGroup(group);
		player.sendMessage(ChatColor.YELLOW + "You have set your default group to: " + ChatColor.GOLD + group.getName());
	}
	
	@HelpCommand
	public void help(CommandSender sender, CommandHelp help) {
		help.showHelp();
	}
	
}
