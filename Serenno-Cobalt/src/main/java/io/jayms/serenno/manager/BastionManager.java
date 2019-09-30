package io.jayms.serenno.manager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Maps;

import io.jayms.serenno.kit.ItemStackKey;
import io.jayms.serenno.model.citadel.CitadelPlayer;
import io.jayms.serenno.model.citadel.bastion.Bastion;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionDataSource;
import io.jayms.serenno.model.citadel.bastion.BastionWorld;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.util.LocationTools;
import net.md_5.bungee.api.ChatColor;

public class BastionManager {

	private BastionDataSource dataSource;
	private ReinforcementManager rm;
	private Map<String, BastionWorld> bastionWorlds = Maps.newConcurrentMap();
	private Map<ItemStackKey, BastionBlueprint> bastionBlueprints = Maps.newConcurrentMap();
	
	public BastionManager(ReinforcementManager rm, BastionDataSource dataSource) {
		this.rm = rm;
		this.dataSource = dataSource;
	}
	
	public void registerBastionBlueprint(BastionBlueprint blueprint) {
		bastionBlueprints.put(new ItemStackKey(blueprint.getItemStack()), blueprint);
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
	
	public BastionWorld getBastionWorld(World world, BastionDataSource dataSource) {
		BastionWorld bastionWorld = bastionWorlds.get(world.getName());
		if (bastionWorld == null) {
			bastionWorld = new BastionWorld(world, dataSource);
			bastionWorlds.put(world.getName(), bastionWorld);
		}
		return bastionWorld;
	}
	
	public Set<Bastion> getBastions(Location l) {
		BastionWorld bastionWorld = getBastionWorld(l.getWorld(), dataSource);
		return bastionWorld.getBastions(l);
	}
	
	public Set<Bastion> getBastionsInArea(Location l1, Location l2) {
		if (!(l1.getWorld().getUID().equals(l2.getWorld().getUID()))) {
			return new HashSet<>();
		}
		
		Set<Bastion> bastions = new HashSet<>();
		
		World world = l1.getWorld();
		BastionWorld bastionWorld = getBastionWorld(world, dataSource);
		for (Bastion bastion : bastionWorld.getAllBastions()) {
			if (LocationTools.isBetween(l1, l2, bastion.getLocation())) {
				bastions.add(bastion);
			}
		}
		
		return bastions;
	}
	
	// false = allow block
	// true = dont allow block
	public boolean placeBlock(CitadelPlayer cp, Block b) {
		ItemStack it = cp.getBukkitPlayer().getInventory().getItemInMainHand();
		if (it == null) {
			return false;
		}
		
		BastionBlueprint bb = getBastionBlueprint(it);
		if (bb == null) {
			return false;
		}
		
		cp.getBukkitPlayer().sendMessage(ChatColor.YELLOW + "You have placed a " + bb.getDisplayName());
		return placeBastion(b, bb);
	}
	
	public boolean placeBastion(Block b, BastionBlueprint bb) {
		Reinforcement reinforcement = rm.getReinforcement(b);
		return placeBastion(reinforcement, bb);
	}
	
	public boolean placeBastion(Reinforcement reinforcement, BastionBlueprint bb) {
		Bastion bastion = new Bastion(reinforcement, bb);
		
		World world = bastion.getReinforcement().getLocation().getWorld();
		BastionWorld bastionWorld = getBastionWorld(world, dataSource);
		bastionWorld.addBastion(bastion);
		return false;
	}
	
	public void destroyBastion(Bastion bastion) {
		World world = bastion.getLocation().getWorld();
		BastionWorld bastionWorld = getBastionWorld(world, dataSource);
		bastionWorld.deleteBastion(bastion);
	}
	
}
