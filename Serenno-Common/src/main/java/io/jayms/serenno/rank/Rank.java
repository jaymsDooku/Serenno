package io.jayms.serenno.rank;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ChatColor;

public enum Rank {

	ADMIN(999,
		ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Admin" + ChatColor.WHITE + "]",
		Arrays.asList()),
	MOD(4,
		ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE + "Moderator" + ChatColor.WHITE + "]",
		Arrays.asList()),
	ENGINEER(3,
		ChatColor.WHITE + "[" + ChatColor.GREEN + "Engineer" + ChatColor.WHITE + "]",
		Arrays.asList("arena.engineer")),
	PRIME(2,
		ChatColor.WHITE + "[" + ChatColor.RED + "Prime" + ChatColor.WHITE + "]",
		Arrays.asList()),
	MEMBER(1,
		ChatColor.WHITE + "[" + ChatColor.YELLOW + "Member" + ChatColor.WHITE + "]",
		Arrays.asList(Permissions.TEAM_CREATE,
				Permissions.TEAM_INVITE,
				Permissions.TEAM_LEAVE,
				Permissions.TEAM_DISBAND,
				Permissions.TEAM_JOIN,
				Permissions.TEAM_INFO,
				Permissions.TEAM_KICK,
				Permissions.DUEL_REQUEST,
				Permissions.DUEL_ACCEPT,
				Permissions.KIT_EDITOR,
				Permissions.GAME_STATS
				));
	
	private int index;
	private String prefix;
	private List<String> perms;

	private Rank(int index, String prefix, List<String> perms) {
		this.index = index;
		this.prefix = prefix;
		this.perms = perms;
	}
	
	public int getIndex() {
		return index;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public List<String> getPerms() {
		return perms;
	}
	
	public static List<Rank> getRanksBelow(Rank rank) {
		return Arrays.stream(Rank.values()).filter(r -> r.index < rank.index).collect(Collectors.toList());
	}
	
}
