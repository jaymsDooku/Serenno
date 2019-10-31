package io.jayms.serenno.listener;

import java.util.LinkedList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.util.Vector;

import io.jayms.serenno.SerennoCobalt;

public class MechanicsListener implements Listener {

	@EventHandler
	public void onExit(VehicleExitEvent e) {
		final Vehicle vehicle = e.getVehicle();
	    if (vehicle == null || !(vehicle instanceof Minecart)) {
	      return;
	    }
	    final Entity passengerEntity = e.getExited();
	    if (passengerEntity == null || !(passengerEntity instanceof Player)) {
	      return;
	    }
	    // Must delay the teleport 2 ticks or else the player's mis-managed
	    //  movement still occurs. With 1 tick it could still occur.
	    final Player player = (Player)passengerEntity;
	    final Location vehicleLoc = vehicle.getLocation();
	    Bukkit.getScheduler().runTaskLater(SerennoCobalt.get(), new Runnable() {
	      @Override
	      public void run() {
	        if (!tryToTeleport(player, vehicleLoc)) {
	          player.setHealth(0.000000D);
	        }
	      }
	    }, 2L);
	}
	
	public boolean checkForTeleportSpace(Location loc) {
	    final Block block = loc.getBlock();
	    final Material mat = block.getType();
	    if (mat.isSolid()) {
	      return false;
	    }
	    final Block above = block.getRelative(BlockFace.UP);
	    if (above.getType().isSolid()) {
	      return false;
	    }
	    return true;
	  }
	
	public boolean tryToTeleport(Player player, Location location) {
	    Location loc = location.clone();
	    loc.setX(Math.floor(loc.getX()) + 0.500000D);
	    loc.setY(Math.floor(loc.getY()) + 0.02D);
	    loc.setZ(Math.floor(loc.getZ()) + 0.500000D);
	    final Location baseLoc = loc.clone();
	    final World world = baseLoc.getWorld();
	    // Check if teleportation here is viable
	    boolean performTeleport = checkForTeleportSpace(loc);
	    if (!performTeleport) {
	      loc.setY(loc.getY() + 1.000000D);
	      performTeleport = checkForTeleportSpace(loc);
	    }
	    if (performTeleport) {
	      player.setVelocity(new Vector());
	      player.teleport(loc);
	      return true;
	    }
	    loc = baseLoc.clone();
	    // Create a sliding window of block types and track how many of those
	    //  are solid. Keep fetching the block below the current block to move down.
	    int air_count = 0;
	    LinkedList<Material> air_window = new LinkedList<Material>();
	    loc.setY((float)world.getMaxHeight() - 2);
	    Block block = world.getBlockAt(loc);
	    for (int i = 0; i < 4; ++i) {
	      Material block_mat = block.getType();
	      if (!block_mat.isSolid()) {
	        ++air_count;
	      }
	      air_window.addLast(block_mat);
	      block = block.getRelative(BlockFace.DOWN);
	    }
	    // Now that the window is prepared, scan down the Y-axis.
	    while (block.getY() >= 1) {
	      Material block_mat = block.getType();
	      if (block_mat.isSolid()) {
	        if (air_count == 4) {
	          player.setVelocity(new Vector());
	          loc = block.getLocation();
	          loc.setX(Math.floor(loc.getX()) + 0.500000D);
	          loc.setY(loc.getY() + 1.02D);
	          loc.setZ(Math.floor(loc.getZ()) + 0.500000D);
	          player.teleport(loc);
	          return true;
	        }
	      } else { // !block_mat.isSolid()
	        ++air_count;
	      }
	      air_window.addLast(block_mat);
	      if (!air_window.removeFirst().isSolid()) {
	        --air_count;
	      }
	      block = block.getRelative(BlockFace.DOWN);
	    }
	    return false;
	  }
}

