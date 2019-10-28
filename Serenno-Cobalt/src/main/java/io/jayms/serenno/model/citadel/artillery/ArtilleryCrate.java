package io.jayms.serenno.model.citadel.artillery;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.jayms.serenno.menu.Menu;
import io.jayms.serenno.model.citadel.reinforcement.Reinforcement;

public interface ArtilleryCrate {
	
	String getDisplayName();

	Artillery getArtillery();
	
	Location getLocation();
	
	void setReinforcement(Reinforcement rein);
	
	Reinforcement getReinforcement();
	
	boolean hasBeenPlaced();
	
	ItemStack getItemStack();
	
	Menu getInterface();
	
	boolean isAssembled();
	
}
