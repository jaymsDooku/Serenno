package io.jayms.serenno.model.group;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.finance.FinancialEntity;
import io.jayms.serenno.model.finance.FinancialPlayer;
import mkremins.fanciful.FancyMessage;

public class Group {
	
	private static final GroupRank.Builder DEFAULT_RANK = GroupRank.builder()
			.name("member")
			.displayName("Member")
			.order(5);
	
	private static final GroupRank.Builder OWNER_RANK = GroupRank.builder()
			.name("owner")
			.displayName("Owner")
			.order(1)
			.addPermission(GroupPermissions.ALL);
	
	private String name;
	private FinancialEntity owner;
	private GroupRank defaultRank;
	private Set<GroupRank> ranks;
	private Map<UUID, GroupMember> members;
	
	public Group(String name, FinancialEntity owner) {
		this.name = name;
		this.owner = owner;
		
		this.defaultRank = DEFAULT_RANK.build();
		this.ranks = new HashSet<>();
		addRank(defaultRank);
		
		this.members = new HashMap<>();
		
		if (owner instanceof FinancialPlayer) {
			GroupMember owningMember = addMember(((FinancialPlayer)owner).getBukkitPlayer());
			owningMember.setRank(OWNER_RANK.build());
		}
	}
	
	public String getName() {
		return name;
	}

	public FinancialEntity getOwner() {
		return owner;
	}
	
	public GroupRank createRank(String name, int order) {
		if (isRank(name)) {
			return null;
		}
		
		GroupRank rank = GroupRank.builder()
				.name(name)
				.displayName(name)
				.order(order)
				.permissions(new HashSet<>())
				.build();
		ranks.add(rank);
		return rank;
	}
	
	public void addRank(GroupRank rank) {
		ranks.add(rank);
	}
	
	public GroupRank getRank(String name) {
		return getRanks().stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
	}
	
	public boolean isRank(String name) {
		return getRank(name) != null;
	}
	
	public Set<GroupRank> getRanks() {
		return ranks;
	}
	
	public GroupMember addMember(Player player) {
		GroupMember member = getMember(player);
		if (member == null) {
			member = new GroupMember(this, player, defaultRank);
			members.put(player.getUniqueId(), member);
		}
		return member;
	}
	
	public void removeMember(Player player) {
		members.remove(player.getUniqueId());
	}
	
	public boolean isMember(Player player) {
		return getMember(player) != null;
	}
	
	public GroupMember getMember(Player player) {
		return members.get(player.getUniqueId());
	}
	
	public boolean isAuthorized(Player player, String permission) {
		GroupMember member = getMember(player);
		if (member == null) return false;
		return member.hasPermission(permission) || member.hasPermission(GroupPermissions.ALL);
	}
	
	public void sendMessage(String message) {
		for (GroupMember member : getMembers()) {
			member.getPlayer().sendMessage(message);
		}
	}
	
	public void sendMessage(FancyMessage message) {
		for (GroupMember member : getMembers()) {
			message.send(member.getPlayer());
		}
	}
	
	public Collection<GroupMember> getMembers() {
		return members.values();
	}
	
	public Set<Reinforcement> getReinforcements() {
		return null;
	}
	
	public Set<Bastion> getBastions() {
		return null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Group)) {
			return false;
		}
		
		Group group = (Group) obj;
		return name.equals(group.name) && owner.equals(group.owner);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, owner);
	}

	@Override
	public String toString() {
		return "Group [name=" + name + ", owner=" + owner + ", defaultRank=" + defaultRank + ", ranks=" + ranks
				+ ", members=" + members + "]";
	}
	
}
