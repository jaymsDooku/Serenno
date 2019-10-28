package io.jayms.serenno.region;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class RegionListener implements Listener {

	private RegionManager regionManager;
	
	public RegionListener(RegionManager regionManager) {
		this.regionManager = regionManager;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlace(BlockPlaceEvent e) {
		Block block = e.getBlock();
		Location loc = block.getLocation();
		Region region = regionManager.getRegion(loc);
		if (region == null) {
			return;
		}
		if (region.isFlagEnabled(RegionFlags.BLOCK_PLACE)) {
			return;
		}
		e.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		Location loc = block.getLocation();
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
	public void onDamage(EntityDamageEvent e) {
		Entity entity = e.getEntity();
		Location loc = entity.getLocation();
		Region region = regionManager.getRegion(loc);
		if (region == null) {
			return;
		}
		if (region.isFlagEnabled(RegionFlags.DAMAGE_LOSS)) {
			return;
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		if (!(e.getEntity() instanceof Player)) return;
		
		Player player = (Player) e.getEntity();
		
		Region region = regionManager.getRegion(player.getLocation());
		if (region == null) {
			return;
		}
		if (region.isFlagEnabled(RegionFlags.HUNGER_LOSS)) {
			return;
		}
		
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e) {
		Location loc = e.getEntity().getLocation();
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
