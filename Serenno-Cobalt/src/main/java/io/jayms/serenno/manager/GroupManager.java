package io.jayms.serenno.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.listener.GroupListener;
import io.jayms.serenno.model.finance.FinancialEntity;
import io.jayms.serenno.model.group.Group;

public class GroupManager {
	
	private Map<FinancialEntity, Map<String, Group>> allGroups = new ConcurrentHashMap<>();
	private Multimap<Group, UUID> invites = HashMultimap.create();
	
	private GroupListener listener;
	
	public GroupManager() {
		listener = new GroupListener(this);
		Bukkit.getPluginManager().registerEvents(listener, SerennoCobalt.get());
	}
	
	public Group createGroup(FinancialEntity financeEntity, String name) {
		Map<String, Group> groups = allGroups.get(financeEntity);
		
		if (groups == null) {
			groups = new HashMap<>();
			allGroups.put(financeEntity, groups);
		}
		
		if (groups.containsKey(name)) {
			throw new IllegalArgumentException("Group with that name already exists.");
		}
		
		Group group = new Group(name, financeEntity);
		groups.put(name, group);
		return group;
	}
	
	public Group getGroup(FinancialEntity financeEntity, String name) {
		Map<String, Group> groups = allGroups.get(financeEntity);
		if (groups == null) {
			return null;
		}
		return groups.get(name);
	}
	
	public boolean ownsAGroupCalled(FinancialEntity financeEntity, String name) {
		return getGroup(financeEntity, name) != null;
	}
	
	public Group getGroup(Player player, String name) {
		List<Group> groups = getGroups(player);
		if (groups.isEmpty()) {
			return null;
		}
		return groups.stream().filter(g -> g.getName().equals(name)).findFirst().orElse(null);
	}
	
	public List<Group> getGroups(Player player) {
		return getGroups().stream().filter(g -> g.isMember(player)).collect(Collectors.toList());
	}
	
	public List<Group> getGroups() {
		List<Group> groups = new ArrayList<>();
		for (Map<String, Group> groupsMap : allGroups.values()) {
			groups.addAll(groupsMap.values());
		}
		return groups;
	}
	
	public Collection<Group> listGroups(FinancialEntity financeEntity) {
		Map<String, Group> groups = allGroups.get(financeEntity);
		if (groups == null) {
			return new ArrayList<>();
		}
		return groups.values();
	}
	
	public void inviteToGroup(Group group, Player invitee) {
		if (invites.containsEntry(group, invitee.getUniqueId())) {
			throw new IllegalArgumentException("That player is already invited");
		}
		invites.put(group, invitee.getUniqueId());
	}
	
}
