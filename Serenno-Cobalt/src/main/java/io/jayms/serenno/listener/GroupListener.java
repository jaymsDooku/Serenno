package io.jayms.serenno.listener;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.manager.GroupManager;
import io.jayms.serenno.model.finance.FinancialPlayer;
import io.jayms.serenno.model.group.Group;

public class GroupListener implements Listener {

	private GroupManager gm;
	
	public GroupListener(GroupManager gm) {
		this.gm = gm;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		FinancialPlayer fp = SerennoCobalt.get().getFinanceManager().getPlayer(player);
		List<Group> groups = gm.getGroups(player);
		if (groups.isEmpty()) {
			Group group = gm.createGroup(fp, player.getName());
			SerennoCobalt.get().getCitadelManager().getCitadelPlayer(player).setDefaultGroup(group);
			SerennoCobalt.get().getLogger().info(player.getName() + " doesn't have any groups so making a default one for them.");
		}
	}

}
