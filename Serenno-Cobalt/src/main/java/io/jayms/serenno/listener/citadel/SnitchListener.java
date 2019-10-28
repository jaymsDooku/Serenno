package io.jayms.serenno.listener.citadel;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import io.jayms.serenno.event.reinforcement.ReinforcementCreationEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.manager.SnitchManager;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.snitch.Snitch;

public class SnitchListener implements Listener {

	private SnitchManager sm;
	
	public SnitchListener(SnitchManager sm) {
		this.sm = sm;
	}
	
	@EventHandler
	public void onReinforce(ReinforcementCreationEvent e) {
		Reinforcement rein = e.getReinforcement();
		Location loc = rein.getLocation();
		if (loc.getBlock().getType() != Material.NOTE_BLOCK) {
			return;
		}
		
		sm.placeSnitch(rein);
	}
	
	@EventHandler
	public void onDestroy(ReinforcementDestroyEvent e) {
		sm.destroySnitch(e.getReinforcement());
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
				sm.enterSnitch(player, snitch);
			}
		}
		for (Snitch insideSnitch : insideOf) {
			if (!snitches.contains(insideSnitch)) {
				sm.leaveSnitch(player, insideSnitch);
			}
		}
	}
	
	
}
