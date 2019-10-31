package io.jayms.serenno.game.item;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.game.Duel;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;

public class StopSpectatingItem extends CustomItem {

	public StopSpectatingItem(int id) {
		super(SerennoCrimson.get(), id);
	}
	
	@Override
	protected ItemStackBuilder getItemStackBuilder(Map<String, Object> data) {
		return new ItemStackBuilder(Material.BARRIER, 1)
				.meta(new ItemMetaBuilder().name(ChatColor.RED + "Stop Spectating"));
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
			SerennoPlayer spectator = SerennoCrimson.get().getPlayerManager().get(who);
			Duel duel = spectator.getDuel();
			if (!duel.isSpectating(spectator)) {
				who.sendMessage(ChatColor.RED + "You're not even spectating a game.");
				return;
			}
			
			duel.stopSpectating(spectator);
		};
	}
}
