package io.jayms.serenno.team.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;

public class LeaveTeamItem extends CustomItem {
	
	public LeaveTeamItem() {
		super(SerennoCrimson.get());
	}

	@Override
	protected ItemStackBuilder getItemStackBuilder() {
		return new ItemStackBuilder(Material.END_CRYSTAL, 1)
				.meta(new ItemMetaBuilder().name(ChatColor.YELLOW + "Leave Team"));
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
			SerennoPlayer sp = SerennoCrimson.get().getPlayerManager().get(who);
			SerennoCrimson.get().getTeamManager().getTeam(sp).leave(sp);
		};
	}
}
