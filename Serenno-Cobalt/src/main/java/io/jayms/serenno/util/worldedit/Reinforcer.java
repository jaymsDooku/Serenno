package io.jayms.serenno.util.worldedit;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import io.jayms.serenno.model.group.Group;

public class Reinforcer implements RegionFunction {
	
	private Player placer;
	private ReinforcementWorld world;
	private ReinforcementBlueprint blueprint;
	private Group group;
	
	public Reinforcer(Player placer, ReinforcementWorld world, ReinforcementBlueprint blueprint, Group group) {
		this.placer = placer;
		this.world = world;
		this.blueprint = blueprint;
		this.group = group;
	}

	@Override
	public boolean apply(Vector position) throws WorldEditException {
		World world = this.world.getWorld();
		Block b = world.getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ());
		SerennoCobalt.get().getCitadelManager().getReinforcementManager().reinforceBlock(placer, b, blueprint, group);
		SerennoCobalt.get().getLogger().info("Reinforced:  " + b.getLocation());
		return true;
	}

}
