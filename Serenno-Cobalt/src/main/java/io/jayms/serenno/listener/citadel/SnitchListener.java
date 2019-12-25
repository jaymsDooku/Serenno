package io.jayms.serenno.listener.citadel;

import java.util.Collection;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Lever;
import org.bukkit.scheduler.BukkitRunnable;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.event.reinforcement.ReinforcementCreationEvent;
import io.jayms.serenno.event.reinforcement.ReinforcementDestroyEvent;
import io.jayms.serenno.event.snitch.SnitchEnterEvent;
import io.jayms.serenno.manager.SnitchManager;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.snitch.Snitch;
import io.jayms.serenno.model.group.GroupPermissions;

public class SnitchListener implements Listener {

	private SnitchManager sm;
	
	public SnitchListener(SnitchManager sm) {
		this.sm = sm;
	}
	
	@EventHandler
	public void onReinforce(ReinforcementCreationEvent e) {
		Reinforcement rein = e.getReinforcement();
		Location loc = rein.getLocation();
		if (loc.getBlock().getType() != Material.NOTE_BLOCK && loc.getBlock().getType() != Material.JUKEBOX) {
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
				sm.enterSnitch(player, snitch);
			}
		}
		for (Snitch insideSnitch : insideOf) {
			if (!snitches.contains(insideSnitch)) {
				sm.leaveSnitch(player, insideSnitch);
			}
		}
	}
	
	@EventHandler
	public void onSnitchEnter(SnitchEnterEvent e) {
		Player player = e.getEntering();
		
		if (e.getSnitch().getReinforcement().getGroup().isAuthorized(player, GroupPermissions.SNITCH_NOTIFICATION_BYPASS)) {
			return;
		}
		
		Block snitchBlock = e.getSnitch().getReinforcement().getLocation().getBlock();
		if (snitchBlock.getType() != Material.JUKEBOX) {
			return;
		}
		
		Block northBlock = snitchBlock.getRelative(BlockFace.NORTH);
		if (northBlock.getType() != Material.LEVER) {
			return;
		}
		BlockState leverState = northBlock.getState();
		Lever lever = ((Lever) leverState.getData());
		lever.setPowered(true);
		leverState.setData(lever);
		leverState.update();
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				lever.setPowered(false);
				leverState.setData(lever);
				leverState.update();
			}
			
		}.runTaskLater(SerennoCobalt.get(), 15L);
	}
}
