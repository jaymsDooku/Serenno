package io.jayms.serenno.rank;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import io.jayms.serenno.SerennoCommon;
import io.jayms.serenno.player.CommonPlayer;

public class RankHandler {

	private SerennoCommon common;
	private Map<UUID, PermissionAttachment> attachments = new ConcurrentHashMap<>();
	
	public RankHandler(SerennoCommon common) {
		this.common = common;
	}
	
	public void initPermissions(CommonPlayer player) {
		List<Rank> ranks = new ArrayList<>();
		ranks.add(player.getRank());
		ranks.addAll(Rank.getRanksBelow(player.getRank()));
		
		ranks.stream().forEach(r -> {
			common.getLogger().info("Giving " + player.getBukkitPlayer().getName() + " permissions for " + r + "...");
			initPermissions(player, r);
		});
	}
	
	public void initPermissions(CommonPlayer player, Rank rank) {
		Player bukkitPlayer = player.getBukkitPlayer();
		
		PermissionAttachment attachment = attachments.get(bukkitPlayer.getUniqueId());
		if (attachment == null) {
			attachment = bukkitPlayer.addAttachment(common);
			attachments.put(bukkitPlayer.getUniqueId(), attachment);
		} else {
			common.getLogger().info(bukkitPlayer.getName() + " already has a permission attachment.");
		}
		
		for (String perm : rank.getPerms()) {
			attachment.setPermission(perm, true);
			common.getLogger().info("Set permission " + perm + " for " + bukkitPlayer.getName());
		}
	}
	
	public void removePermissions(CommonPlayer player) {
		Player bukkitPlayer = player.getBukkitPlayer();
		
		PermissionAttachment attachment = attachments.remove(bukkitPlayer.getUniqueId());
		common.getLogger().info("Removing permission attachment for " + bukkitPlayer.getName() + "...");
		if (attachment == null) return;

		for (String perm : player.getRank().getPerms()) {
			attachment.unsetPermission(perm);
		}
		bukkitPlayer.removeAttachment(attachment);
		common.getLogger().info("Permission attachment removed.");
	}
	
}
