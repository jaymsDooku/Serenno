package io.jayms.serenno.model.group;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;

public class GroupMember {

	private Player player;
	private Group group;
	private GroupRank rank;
	private Set<String> permissions = new HashSet<>();
	
	public GroupMember(Group group, Player player, GroupRank rank) {
		this.player = player;
		this.group = group;
		this.rank = rank;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setRank(GroupRank rank) {
		this.rank = rank;
	}
	
	public GroupRank getRank() {
		return rank;
	}
	
	public Group getGroup() {
		return group;
	}
	
	public boolean hasPermission(String perm) {
		return rank.hasPermission(perm) || permissions.contains(perm);
	}
	
	public Set<String> getPermissions() {
		return permissions;
	}
	
}
