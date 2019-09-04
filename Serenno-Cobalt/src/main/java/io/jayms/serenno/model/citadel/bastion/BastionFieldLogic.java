package io.jayms.serenno.model.citadel.bastion;

import org.bukkit.Location;
import org.bukkit.block.Block;

public interface BastionFieldLogic {

	Location getLocation();
	
	boolean inField(Block block);
	
	boolean inField(Location loc);
	
}
