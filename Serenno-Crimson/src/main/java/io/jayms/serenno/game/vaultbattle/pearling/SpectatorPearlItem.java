package io.jayms.serenno.game.vaultbattle.pearling;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import io.jayms.serenno.SerennoCrimson;
import io.jayms.serenno.item.CustomItem;
import io.jayms.serenno.kit.ItemMetaBuilder;
import io.jayms.serenno.kit.ItemStackBuilder;
import io.jayms.serenno.player.SerennoPlayer;
import net.md_5.bungee.api.ChatColor;

public class SpectatorPearlItem extends CustomItem {

	private SerennoPlayer pearled;
	
	public SpectatorPearlItem() {
		super(SerennoCrimson.get());
	}
	
	public void setPearled(SerennoPlayer pearled) {
		this.pearled = pearled;
	}
	
	public SerennoPlayer getPearled() {
		return pearled;
	}
	
	@Override
	protected ItemStackBuilder getItemStackBuilder() {
		return new ItemStackBuilder(Material.ENDER_PEARL, 1)
				.meta(new ItemMetaBuilder().name(ChatColor.RED + (pearled != null ? pearled.getName() : "No one") + "'s Pearl"));
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
			who.sendMessage(ChatColor.RED + pearled.getName() + ChatColor.YELLOW + " is held in this pearl.");
		};
	}
}
