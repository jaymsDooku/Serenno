package io.jayms.serenno.model.citadel.bastion;

import io.jayms.serenno.model.citadel.reinforcement.ReinforcementWorld;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface BastionFieldLogic {

	Location getLocation(ReinforcementWorld world);
	
	boolean inField(ReinforcementWorld world, Block block);
	
	boolean inField(ReinforcementWorld world, Location loc);
	
}
