package io.jayms.serenno.team.item;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import net.md_5.bungee.api.ChatColor;

public class ViewTeamItem extends CustomItem {
	
	public static final int ID = 406;
	
	public ViewTeamItem(int id) {
		super(SerennoCrimson.get(), id);
	}

	@Override
	protected ItemStackBuilder getItemStackBuilder(Map<String, Object> data) {
		return new ItemStackBuilder(Material.NETHER_STAR, 1)
				.meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "List Teammates"));
	}

	@Override
	public boolean preventOnLeftClick() {
		return false;
	}
	
	@Override
	public boolean preventOnRightClick() {
		return true;
	}
	
	@Override
	public Runnable getLeftClick(PlayerInteractEvent e) {
		return null;
	}

	@Override
	public Runnable getRightClick(PlayerInteractEvent e) {
		return () -> {
			Player who = e.getPlayer();
			
			SerennoCrimson.get().getTeamManager().getTeam(SerennoCrimson.get().getPlayerManager().get(who)).showInformation(who);
		};
	}
}
