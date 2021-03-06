package io.jayms.serenno.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.event.snitch.SnitchDestroyEvent;
import io.jayms.serenno.event.snitch.SnitchExitEvent;
import io.jayms.serenno.event.snitch.SnitchPlacementEvent;
import io.jayms.serenno.listener.citadel.SnitchListener;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.snitch.Snitch;
import io.jayms.serenno.model.citadel.snitch.SnitchDataSource;
import io.jayms.serenno.model.citadel.snitch.SnitchWorld;
import io.jayms.serenno.util.LocationTools;
import net.md_5.bungee.api.ChatColor;

public class SnitchManager {
	
	private SnitchDataSource dataSource;
	private ReinforcementManager rm;
	private Map<String, SnitchWorld> snitchWorlds = Maps.newConcurrentMap();
	
	private Multimap<Player, Snitch> playersInSnitches = HashMultimap.create();
	
	private SnitchListener snitchListener;
	
	public SnitchManager(ReinforcementManager rm, SnitchDataSource dataSource) {
		this.rm = rm;
		this.dataSource = dataSource;
		
		snitchListener = new SnitchListener(this);
		Bukkit.getPluginManager().registerEvents(snitchListener, SerennoCobalt.get());
		
		World world = Bukkit.getWorld(SerennoCobalt.get().getConfigManager().getDefaultReinforcementWorld());
		newSnitchWorld(world, dataSource);
	}
	
	public void enterSnitch(Player player, Snitch snitch) {
		snitch.notifyEntrance(player);
		playersInSnitches.put(player, snitch);
	}
	
	public Collection<Snitch> getInsideSnitches(Player player) {
		return new HashSet<>(playersInSnitches.get(player));
	}
	
	public void leaveSnitch(Player player, Snitch snitch) {
		SnitchExitEvent event = new SnitchExitEvent(snitch, player);
		Bukkit.getPluginManager().callEvent(event);
		
		playersInSnitches.remove(player, snitch);
	}
	
	public SnitchWorld newSnitchWorld(World world, SnitchDataSource dataSource) {
		SnitchWorld snitchWorld = new SnitchWorld(world, dataSource);
		snitchWorlds.put(world.getName(), snitchWorld);
		return snitchWorld;
	}
	
	public SnitchWorld getSnitchWorld(World world) {
		return snitchWorlds.get(world.getName());
	}
	
	public void deleteSnitchWorld(World world) {
		snitchWorlds.remove(world.getName());
	}
	
	public Set<Snitch> getSnitches(Location l) {
		SnitchWorld snitchWorld = getSnitchWorld(l.getWorld());
		if (snitchWorld == null) {
			return new HashSet<>();
		}
		return snitchWorld.getSnitches(l);
	}
	
	public Set<Snitch> getSnitchesInArea(Location l1, Location l2) {
		if (!(l1.getWorld().getUID().equals(l2.getWorld().getUID()))) {
			return new HashSet<>();
		}
		
		Set<Snitch> areaSnitches = new HashSet<>();
		
		World world = l1.getWorld();
		SnitchWorld snitchWorld = getSnitchWorld(world);
		for (Snitch snitch : snitchWorld.getAllSnitches()) {
			if (LocationTools.isBetween(l1, l2, snitch.getReinforcement(SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcementWorld(snitchWorld.getWorld())).getLocation())) {
				areaSnitches.add(snitch);
			}
		}
		
		return areaSnitches;
	}
	
	// false = allow block
	// true = dont allow block
	public void placeBlock(CitadelPlayer cp, Reinforcement rein, ItemStack item) {
		if (item.getType() != Material.NOTE_BLOCK) {
			return;
		}
		
		cp.getBukkitPlayer().sendMessage(ChatColor.AQUA + "You have placed a snitch. Do /janame <name> to name it.");
		placeSnitch(rein);
	}
	
	public void placeSnitch(Reinforcement reinforcement) {
		String name = "";
		int radius = 11;
		
		SnitchPlacementEvent event = new SnitchPlacementEvent(reinforcement, name, radius);
		Bukkit.getPluginManager().callEvent(event);
		
		name = event.getName();
		radius = event.getRadius();
		
		Snitch snitch = new Snitch(reinforcement, name, radius);
		
		World world = reinforcement.getLocation().getWorld();
		SnitchWorld snitchWorld = getSnitchWorld(world);
		snitchWorld.addSnitch(snitch);
	}
	
	public void destroySnitch(Reinforcement reinforcement) {
		Location loc = reinforcement.getLocation();
		SnitchWorld snitchWorld = getSnitchWorld(loc.getWorld());
		if (snitchWorld == null) {
			return;
		}
		
		Set<Snitch> snitches = snitchWorld.getSnitches(loc);
		if (snitches.isEmpty()) {
			return;
		}

		ReinforcementWorld reinforcementWorld = SerennoCobalt.get().getCitadelManager().getReinforcementManager().getReinforcementWorld(loc.getWorld());
		for (Snitch snitch : snitches) {
			if (snitch.getReinforcement(reinforcementWorld).equals(reinforcement)) {
				destroySnitch(reinforcementWorld, snitch);
				break;
			}
		}
	}
	
	public void destroySnitch(ReinforcementWorld reinforcementWorld, Snitch snitch) {
		SnitchDestroyEvent event = new SnitchDestroyEvent(snitch);
		Bukkit.getPluginManager().callEvent(event);
		
		World world = snitch.getReinforcement(reinforcementWorld).getLocation().getWorld();
		SnitchWorld snitchWorld = getSnitchWorld(world);
		snitchWorld.deleteSnitch(snitch);
	}
	
}
