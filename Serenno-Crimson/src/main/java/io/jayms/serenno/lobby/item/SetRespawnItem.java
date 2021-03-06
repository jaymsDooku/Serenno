package io.jayms.serenno.lobby.item;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.lobby.Lobby;
import net.md_5.bungee.api.ChatColor;

public class SetRespawnItem extends CustomItem {
	
	public static final int ID = 404;
	
	public SetRespawnItem(int id) {
		super(SerennoCrimson.get(), id);
	}

	@Override
	protected ItemStackBuilder getItemStackBuilder(Map<String, Object> data) {
		return new ItemStackBuilder(Material.ENDER_PORTAL_FRAME, 1)
				.meta(new ItemMetaBuilder().name(ChatColor.DARK_PURPLE + "Set Respawn Location"));
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
			Lobby lobby = SerennoCrimson.get().getLobby();
			lobby.setRespawnLocation(SerennoCrimson.get().getPlayerManager().get(e.getPlayer()), e.getPlayer().getLocation());
		};
	}
}
