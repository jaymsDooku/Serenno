package io.jayms.serenno.util.worldedit;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.RegionFunction;

import io.jayms.serenno.SerennoCobalt;
import io.jayms.serenno.model.citadel.bastion.BastionBlueprint;
import io.jayms.serenno.model.citadel.bastion.BastionWorld;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;
import io.jayms.serenno.model.citadel.reinforcement.ReinforcementBlueprint;
import io.jayms.serenno.model.group.Group;

public class Bastionizer implements RegionFunction {
	
	private Player placer;
	private BastionWorld world;
	private ReinforcementBlueprint reinBlueprint;
	private BastionBlueprint blueprint;
	private Group group;
	
	public Bastionizer(Player placer, BastionWorld world, ReinforcementBlueprint reinBlueprint,
			BastionBlueprint blueprint, Group group) {
		this.placer = placer;
		this.world = world;
		this.reinBlueprint = reinBlueprint;
		this.blueprint = blueprint;
		this.group = group;
	}

	@Override
	public boolean apply(Vector position) throws WorldEditException {
		World world = this.world.getWorld();
		Block b = world.getBlockAt(position.getBlockX(), position.getBlockY(), position.getBlockZ());
		Reinforcement reinforcement = SerennoCobalt.get().getCitadelManager().getReinforcementManager().reinforceBlock(placer, b, null, reinBlueprint, group);
		if (reinforcement == null) {
			return true;
		}
		SerennoCobalt.get().getCitadelManager().getBastionManager().placeBastion(reinforcement, blueprint);
		return true;
	}

}
