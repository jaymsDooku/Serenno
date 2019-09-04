package io.jayms.serenno.region;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import io.jayms.serenno.SerennoCrimson;

public class RegionListener implements Listener {

	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Block block = e.getBlock();
		Location loc = block.getLocation();
		RegionManager regionManager = SerennoCrimson.get().getRegionManager();
		Region region = regionManager.getRegion(loc);
		if (region == null) {
			return;
		}
		if (region.isFlagEnabled(RegionFlags.BLOCK_PLACE)) {
			return;
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Location loc = block.getLocation();
		RegionManager regionManager = SerennoCrimson.get().getRegionManager();
		Region region = regionManager.getRegion(loc);
		if (region == null) {
			return;
		}
		if (region.isFlagEnabled(RegionFlags.BLOCK_BREAK)) {
			return;
		}
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		Location loc = e.getEntity().getLocation();
		RegionManager regionManager = SerennoCrimson.get().getRegionManager();
		Region region = regionManager.getRegion(loc);
		if (region == null) {
			return;
		}
		
		if (e.getDamager() instanceof Player) {
			if ((e.getEntity() instanceof Player)) {
				if (region.isFlagEnabled(RegionFlags.PVP)) {
					return;
				}
				e.setCancelled(true);
			} else {
				if (region.isFlagEnabled(RegionFlags.PVE)) {
					return;
				}
				e.setCancelled(true);
			}
		}
	}
}
