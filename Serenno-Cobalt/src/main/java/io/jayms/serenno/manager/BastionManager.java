package io.jayms.serenno.manager;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.event.bastion.BastionPlacementEvent;
import io.jayms.serenno.kit.ItemStackKey;
import io.jayms.serenno.listener.citadel.BastionListener;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionDataSource;
import io.jayms.serenno.model.citadel.bastion.BastionWorld;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.finance.FinancialEntity;
import io.jayms.serenno.util.LocationTools;
import net.md_5.bungee.api.ChatColor;

public class BastionManager {

	private BastionDataSource dataSource;
	private ReinforcementManager rm;
	private Map<String, BastionWorld> bastionWorlds = Maps.newConcurrentMap();
	private Map<ItemStackKey, BastionBlueprint> bastionBlueprints = Maps.newConcurrentMap();
	
	private BastionListener bastionListener;
	
	public BastionManager(CitadelManager cm, ReinforcementManager rm, BastionDataSource dataSource) {
		this.rm = rm;
		this.dataSource = dataSource;
		
		bastionListener = new BastionListener(cm, this);
		Bukkit.getPluginManager().registerEvents(bastionListener, SerennoCobalt.get());
		
		World world = Bukkit.getWorld(SerennoCobalt.get().getConfigManager().getDefaultReinforcementWorld());
		newBastionWorld(world, dataSource);
	}
	
	public void registerBastionBlueprint(BastionBlueprint blueprint) {
		bastionBlueprints.put(new ItemStackKey(blueprint.getItemStack()), blueprint);
	}
	
	public void unregisterBastionBlueprint(BastionBlueprint blueprint) {
		bastionBlueprints.remove(new ItemStackKey(blueprint.getItemStack()));
	}
	
	public BastionBlueprint getBastionBlueprint(String name) {
		return bastionBlueprints.values().stream()
				.filter(b -> b.getName().equalsIgnoreCase(name))
				.findFirst()
				.orElse(null);
	}
	
	public BastionBlueprint getBastionBlueprint(ItemStack it) {
		return bastionBlueprints.get(new ItemStackKey(it));
	}
	
	public Collection<BastionBlueprint> getBastionBlueprints() {
		return bastionBlueprints.values();
	}
	
	public boolean hasBastionBlueprint(ItemStack it) {
		return bastionBlueprints.containsKey(new ItemStackKey(it));
	}
	
	public BastionWorld newBastionWorld(World world, BastionDataSource dataSource) {
		BastionWorld bastionWorld = new BastionWorld(world, dataSource);
		bastionWorlds.put(world.getName(), bastionWorld);
		return bastionWorld;
	}
	
	public BastionWorld getBastionWorld(World world) {
		return bastionWorlds.get(world.getName());
	}
	
	public void deleteBastionWorld(World world) {
		bastionWorlds.remove(world.getName());
	}
	
	public boolean shouldStopBlock(Block origin, List<Block> blocks) {
		Set<FinancialEntity> owners = new HashSet<>();
		Set<Bastion> originBastions = getBastions(origin.getLocation());
		Set<Bastion> toBastions = getBastions(blocks);
		
		if (toBastions.isEmpty()) {
			return false;
		}
		
		for (Bastion originBastion : originBastions) {
			owners.add(originBastion.getReinforcement().getGroup().getOwner());
		}
		
		for (Bastion toBastion : toBastions) {
			FinancialEntity owner = toBastion.getReinforcement().getGroup().getOwner();
			if (!owners.contains(owner)) {
				return true;
			}
		}
		return false;
	}
	
	public Set<Bastion> getBastions(Location l) {
		BastionWorld bastionWorld = getBastionWorld(l.getWorld());
		return bastionWorld.getBastions(l);
	}
	
	public Set<Bastion> getBastions(List<Block> blocks) {
		Set<Bastion> result = new HashSet<>();
		
		for (Block b : blocks) {
			result.addAll(getBastions(b.getLocation()));
		}
		
		return result;
	}
	
	public Set<Bastion> getBastionsInArea(Location l1, Location l2) {
		if (!(l1.getWorld().getUID().equals(l2.getWorld().getUID()))) {
			return new HashSet<>();
		}
		
		Set<Bastion> bastions = new HashSet<>();
		
		World world = l1.getWorld();
		BastionWorld bastionWorld = getBastionWorld(world);
		for (Bastion bastion : bastionWorld.getAllBastions()) {
			if (LocationTools.isBetween(l1, l2, bastion.getLocation())) {
				bastions.add(bastion);
			}
		}
		
		return bastions;
	}
	
	// false = allow block
	// true = dont allow block
	public boolean placeBlock(CitadelPlayer cp, Reinforcement rein, ItemStack item) {
		BastionBlueprint bb = getBastionBlueprint(item);
		
		BastionPlacementEvent event = new BastionPlacementEvent(cp, rein, item, bb);
		Bukkit.getPluginManager().callEvent(event);
		
		bb = event.getBlueprint();
		
		if (bb == null) {
			return false;
		}
		
		cp.getBukkitPlayer().sendMessage(ChatColor.YELLOW + "You have placed a " + bb.getDisplayName());
		return placeBastion(rein, bb);
	}
	
	public boolean placeBastion(Reinforcement reinforcement, BastionBlueprint bb) {
		Bastion bastion = new Bastion(reinforcement, bb);
		
		World world = bastion.getReinforcement().getLocation().getWorld();
		BastionWorld bastionWorld = getBastionWorld(world);
		bastionWorld.addBastion(bastion);
		return false;
	}
	
	public void destroyBastion(Reinforcement reinforcement) {
		Location loc = reinforcement.getLocation();
		BastionWorld bastionWorld = getBastionWorld(loc.getWorld());
		if (bastionWorld == null) {
			return;
		}
		
		Set<Bastion> bastions = bastionWorld.getBastions(loc);
		if (bastions.isEmpty()) {
			return;
		}
		
		for (Bastion bastion : bastions) {
			if (bastion.getReinforcement().equals(reinforcement)) {
				destroyBastion(bastion);
				break;
			}
		}
	}
	
	public void destroyBastion(Bastion bastion) {
		World world = bastion.getLocation().getWorld();
		BastionWorld bastionWorld = getBastionWorld(world);
		bastionWorld.deleteBastion(bastion);
	}
	
}
