package io.jayms.serenno.listener.citadel;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.event.reinforcement.PlayerReinforcementCreationEvent;
import io.jayms.serenno.manager.SnitchManager;
import io.jayms.serenno.model.citadel.snitch.Snitch;

public class SnitchListener implements Listener {

	private SnitchManager sm;
	
	public SnitchListener(SnitchManager sm) {
		this.sm = sm;
	}
	
	@EventHandler
	public void onReinforce(PlayerReinforcementCreationEvent e) {
		ItemStack item = e.getItemPlaced();
		if (item.getType() != Material.NOTE_BLOCK) {
			return;
		}
		
		sm.placeSnitch(e.getReinforcement());
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		
		Location from = e.getFrom();
		Location to = e.getTo();
		
		if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
			return;
		}
		
		Collection<Snitch> insideOf = sm.getInsideSnitches(player);
		Set<Snitch> snitches = sm.getSnitches(to);
		for (Snitch snitch : snitches) {
			if (!insideOf.contains(snitch)) {
				snitch.notifyEntrance(player);
			}
		}
		for (Snitch insideSnitch : insideOf) {
			if (!snitches.contains(insideSnitch)) {
				sm.leaveSnitch(player, insideSnitch);
			}
		}
	}
	
	
}
